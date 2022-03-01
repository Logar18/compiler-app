package com.example.compiler;
import java.util.*;

public class Tree {
    private Node root;
    private Node curr;

    public Tree() {
        this.root = null;
        this.curr = new Node();
    } 

    public void addNode(String name, String kind) {
        Node newNode = new Node(name);

        if(this.root == null) {
            this.root = newNode;
        }
        else {
            newNode.setParent(this.curr);
            this.curr.addChild(newNode);
        }
        if(kind == "branch") {
            this.curr = newNode;
        }
    }

    public void endChildren() {
        if(this.curr.getParent() != null) {
            this.curr = this.curr.getParent();
        }
        else {
            System.out.println("Something went wrong");
        }
    }

    public String toString() {
        String result = expand(this.root, 0, "");
        return result;

    }

    public String expand(Node node, int depth, String result) {
        for(int i = 0; i < depth; i++) {
            result += "-";
        }
        if( node.getChildren().size() == 0) {
            result += "[" + node.getName() + "]";
            result += "\n";
        }
        else {
            result += "<" + node.getName() + ">";
            for(int i = 0; i< node.getChildren().size(); i++) {
                result = expand(node.getChildren().get(i), depth + 1, result);
            }
        }
        System.out.println("RESULT: " + result);
        return result;
    }


}

class Node {
    private String name;
    private Node parent;
    private List<Node> children;

    public Node() {
        this.name = null;
        this.parent = null;
        this.children = new ArrayList<>();
        }

    public Node(String name) {
        this.name = name;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return this.parent;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public List<Node> getChildren() { 
        return this.children;
    }

    public String getName() {
        return this.name;
    }
}
