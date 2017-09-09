package models;

import java.sql.Blob;

public class File {
    private int fileID;
    private String fileName;
    private Blob file;

    public Blob getFile() {
        return file;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileID() {
        return fileID;
    }


    public void setFile(Blob file) {
        this.file = file;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }
}