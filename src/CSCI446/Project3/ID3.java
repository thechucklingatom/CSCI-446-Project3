package CSCI446.Project3;

import java.util.List;

/**
 * Created by Alan Fraticelli on 11/11/2016.
 */
public class ID3 {

    public ID3(){

    }

    //if need be, this will go through the data and make descrete continuous data
    public void makeDescrete(){

    }

    //this is the actual recursive method to run ID3
    public void run(){

    }
}

class Tree {
    private Node root;
    private List<Node> children;

    public Tree(){

    }

    public void addChild(Node addNode){
        children.add(addNode);
    }

    public void removeChild(Node remNode){
        children.remove(remNode);
    }
}

class Node {
    //some sort of information to hold
    public Node(){

    }
}
