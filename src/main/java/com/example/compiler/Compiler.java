package com.example.compiler;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.*;
import java.io.*;

public class Compiler {
    public static void main(String[] args) throws Exception {

    }

    public static List<String> Compile(String filename, String mode) throws Exception {
        ArrayList<ArrayList<Character>> chars = new ArrayList<>();
        chars = generateCharsByFile(filename);
        Lexer L = new Lexer(mode);
        List<String> results = L.Scan(chars);
        return results;
    }

    public static List<String> CompileByString(String source, String mode) throws Exception {
        ArrayList<ArrayList<Character>> chars = new ArrayList<>();
        chars = generateCharsByString(source);
        Lexer L = new Lexer(mode);
        List<String> results = L.Scan(chars);
        return results;
    }

    //Used resources:
    //https://www.geeksforgeeks.org/different-ways-reading-text-file-java/
    //generates a 2d array of chars
    public static ArrayList<ArrayList<Character>> generateCharsByFile(String filename) throws Exception {
        ArrayList<ArrayList<Character>> chars = new ArrayList<ArrayList<Character>>();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        ArrayList<Character> newList = new ArrayList<>();
        chars.add(newList);
        String line = in.readLine();
        int lineNumber = 0;
        while (line != null) {
            for (int i = 0; i < line.length(); i++) {
                chars.get((lineNumber)).add(line.charAt(i));
            }
            line = in.readLine();
            lineNumber++;
            ArrayList<Character> temp = new ArrayList<>();
            chars.add(temp);
        }
        in.close();
        return chars;
    }

    public static ArrayList<ArrayList<Character>> generateCharsByString(String source) throws Exception {
        ArrayList<ArrayList<Character>> chars = new ArrayList<>();
        int line = 0;
        chars.add(new ArrayList<Character>());
        for(int i = 0; i<source.length(); i++) {
            if(source.charAt(i) == '\n') {
                System.out.println("NEW LINE");
//                chars.get(line).add(source.charAt(i));
                chars.add(new ArrayList<Character>());
                line++;
            }
            else {
                chars.get(line).add(source.charAt(i));
            }
        }
        System.out.println(chars);

        return chars;
    }

    public static void writeLogo() {
        System.out.println();
        System.out.println("Version 0.4.47 running on: " + System.getProperty("os.name") + "\n");

    }
}