import models.*;
import org.jasypt.util.text.BasicTextEncryptor;
import org.sql2o.Sql2o;
import repos.*;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    private static Visitor visitor = new Visitor();

    public static void main(String args[]) {
        //Assign port for Spark
        port(443);

        // SSL Certificate
        String keyStorePass = System.getenv("keyStorePass");
        secure("deploy/mykeystore.jks", keyStorePass, null, null);

        // Set these environment variables to connect to your database
        String jdbcURL = System.getenv("JDBC-URL");
        String jdbcUser = System.getenv("JDBC-USER");
        String jdbcPass = System.getenv("JDBC-PASS");
        String cryptoPass = System.getenv("cryptor");

        Sql2o sql2o = new Sql2o(jdbcURL, jdbcUser, jdbcPass);
        UserRepository userRepo = new UserRepository(sql2o, visitor);
        FileRepository fileRepo = new FileRepository(sql2o);


        // So Spark knows which folder (starting from "/resources") our static files are located in
        staticFileLocation("/public");

        // Sets the login information on each page load
        before((req, res) -> {
            //secure();  Fill out for SSL
            String username = req.session().attribute("username");
            User userData = userRepo.sessionLogin(username);
            req.session().attribute("userData", userData);
        });

        // API group - organizes all URLs for the API
        path("/api", () -> {
            post("/upload", (req, res) -> {
                MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
                req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
                Part uploadFile = req.raw().getPart("file");
                String userID = req.queryParams("userID");
                String fileName = req.raw().getPart("file").getSubmittedFileName();

                try (InputStream is = uploadFile.getInputStream()) {
                    fileRepo.uploadFile(userID, is, fileName);
                }

                return "API not yet implemented";
            });

            // Download file API
            get("/download", (req, res) -> {
                String fileID = req.queryParams("fileID");
                byte[] fileBuffer = fileRepo.download(fileID);
                String fileName = fileRepo.getFileName(fileID);
                res.raw().addHeader("Content-Disposition", "attachment; filename=" + fileName);
                OutputStream out = res.raw().getOutputStream();
                out.write(fileBuffer);
                return "";
            });


            // File removal API
            post("/delete", (req, res) -> {
                int fileID = Integer.parseInt(req.queryParams("fileID"));
                fileRepo.deleteFile(fileID);
                return "";
            });

            // Register Account API
            post("/register", (req, res) ->{
                String email = req.queryParams("email");
                String username = req.queryParams("username");

                BasicTextEncryptor cryptor = new BasicTextEncryptor();
                cryptor.setPassword(cryptoPass);
                String encryptedPassword = cryptor.encrypt(req.queryParams("password"));
                userRepo.createUser(username, email,encryptedPassword);
                User userData = userRepo.attemptLogin(username, encryptedPassword, cryptor); // Login after register
                if (userData != visitor) {
                    req.session().attribute("username", username);
                    res.redirect("/portal");
                } else {
                    res.redirect("/?loginError=true");
                }
                return "";
            });
        });

        // Home page
        get("/", (req, res) -> {
            String loginErrorParam = req.queryParams("loginError");
            String logoutParam = req.queryParams("logout");

            boolean loginError = (loginErrorParam != null) && loginErrorParam.equals("true");
            boolean logout = (logoutParam != null) && logoutParam.equals("true");

            Map<String, Object> model = new HashMap<>();
            model.put("userData", req.session().attribute("userData"));
            model.put("loginError", loginError);
            model.put("logout", logout);
            return new FreeMarkerEngine().render(
                    new ModelAndView(model, "homePage.ftl"));
        });

        // Login form result
        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            BasicTextEncryptor cryptor = new BasicTextEncryptor();
            cryptor.setPassword(cryptoPass);
            String encrypted = cryptor.encrypt(req.queryParams("password"));

            User userData = userRepo.attemptLogin(username, encrypted, cryptor);
            if (userData != visitor) {
                req.session().attribute("username", username);
                res.redirect("/portal");
            } else {
                res.redirect("/?loginError=true");
            }
            return null;
        }
        );

        // Logout function
        get("/logout", (req, res) -> {
            req.session().attribute("username", null);
            req.session().attribute("userData", visitor);
            res.redirect("/?logout=true");
            return null;
        });

        get("/register", (req, res) ->{
            Map<String, Object> model = new HashMap<>();
            User userData = req.session().attribute("userData");
            if(userData != visitor){
                // logged in user detected
                res.redirect("/portal");
            }
            return new FreeMarkerEngine().render(
                    new ModelAndView(model, "register.ftl"));
        });

        // User Portal page
        get("/portal", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            User userData = req.session().attribute("userData");
            if (userData == visitor) {
                halt(401);
                return "Not authorized";
            }
            String userID = Integer.toString(userData.getUserID());
            List<File> userFiles = fileRepo.getDownloadsForUser(userID);
            model.put("userFiles", userFiles);
            model.put("userID", userID);
            model.put("userData", req.session().attribute("userData"));
            return new FreeMarkerEngine().render(
                    new ModelAndView(model, "portal.ftl")
            );
        });
    }
}
