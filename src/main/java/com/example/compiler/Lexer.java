package com.example.compiler;
import java.util.*;
import java.util.regex.Pattern;


public class Lexer extends Logger {

    private String mode;
    // private String tokenList;

    public Lexer(String mode) {
        this.mode = mode;
    }

    public void Scan(ArrayList<ArrayList<Character>> chars) {
        //return value

        //temp data structures
        List<Alert> Errors = new ArrayList<Alert>();
        List<Alert> Warnings = new ArrayList<Alert>();
        List<Token> tokenList = new ArrayList<Token>();
        ArrayList<Character> scanner = new ArrayList<>();
        Parser p;

        //these are all temp variables to hold misc information as the lexer is scanning
        Token temp = new Token();
        boolean fString = false; boolean fComment = false;
        String str = ""; String type =""; 
        String foundType=""; String foundValue="";
        int currPos = 0; int foundPos = -1;
        int programCount = 1; int tempPos = 0;

        super.log("SYSTEM", "Lexer", "Lexing program #" + programCount, mode);

        for(int i=0; i<chars.size(); i++) {  //for each line
            ArrayList<Character> curr = chars.get(i);
            for(int j=0; j<curr.size(); j++) {  //for each character in curr line
                currPos = j;
                //while current char is within boundaries(space, symbol) build a string
                while(currPos < curr.size() && !Pattern.matches("\\{|\\}|\\(|\\)|\\=|!|\\|\\+|\"|\\+|\\$|/|\\ ", Character.toString(curr.get(currPos)))) {
                    str += curr.get(currPos);
                    type = Classify(str);
                    if(type != "NONE") {
                        foundType = type;
                        foundPos = currPos;
                        foundValue = str;
                        type = "NONE";
                    }
                    currPos++;
                }
                if(str != "") {  //if a string was built make a token of it and reset pointers
                    if(foundPos > -1) {
                        temp = new Token(foundValue, foundType, i+":"+foundPos);
                        super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                        tokenList.add(temp);
                        currPos = foundPos;
                        j = foundPos;
                    }
                    else {
                        super.log("ERROR", "Lexer", "Unrecognized token: [ " + str + " ] found at " + i + ":" + currPos, mode);
                        Errors.add(new Alert((i + ":" + currPos), "Unrecognized token: " + str));
                        j = currPos;
                    }
                    foundType="";
                    foundValue="";
                    foundPos = -1;
                }
                //if a string wasn't built, check the symbol
                if(currPos < curr.size() && Pattern.matches("\\{|\\}|\\(|\\)|\\=|!|\\|\\+|\"|\\+|\\$|/|\\ |\s", Character.toString(curr.get(currPos)))) {
                    //if current symbol could potentially be a double char symbol (boolean operators or comment)
                    if(curr.get(currPos) == '!' && curr.get(currPos+1) == '=') {
                            temp = new Token("!=", "BOOLOP_NE", i+":"+currPos);
                            super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                            tokenList.add(temp);
                            currPos++;
                    }
                    else if(curr.get(currPos) == '=' && curr.get(currPos+1) == '=') {
                            temp = new Token("==", "BOOLOP_E", i+":"+currPos);
                            super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                            tokenList.add(temp);
                            currPos++;
                    }
                    else if(curr.get(currPos) == '/' && curr.get(currPos+1) == '*') {
                        currPos = currPos + 2;
                        fComment = false;
                        for(int tempI=i; tempI<chars.size(); tempI++) {
                            if(fComment) {break;}
                            scanner = chars.get(tempI);
                            for(int tempJ=currPos; tempJ<scanner.size(); tempJ++) {
                                if(scanner.get(tempJ) == '*' && scanner.get(tempJ + 1) == '/') {
                                    fComment = true;
                                    tempJ++;
                                    fString = true;
                                    i++;
                                    if(tempJ + 1 > scanner.size()) {
                                        currPos = 0;
                                    }
                                    else {
                                        i = tempI;
                                        currPos = tempJ;
                                        curr = chars.get(i);
                                    }
                                    break;
                                }
                            }
                        }
                        if(!fComment) {
                            super.log("ERROR", "Lexer", "Expecting closing comment symbol [ */ ] from comment on on line: " + i, this.mode);
                            Errors.add(new Alert((i + ":" + currPos), "Expecting closing comment symbol [ */ ]"));
                        }
                        fComment = false;
                    }//end elseif (it's a open comment)

                    //if it's not a 2char symbol just classify as normal and if something is off classifier will catch it
                    else {
                        type = Classify(Character.toString(curr.get(currPos)));
                        //check for symbols that have special cases
                        if(type == "EOP") { //if EOP, parse
                            temp = new Token("$", "EOP", i+":"+currPos);
                            super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                            tokenList.add(temp);
                            if(Errors.size() > 0 || Warnings.size() > 0) {  //if there are errors, do not parse, log errors, and begin lex of next program.
                                super.log("SYSTEM", "Lexer", "Lexical Analyis FAILED with the following errors:", mode);
                                super.Dump(Errors, "Lexer", mode);
                            }
                            if(Warnings.size() > 0) {
                                super.log("SYSTEM", "Lexer", "Lexical Analyis issued the following warnings:", mode);
                                super.Dump(Warnings, "Lexer", mode);
                            }
                            if(Warnings.size() == 0 && Errors.size() == 0) {
                                super.log("SYSTEM", "Lexer", "Lexical Analysis Passed on program #" + programCount, mode);
                                p = new Parser(tokenList, mode);
                                if(mode != "TEST") {
                                    p.Parse();
                                }
                            }
                            tokenList.clear();
                            Errors.clear();
                            Warnings.clear();
                            programCount++;
                            if(i+1 >= chars.size()) {
                                super.log("SYSTEM", "Lexer", "Lexing program #" + programCount, mode);
                            }
                        }
                        else if(type == "D_QUOTE") {  //if D_QUOTE, build new string until closing D_QUOTE
                            temp = new Token(Character.toString(curr.get(currPos)), type, i+":"+currPos);
                            super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                            tokenList.add(temp); //adding initial quote symbol and moving up pointers
                            currPos++;
                            tempPos = currPos;
                            str = "";
                            fString = false;
                            for(int tempI=i; tempI<chars.size(); tempI++) {
                                if(fString) {break;}
                                scanner = chars.get(tempI);
                                for(int tempJ=tempPos; tempJ<scanner.size(); tempJ++) {
                                    if(scanner.get(tempJ) == '"') {  //add new string value and throw on the ending quote
                                        temp = new Token(str, "STRING_VALUE", tempI+":"+(tempJ-1));
                                        super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                                        tokenList.add(temp);
                                        temp = new Token(Character.toString('"'), "D_QUOTE", tempI+":"+tempJ);
                                        super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                                        tokenList.add(temp);

                                        //adjust pointers and change flags
                                        fString = true;
                                        i++;
                                        if(tempJ + 1 > scanner.size()) {
                                            currPos = 0;
                                        }
                                        else {
                                            i = tempI;
                                            currPos = tempJ;
                                            curr = chars.get(i);
                                        }
                                        break;
                                    }
                                    else if(scanner.get(tempJ) != ' ' && Pattern.matches("[^a-z]|", Character.toString(scanner.get(tempJ)))) {
                                        super.log("ERROR", "Lexer", "Invalid token in string literal: " + scanner.get(tempJ), this.mode);
                                        Errors.add(new Alert((tempI + ":" + tempJ), "Invalid token in string literal: " + scanner.get(tempJ)));
                                        str += scanner.get(tempJ);
                                    }
                                    else {
                                        str += scanner.get(tempJ); //validate character later (!!!!!!!!!)
                                    }
                                }
                                tempPos = 0; //sometimes the scan can pick up mid line so this resets tempJ at EOL
                            }
                            if(!fString) {
                                super.log("ERROR", "Lexer", "Expecting closing [ \" ] on line: " + i, this.mode);
                                Errors.add(new Alert((i + ":" + currPos), "Expecting closing [ \" ]"));
                            }
                            fString = false;
                        }
                        else if(type != "NONE") {
                            temp = new Token(Character.toString(curr.get(currPos)), type, i+":"+currPos);
                            super.log("DEBUG", "Lexer", temp.toString(), this.mode);
                            tokenList.add(temp);
                        }
                        else {
                            if(curr.get(currPos) != ' ') {
                                super.log("ERROR", "Lexer", "Error lexing: " + curr.get(currPos), mode);
                                Errors.add(new Alert((i + ":" + currPos), "Unrecognized token: " + curr.get(currPos)));
                            }
                        }
                    }//end else (general non string symbol classification)
                    j = currPos;
                }//end if (it was symbol)
                str = "";
            }//end for (char in line)
        }//end for (line)
        if(tokenList.size() > 0 && tokenList.get(tokenList.size()-1).getValue() != "$") {
            super.log("WARNING", "Lexer", "MISSING EOP TOKEN [ $ ]", mode);
            Warnings.add(new Alert(chars.size()+":"+currPos, "MISSING EOP TOKEN [ $ ]"));
            if(Errors.size() > 0) {
                super.log("SYSTEM", "Lexer", "Lexical analysis FAILED with the following errors:", mode);
                super.Dump(Errors, "Lexer", mode);
            }
            else {
                super.log("SYSTEM", "Lexer", "Lexical Analysis Passed on program #" + programCount, mode);
                temp = new Token("$", "EOP", chars.size()+":"+currPos);
                tokenList.add(temp);
                if(mode != "TEST") {
                    p = new Parser(tokenList, mode);
                    p.Parse();
                }
            }
        }
        if(chars.get(0).size() == 0) {
            super.log("WARNING", "Lexer", "No code was provided", mode);
            Warnings.add(new Alert(chars.size()+":"+currPos, "No code was provided"));
        }
    }//end Scan

    public String Classify(String token) {
            String classified = "";
            if(Pattern.matches("int", token)) {
                classified = "INT_TYPE";
            }
            else if(Pattern.matches("boolean", token)) {
                classified = "BOOL_TYPE";
            }
            else if(Pattern.matches("string", token)) {
                classified = "STRING_TYPE";
            }
            else if(Pattern.matches("^[a-z]$", token)) {
                classified = "ID";
            }
            else if(Pattern.matches("while", token)) {
                classified = "WHILE_KEYWORD";
            }
            else if(Pattern.matches("print", token)) {
                classified = "PRINT_KEYWORD";
            }
            else if(Pattern.matches("if", token)) {
                classified = "IF_KEYWORD";
            }
            else if(Pattern.matches("true", token)) {
                classified = "BOOL_TVAL";
            }
            else if(Pattern.matches("false", token)) {
                classified = "BOOL_FVAL";
            }
            else if(Pattern.matches("[0-9]", token)) {
                classified = "DIGIT";
            }
            else if(Pattern.matches("\\{", token)) { 
                classified = "OPEN_BLOCK";
            }
            else if(Pattern.matches("\\}", token)) {
                classified = "CLOSE_BLOCK";
            }
            else if(Pattern.matches("\\(", token)) {
                classified = "OPEN_PAREN";
            }
            else if(Pattern.matches("\\)", token)) {
                classified = "CLOSE_PAREN";
            }
            else if(Pattern.matches("\\$", token)) {
                classified = "EOP";
            }
            else if(Pattern.matches("\\+", token)) {
                classified = "INT_OP";
            }
            else if (Pattern.matches("\\=", token)) {
                classified = "ASSIGN";
            }
            else if(Pattern.matches(Character.toString('"'), token)) {
                classified = "D_QUOTE";
            }
            else {
                classified = "NONE";
            }
            return classified;
        }
}