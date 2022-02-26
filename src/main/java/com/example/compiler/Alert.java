package com.example.compiler;
public class Alert {

    private String message;
    private String location;

    public Alert(String location, String message) {
        this.message = message;
        this.location = location;
    }

    public String toString() {
        return(this.message + " found at " + this.location);
    }


}
