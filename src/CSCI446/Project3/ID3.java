package CSCI446.Project3;

import java.io.Writer;
import java.util.List;

/**
 * Created by Alan Fraticelli on 11/11/2016.
 */
public class ID3 {
    private int foldToWrite;
    private Writer writer;
    private DataContainer container;
    private int currentFold;
    private int testingFold;
    private Tree tree;


    public ID3(Writer inWriter, DataContainer inContainer){
        this.writer = inWriter;
        this.container = inContainer;
        tree = new Tree();

    }

    //if need be, this will go through the data and make descrete continuous data
    public void makeDescrete(){
        container.
    }

    //this is the actual recursive method to run ID3
    public void id3(){

    }

    public void tenFold(){

    }
}