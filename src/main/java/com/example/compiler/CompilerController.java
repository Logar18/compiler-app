package com.example.compiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping(path="/compile")
public class CompilerController extends Logger {

    @CrossOrigin
    @GetMapping("/")
    public List<List<String>> HelloWorld(@RequestParam String source) throws Exception {
        List<List<String>> results = new ArrayList<>();
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());

        Compiler c = new Compiler();
        try {
            c.CompileByString(source, "VERBOSE");
            List<String> logs = super.getLogs();
            for(String s: logs) {
                if(Pattern.matches("*Lexer*", s)) {
                    System.out.println(s);
                    results.get(0).add(s);
                } 
                else if(Pattern.matches("*Parser*", s)) {
                    results.get(1).add(s);
                }
                else {
                    results.get(2).add(s);
                }
            }
            System.out.println(results);
            return results;
        } catch (Exception e) {
            c.CompileByString(source, "VERBOSE");
            List<String> logs = super.getLogs();
            for(String s: logs) {
                if(s.contains("Lexer")) {
                    results.get(0).add(s);
                } 
                else if(s.contains("Parser")) {
                    results.get(1).add(s);
                }
                else {
                    results.get(2).add(s);
                }
            }
            System.out.println(results);
            return results;
            
        }

    }
}
