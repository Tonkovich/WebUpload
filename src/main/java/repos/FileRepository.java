package repos;

import models.File;
import org.sql2o.*;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class FileRepository {

    private Sql2o sql2o;

    public FileRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void uploadFile(String UserID, InputStream is, String fileName) {
        String uploadSQL = "INSERT INTO Files (user_id, file, file_name) VALUES (:id,:file,:file_name)";
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery(uploadSQL)
                    .addParameter("id", UserID)
                    .addParameter("file", is)
                    .addParameter("file_name", fileName)
                    .executeUpdate();
        }

    }

    // Works but is ugly
    public byte[] download(String downloadID) {
        String downloadSQL = "SELECT file FROM Files WHERE file_id = ?";
        try {
            String jdbcUser = System.getenv("JDBC-USER");
            String jdbcPass = System.getenv("JDBC-PASS");
            String jdbcURL = System.getenv("JDBC-URL");

            Class.forName("com.mysql.jdbc.Driver");

            if (jdbcURL == null) {
                jdbcURL = "jdbc:mysql://localhost:3306/test";
            }

            if (jdbcUser != null && jdbcPass != null) {
                jdbcURL += "?user=" + jdbcUser + "&password=" + jdbcPass;
            }
            Connection conn = DriverManager.getConnection(jdbcURL);
            PreparedStatement getFiles = conn.prepareStatement(downloadSQL);
            getFiles.setString(1, downloadID);
            ResultSet rs = getFiles.executeQuery();
            rs.next();
            Blob blob = rs.getBlob(1);
            int blobLength = (int) blob.length();
            byte[] blobAsBytes = blob.getBytes(1, blobLength);
            blob.free();
            conn.close();
            return blobAsBytes;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<File> getDownloadsForUser(String userID) {
        String uploadSQL = "SELECT file_id, file_name FROM Files WHERE user_id = :id";
        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(uploadSQL)
                    .addParameter("id", userID)
                    .addColumnMapping("file_id", "fileID")
                    .addColumnMapping("file_name", "fileName")
                    .executeAndFetch(File.class);
        }
    }

    public String getFileName(String fileID) {
        String nameSQL = "SELECT file_name FROM Files WHERE file_id = :file_id";
        try (org.sql2o.Connection con = sql2o.open()) {
            File file = con.createQuery(nameSQL)
                    .addParameter("file_id", fileID)
                    .addColumnMapping("file_name", "fileName")
                    .executeAndFetchFirst(File.class);
            return file.getFileName();
        }
    }

    public void deleteFile(int fileID) {
        String assQuery = "DELETE FROM Files WHERE file_id = :fileID";
        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery(assQuery)
                    .addParameter("fileID", fileID)
                    .executeUpdate();
        }
    }
}