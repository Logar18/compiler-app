package com.example.compiler;
public class Token extends Logger{
    private String value;
    private String type;
    private String location;

    public Token(String value, String type, String location) { 
        this.value = value;
        this.type = type;
        this.location = location;
    }

    public Token(String value, String location) {
        this.value = value;
        this.location = location;
    }

    public Token(){}
    
    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }

    public String getLocation() {
        return this.location;
    }

    public String toString() { 
        return(this.type + " [ " + this.value + " ] found at " + this.location);
    }
}
