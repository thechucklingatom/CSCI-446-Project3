package CSCI446.Project3;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Alan Fraticelli on 11/11/2016.
 */
public class ID3 {
    private int foldToWrite;
    private Writer writer;
    private DataContainer container;
    private int currentFold;
    private List<List<String>> trainingSet;
    private int testingFold;
    private Tree tree;
    private List<List<String>> binAtt;

    public ID3(Writer inWriter, DataContainer inContainer){
        this.writer = inWriter;
        this.container = inContainer;
        tree = new Tree();
        binAtt = new ArrayList<>();
    }

    //takes in a transposed training set (to make rows  attributes) and bin it
    public void discretize(List<List<String>> inFold){
        //iterationg through the attributes
        for(int i = 0; i < inFold.size(); i++){
            //we are going to determine the size of unique data of each attribute,
            //and bin the attributes with a max of 5 unique data
            int numBin = findNumBin(inFold.get(i));
            bin(inFold.get(i), numBin);
        }
    }

    public int findNumBin(List<String> inFold) {
        int answer = 0;
        HashSet noDupes = new HashSet();
        for (int j = 0; j < inFold.size(); j++) {
            noDupes.add(inFold.get(j));
        }
        if (noDupes.size() <= 5){
            return answer;
        } else {
            return 6;
        }
    }

    public void bin(List<String> inFold, int numBin){
        if(numBin <= 5){

        } else {
            int max = findMax(inFold);
            int min = findMin(inFold);
            int range = max - min;
            float binRange = range / 6;
            float lowDiv = min;
            float nextDiv = lowDiv + binRange;
            List<Bin>

            Bin bin1, bin2, bin3, bin4, bin5, bin6;
            bin1 = new Bin(lowDiv, nextDiv, 1);
            lowDiv = nextDiv;
            nextDiv = nextDiv + binRange;
            bin2 = new Bin(lowDiv, nextDiv, 2);
            lowDiv = nextDiv;
            nextDiv = nextDiv + binRange;
            bin3 = new Bin(lowDiv, nextDiv, 3);
            lowDiv = nextDiv;
            nextDiv = nextDiv + binRange;
            bin4 = new Bin(lowDiv, nextDiv, 4);
            lowDiv = nextDiv;
            nextDiv = nextDiv + binRange;
            bin5 = new Bin(lowDiv, nextDiv, 5);
            lowDiv = nextDiv;
            nextDiv = nextDiv + binRange;
            bin6 = new Bin(lowDiv, nextDiv, 6);

            for(int i = 0; i < inFold.size(); i++){

            }
        }
    }

    public int findMax(List<String> inAtt){
        int answer = -1000000000;
        for(int i = 0; i < inAtt.size(); i++){
            int curNum = Integer.getInteger(inAtt.get(i));
            if(curNum > answer){
                answer = curNum;
            }
        }
        return answer;
    }

    public int findMin(List<String> inAtt){
        int answer = 1000000000;
        for(int i = 0; i < inAtt.size(); i++){
            int curNum = Integer.getInteger(inAtt.get(i));
            if(curNum < answer){
                answer = curNum;
            }
        }
        return answer;
    }

    //this is the actual recursive method to run ID3
    public void id3(){

    }

    public void tenFold(){

    }
}