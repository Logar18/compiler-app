package com.example.compiler;
import java.util.*;

public class Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final String OS = System.getProperty("os.name").toLowerCase();
    public static final List<String> logs = new ArrayList<>();

    public void log(String type, String source, String message, String mode) {
        if(OS.charAt(0) != 'w') {
            switch(type) {
                case("DEBUG"):
                    if(mode == "VERBOSE") {
                        System.out.println(ANSI_GREEN + "[DEBUG] "  + ANSI_RESET + source + " - " + message);
                    }
                    break;
                case("ERROR"):
                    System.out.println(ANSI_RED + "[ERROR] " + ANSI_RESET + source + " - " + message);
                    break;
                case("SYSTEM"):
                    System.out.println(ANSI_CYAN + "[SYSTEM] " + ANSI_RESET + source + " - " + message);
                    break;
                case("WARNING"):
                    System.out.println(ANSI_YELLOW + "[WARNING] " + ANSI_RESET + source + " - " + message);
                    break;
                default:
                    System.out.println(type + " " + source + " - " + message);
                    break;

            }
        }
        else {
            switch(type) {
                case("DEBUG"):
                    if(mode == "VERBOSE") {
                        System.out.println("[DEBUG] "+ source + " - " + message);
                        logs.add("[DEBUG] "+ source + " - " + message);
                    }
                    break;
                case("ERROR"):
                    System.out.println("[ERROR] " + source + " - " + message);
                    logs.add("[ERROR] "+ source + " - " + message);
                    break;
                case("SYSTEM"):
                    System.out.println("[SYSTEM] " + source + " - " + message);
                    logs.add("[SYSTEM] "+ source + " - " + message);
                    break;
                case("WARNING"):
                    System.out.println("[WARNING] " + source + " - " + message);
                    logs.add("[WARNING] "+ source + " - " + message);
                    break;
                default:
                    System.out.println(type + " " + source + " - " + message);
                    logs.add(type + " " + source + " - " + message);
                    break;
            }
        }

    }

    public List<String> getLogs() {
        List<String> tempLogs = new ArrayList<>(logs);
        logs.clear();
        return tempLogs;
    }

    public void Dump(List<Alert> alerts, String source, String mode) {
        System.out.println("-----");
        for(Alert a: alerts) {
            System.out.print("   ");
            log("ERROR", source, a.toString(), "VERBOSE");
        }
        System.out.println("-----");
    }
}