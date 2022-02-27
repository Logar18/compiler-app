package com.example.compiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping(path="/compile")
public class CompilerController extends Logger {

    @CrossOrigin
    @GetMapping("/")
    public List<String> HelloWorld(@RequestParam String source) throws Exception {
        Compiler c = new Compiler();
        try {
            c.CompileByString(source, "VERBOSE");
            return super.getLogs();
        } catch (Exception e) {
            return super.getLogs();
        }

    }
}
