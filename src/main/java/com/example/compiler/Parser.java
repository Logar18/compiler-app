package com.example.compiler;

import java.util.*;

public class Parser extends Logger{
    
    private String mode;
    private List<Token> stream;
    private int i = 0;
    private Set<String> types = new HashSet<String>();
    private Set<String> boolValues = new HashSet<String>();

    public Parser(List<Token> stream, String mode) {
        this.mode = mode;
        this.stream = stream;
        this.types.add("STRING_TYPE");
        this.types.add("BOOL_TYPE");
        this.types.add("INT_TYPE");
        this.boolValues.add("BOOL_FVAL");
        this.boolValues.add("BOOL_TVAL");
    }

    public Parser(String mode) {
        this.mode = mode;
        this.stream =  new ArrayList<Token>();
    }

    //In recursive-descent parsing, each nonterminal in the grammar has an associated
    // parsing procedure that is responsible for determining if the token stream contains
    // a sequence of tokens derivable from that nonterminal.

    public void setStream(List<Token> stream) {
        this.stream = stream;
    }

    public void Parse() {
        System.out.println(this.stream);
        ParseProgram();
        this.i = 0;
        this.stream.clear();
    }

    public void ParseProgram() {
        super.log("DEBUG", "Parser", "PARSING Program...", mode);
        ParseBlock();
        match("EOP");
    }

    public void ParseBlock() {
        super.log("DEBUG", "Parser", "PARSING Block...", mode);
        match("OPEN_BLOCK");
        ParseStatementList();
        match("CLOSE_BLOCK");
    }

    public void ParseStatementList() {
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING StatementList... i = " + this.i, mode);
            if(stream.get(this.i).getType() != "CLOSE_BLOCK") { //if it's the start of an expression
                ParseStatement();
                ParseStatementList();
            }
            else {
                //do nothing, its an empty string transition
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a STATEMENT LIST", mode);
        }
    }

    public void ParseStatement() {
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING Statement...", mode);
            String currType = stream.get(this.i).getType();
            if(currType == "PRINT_KEYWORD") {
                ParsePrintStatement();
            }
            else if(currType == "ID") {
                ParseAssignmentStatement();
            }
            else if(this.types.contains(currType)) {
                ParseVarDeclr();
            }
            else if(currType == "WHILE_KEYWORD") {
                ParseWhileStatement();
            }
            else if(currType == "IF_KEYWORD") {
                ParseIfStatement();
            }
            else if(currType == "OPEN_BLOCK") {
                ParseBlock();
            }
            else {
                super.log("ERROR", "Parser", "Expected a statement token, recieved [ " + currType + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a STATEMENT", mode);
        }
        
    }

    public void ParsePrintStatement() {
        super.log("DEBUG", "Parser", "PARSING PrintStatement...", mode);
        match("PRINT_KEYWORD");
        match("OPEN_PAREN");
        ParseExpr();
        match("CLOSE_PAREN");
    }

    public void ParseAssignmentStatement() {
        super.log("DEBUG", "Parser", "PARSING AssignmentStatement...", mode);
        match("ID");
        match("ASSIGN");
        ParseExpr();
    }

    public void ParseVarDeclr() {
        super.log("DEBUG", "Parser", "PARSING VarDecl...", mode);
        match(stream.get(this.i).getType());
        match("ID");
    }

    public void ParseWhileStatement() {
        super.log("DEBUG", "Parser", "PARSING WhileStatement...", mode);
        match("WHILE_KEYWORD");
        ParseBooleanExpr();
        ParseBlock();
    }

    public void ParseIfStatement() {
        super.log("DEBUG", "Parser", "PARSING IfStatement...", mode);
        match("IF_KEYWORD");
        ParseBooleanExpr();
        ParseBlock();
    }

    public void ParseExpr() {
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING Expr...", mode);
            String currType = this.stream.get(this.i).getType();
            if(currType == "D_QUOTE") {
                ParseStringExpr();
            }
            else if(currType == "DIGIT") {
                ParseIntExpr();
            }
            else if(currType == "OPEN_PAREN") {
                match("OPEN_PAREN");
                ParseBooleanExpr();
            }
            else if(currType == "ID") {
                if(this.stream.get(this.i+1).getType() == "INT_OP") {
                    super.log("ERROR", "Parser", "Expected a DIGIT token, recieved [ " + currType + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                }
                match("ID");
            }
            else if(this.boolValues.contains(currType)) {
                ParseBoolVal();
            }
            else {
                super.log("ERROR", "Parser", "Expected an Expression token, recieved [ " + currType + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse an EXPR", mode);
        }
    }

    public void ParseBoolVal() {
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING boolval...", mode);
            if(this.stream.get(this.i).getType() == "BOOL_FVAL") {
                match("BOOL_FVAL");
            }
            else if(this.stream.get(this.i).getType() == "BOOL_TVAL") {
                match("BOOL_TVAL");
            }
            else {
                super.log("ERROR", "Parser", "Expected a boolean value, recieved [ " + this.stream.get(this.i).getType() + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a BOOLEAN VALUE", mode);
        }
    }

    public void ParseBooleanExpr() {
        super.log("DEBUG", "Parser", "PARSING BooleanExpr...", mode);
        match("OPEN_PAREN");
        ParseExpr();
        ParseBoolop();
        ParseExpr();
        match("CLOSE_PAREN");
    }

    public void ParseIntExpr() {
        super.log("DEBUG", "Parser", "PARSING IntExpr...", mode);
        match("DIGIT");
        if(this.i < this.stream.size()) {
            if(stream.get(this.i).getType() == "INT_OP") {
                match("INT_OP");
                ParseExpr();
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse an INT_OP", mode);
        }

    }

    public void ParseCharList() {
        super.log("DEBUG", "Parser", "PARSING CharList...", mode);
        match("STRING_VALUE");
    }

    public void ParseStringExpr() {
        super.log("DEBUG", "Parser", "PARSING StringExpr...", mode);
        match("D_QUOTE");
        ParseCharList();
        match("D_QUOTE");
    }

    public void ParseBoolop() {
        super.log("DEBUG", "Parser", "PARSING boolop...", mode);
        if(this.i < this.stream.size()) {
            if(stream.get(this.i).getType() == "BOOLOP_E") {
                match("BOOLOP_E");
            }
            else if(stream.get(this.i).getType() == "BOOLOP_NE"){
                match("BOOLOP_NE");
            }
            else {
                super.log("ERROR", "Parser", "Expected a boolean operator, recieved [ " + this.stream.get(this.i).getType() + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a BOOLEAN OPERATOR", mode);
        }
    }
    
    public void match(String expectedTokens) {
        if(this.i < this.stream.size()) {
            if(stream.get(i).getType() == expectedTokens) {
                super.log("DEBUG", "Parser", "MATCH FOUND: Consumed expected token of " + stream.get(this.i).getType() + " [ i = " + this.i + " ] ", mode);
                this.i++;
            }
            else {
                super.log("ERROR", "Parser", "Expected " + expectedTokens + " but got [ " + this.stream.get(this.i).getType() + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to match for: [ " + expectedTokens + " ]", mode);
        }

    }

}


//https://www.geeksforgeeks.org/dfs-traversal-of-a-tree-using-recursion/
