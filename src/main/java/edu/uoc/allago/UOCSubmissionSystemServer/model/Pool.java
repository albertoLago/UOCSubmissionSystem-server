package edu.uoc.allago.UOCSubmissionSystemServer.model;

import java.util.List;

public class Pool {
    private String id;
    private String path;
    private String date;
    private boolean active;
    private List<String> files;


    public Pool() {
    }

    public Pool(String id, String path, String date, List<String> files, boolean active) {
        this.id = id;
        this.path = path;
        this.date = date;
        this.files = files;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public boolean getActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
