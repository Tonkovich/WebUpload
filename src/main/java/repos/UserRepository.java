package repos;

import models.Account;
import models.User;
import models.Visitor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class UserRepository {
    private Sql2o sql2o;
    private Visitor visitor;

    public UserRepository(Sql2o sql2o, Visitor visitor) {
        this.sql2o = sql2o;
        this.visitor = visitor;
    }

    public User attemptLogin(String username, String password, BasicTextEncryptor cryptor) {
        String user_sql = "SELECT id, username " +
                "FROM users " +
                "WHERE username = :username";
        if(decryptPassAndCheck(password, username, cryptor) == true){
            // Password match
        } else {
            // Password incorrect
            return visitor;
        }
        try(Connection con = sql2o.open()) {
            if (this.accountExists(username)) {
                List<Account> userList = con.createQuery(user_sql)
                        .addParameter("username", username)
                        .addColumnMapping("id", "userID")
                        .addColumnMapping("username", "userName")
                        .executeAndFetch(Account.class);
                if (userList.isEmpty()) {
                    return visitor;
                }
                return userList.get(0);

            } else {
                return visitor;
            }
        }
    }
    // Create session for user upon login
    public User sessionLogin(String username) {
        String student_sql = "SELECT id ,username " +
                "FROM Users " +
                "WHERE username = :username";

        if (username == null) {
            return visitor;
        }

        try(Connection con = sql2o.open()) {
            if (this.accountExists(username)) {
                List<Account> userList = con.createQuery(student_sql)
                        .addParameter("username", username)
                        .addColumnMapping("id", "userID")
                        .executeAndFetch(Account.class);
                if (userList.isEmpty()) {
                    return visitor;
                }
                return userList.get(0);

            }else {
                return visitor;
            }
        }
    }

    private boolean accountExists(String username) {
        String sql = "SELECT count(*) FROM Users WHERE username = :username";
        try(Connection con = sql2o.open()) {
            int count = con.createQuery(sql)
                    .addParameter("username", username)
                    .executeScalar(Integer.class);
            return count > 0;
        }
    }

    public void createUser(String username, String email, String password){
        String sql = "INSERT INTO Users(username, email, password) VALUES (:username, :email, :password)";
        try(Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("username", username)
                    .addParameter("email", email)
                    .addParameter("password", password)
                    .executeUpdate();
        }
    }
    public boolean decryptPassAndCheck(String encryptedIncoming, String username, BasicTextEncryptor crypto){
        String sql = "SELECT password FROM users WHERE username = :username";
        try(Connection con = sql2o.open()) {
            String encryptedSQLPass = con.createQuery(sql)
                    .addParameter("username", username)
                    .executeScalar().toString();
            if(crypto.decrypt(encryptedSQLPass).equals(crypto.decrypt(encryptedIncoming))){
                return true;
            }
        }
        return false;
    }
}