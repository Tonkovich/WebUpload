package models;

public class Visitor implements User {
    public int getUserID() {
        return 0;
    }

    public String getFirstName() {
        return null;
    }

    public String getLastName() {
        return null;
    }

    public String getUserName() {
        return null;
    }

    public boolean isLoggedIn() {
        return false;
    }

    public boolean isAdmin() {
        return false;
    }
}