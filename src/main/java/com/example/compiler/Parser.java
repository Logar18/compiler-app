package com.example.compiler;

import java.util.*;
import java.util.regex.Pattern;

public class Parser extends Logger{
    
    private String mode;
    private List<Token> stream;
    private int i = 0;
    private Set<String> types = new HashSet<String>();
    private Set<String> boolValues = new HashSet<String>();
    private Tree CST = new Tree();

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
        ParseProgram();
        System.out.println("CST STRING:" + this.CST.toString());
        System.out.println(this.CST);
        this.i = 0;
        this.stream.clear();
    }

    public void ParseProgram() {
        this.CST.addNode("Program", "root");
        super.log("DEBUG", "Parser", "PARSING Program...", mode);
        ParseBlock();
        match("EOP");
        // this.CST.endChildren();
    }

    public void ParseBlock() {
        this.CST.addNode("Block", "branch");
        super.log("DEBUG", "Parser", "PARSING Block...", mode);
        match("OPEN_BLOCK");
        ParseStatementList();
        match("CLOSE_BLOCK");
        this.CST.endChildren();
    }

    public void ParseStatementList() {
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING StatementList... i = " + this.i, mode);
            if(currType() != "CLOSE_BLOCK") { //if it's the start of an expression
                this.CST.addNode("StatementList", "branch");
                ParseStatement();
                ParseStatementList();
                this.CST.endChildren();
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
        this.CST.addNode("Statement", "branch");
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING Statement...", mode);
            String currType = currType();
            if(currType == "PRINT_KEYWORD") {
                ParsePrintStatement();
                this.CST.endChildren();
            }
            else if(currType == "ID") {
                ParseAssignmentStatement();
                this.CST.endChildren();
            }
            else if(this.types.contains(currType)) {
                ParseVarDeclr();
                this.CST.endChildren();
            }
            else if(currType == "WHILE_KEYWORD") {
                ParseWhileStatement();
                this.CST.endChildren();
            }
            else if(currType == "IF_KEYWORD") {
                ParseIfStatement();
                this.CST.endChildren();
            }
            else if(currType == "OPEN_BLOCK") {
                ParseBlock();
                this.CST.endChildren();

            }
            else {
                super.log("ERROR", "Parser", "Expected start of a STATEMENT token, recieved [ " + currType + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a STATEMENT", mode);
            this.i++;
        }  
    }

    public void ParsePrintStatement() {
        this.CST.addNode("PrintStatement", "branch");
        super.log("DEBUG", "Parser", "PARSING PrintStatement...", mode);
        match("PRINT_KEYWORD");
        match("OPEN_PAREN");
        ParseExpr();
        match("CLOSE_PAREN");
        this.CST.endChildren();
    }

    public void ParseAssignmentStatement() {
        this.CST.addNode("AssignmentStatement", "branch");
        super.log("DEBUG", "Parser", "PARSING AssignmentStatement...", mode);
        match("ID");
        match("ASSIGN");
        ParseExpr();
        this.CST.endChildren();
    }

    public void ParseVarDeclr() {
        this.CST.addNode("VarDeclr", "branch");
        super.log("DEBUG", "Parser", "PARSING VarDecl...", mode);
        match(currType());
        match("ID");
        this.CST.endChildren();
    }

    public void ParseWhileStatement() {
        this.CST.addNode("WhileStatement", "branch");
        super.log("DEBUG", "Parser", "PARSING WhileStatement...", mode);
        match("WHILE_KEYWORD");
        ParseBooleanExpr();
        ParseBlock();
        this.CST.endChildren();
    }

    public void ParseIfStatement() {
        this.CST.addNode("IfStatement", "branch");
        super.log("DEBUG", "Parser", "PARSING IfStatement...", mode);
        match("IF_KEYWORD");
        ParseBooleanExpr();
        ParseBlock();
        this.CST.endChildren();
    }
    

    public void ParseExpr() {
        this.CST.addNode("Expr", "branch");
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING Expr...", mode);
            String currType = currType();
            if(currType == "D_QUOTE") {
                ParseStringExpr();
                this.CST.endChildren();
            }
            else if(currType == "DIGIT") {
                ParseIntExpr();
                this.CST.endChildren();
            }
            else if(currType == "OPEN_PAREN") {
                ParseBooleanExpr();
                this.CST.endChildren();
            }
            else if(currType == "ID") {
                if(nextFrom(this.i) == "INT_OP") {
                    super.log("ERROR", "Parser", "Expected a DIGIT token, recieved [ " + currType + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                    this.i++;
                    match("INT_OP");
                    match("ID");
                    this.CST.endChildren();
                }
                else {
                    match("ID");
                    this.CST.endChildren();
                }
            }
            else if(this.boolValues.contains(currType)) {
                ParseBoolVal();
                this.CST.endChildren();
            }
            else {
                super.log("ERROR", "Parser", "Expected an Expression token, recieved [ " + currType + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse an EXPR", mode);
            this.i++;
        }
    }

    public void ParseBoolVal() {
        this.CST.addNode("BoolVal", "branch");
        if(this.i < this.stream.size()) {
            super.log("DEBUG", "Parser", "PARSING boolval...", mode);
            if(currType() == "BOOL_FVAL") {
                match("BOOL_FVAL");
            }
            else if(currType() == "BOOL_TVAL") {
                match("BOOL_TVAL");
            }
            else {
                super.log("ERROR", "Parser", "Expected a boolean value, recieved [ " + currType() + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a BOOLEAN VALUE", mode);
            this.i++;
        }
        this.CST.endChildren();
    }

    public void ParseBooleanExpr() {
        this.CST.addNode("BooleanExpr", "branch");
        super.log("DEBUG", "Parser", "PARSING BooleanExpr...", mode);
        match("OPEN_PAREN");
        ParseExpr();
        ParseBoolop();
        ParseExpr();
        match("CLOSE_PAREN");
        this.CST.endChildren();
    }

    public void ParseIntExpr() {
        this.CST.addNode("IntExpr", "branch");
        super.log("DEBUG", "Parser", "PARSING IntExpr...", mode);
        match("DIGIT");
        if(this.i < this.stream.size()) {
            if(currType() == "INT_OP") {
                match("INT_OP");
                ParseExpr();
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse an INT_OP", mode);
            this.i++;
            ParseExpr();
        }
        this.CST.endChildren();

    }

    public void ParseCharList() {
        this.CST.addNode("CharList", "branch");
        super.log("DEBUG", "Parser", "PARSING CharList...", mode);
        match("STRING_VALUE");
        this.CST.endChildren();
    }

    public void ParseStringExpr() {
        this.CST.addNode("StringExpr", "branch");
        super.log("DEBUG", "Parser", "PARSING StringExpr...", mode);
        match("D_QUOTE");
        ParseCharList();
        match("D_QUOTE");
        this.CST.endChildren();
    }

    public void ParseBoolop() {
        this.CST.addNode("Boolop", "branch");
        super.log("DEBUG", "Parser", "PARSING boolop...", mode);
        if(this.i < this.stream.size()) {
            if(currType() == "BOOLOP_E") {
                match("BOOLOP_E");
            }
            else if(currType() == "BOOLOP_NE"){
                match("BOOLOP_NE");
            }
            else {
                super.log("ERROR", "Parser", "Expected a boolean operator, recieved [ " + currType() + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to parse a BOOLEAN OPERATOR", mode);
            this.i++;
        }
        this.CST.endChildren();
    }
    
    public void match(String expectedTokens) {
        if(this.i < this.stream.size()) {
            if(currType() == expectedTokens) {
                this.CST.addNode(currType(), "leaf");
                super.log("DEBUG", "Parser", "MATCH FOUND: Consumed expected token of " + currType() + " [ i = " + this.i + " ] ", mode);
                this.i++;
            }
            else {
                super.log("ERROR", "Parser", "Expected " + expectedTokens + " but got [ " + currType() + " ] " + "at " + this.stream.get(this.i).getLocation(), mode);
                this.i++;
            }
        }
        else {
            super.log("ERROR", "Parser", "Unexpected parse error attempting to zmatch for: [ " + expectedTokens + " ]", mode);
        }

    }

    public String nextFrom(int i) {
        return this.stream.get(this.i+1).getType();
    }

    public String currType() {
        return this.stream.get(this.i).getType();
    }

}


//https://www.geeksforgeeks.org/dfs-traversal-of-a-tree-using-recursion/
