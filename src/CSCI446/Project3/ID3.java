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
    private List<List<Bin>> binAtt;
    private final int MAX_NUM_BINS = 6;

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
            if(!inFold.get(j).equals("?")) {
                noDupes.add(inFold.get(j));
            }
        }
        if (noDupes.size() <= 5){
            return answer;
        } else {
            return 6;
        }
    }

    public void bin(List<String> inAtt, int numBin){
        if(numBin <= 5){
            List<Bin> bins = new ArrayList<>();
            binAtt.add(bins);
            //obtain a list of the unique values in this attribute
            List<String> uniqueValue = new ArrayList<>();
            //create the unique doubles that will bound our bins
            List<Double> stringHash = new ArrayList<>();
            for(String s : uniqueValue) {
                stringHash.add((double) s.hashCode());
            } //create the bins
            int i;
            bins.add(new Bin(Double.MIN_VALUE, stringHash.get(0), -1));
            for(i = 0; i <= stringHash.size(); i++){
                bins.add(new Bin(stringHash.get(i), stringHash.get(i+1), i));
            }
            bins.add(new Bin(stringHash.get(i), Double.MAX_VALUE, i));
            //fill the bins

        } else {
            int max = findMax(inAtt);
            int min = findMin(inAtt);
            int range = max - min;
            float binRange = range / MAX_NUM_BINS;
            float lowDiv = min;
            float nextDiv = lowDiv + binRange;
            List<Bin> bins = new ArrayList<>();
            binAtt.add(bins);
            bins.add(new Bin(Double.MIN_VALUE, lowDiv, -1));
            for(int i = 0; i < MAX_NUM_BINS; i ++){
                bins.add(new Bin(lowDiv, nextDiv, i));
                lowDiv = nextDiv;
                nextDiv = nextDiv + binRange;
            } bins.add(new Bin(lowDiv, Double.MAX_VALUE, MAX_NUM_BINS + 1));
            for(int i = 0; i < inAtt.size(); i++){
                for(int j = 0; j < bins.size(); j++){
                    if(bins.get(j).binContains(Double.valueOf(inAtt.get(i))) && !bins.get(j).equals("?")){
                        bins.get(j).incrementFreq();
                        j = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }

    public int findMax(List<String> inAtt){
        int answer = Integer.MIN_VALUE;
        for(int i = 0; i < inAtt.size(); i++){
            int curNum = Integer.getInteger(inAtt.get(i));
            if(curNum > answer){
                answer = curNum;
            }
        }
        return answer;
    }

    public int findMin(List<String> inAtt){
        int answer = Integer.MAX_VALUE;
        for(int i = 0; i < inAtt.size(); i++){
            int curNum = Integer.getInteger(inAtt.get(i));
            if(curNum < answer){
                answer = curNum;
            }
        }
        return answer;
    }

   /* public char[] toChars(String inString){
        String s = inString;
        int l = s.length();
        char[] chars = new char[l];
        s.getChars(0, l, chars, l - 1);
        return chars;
    }*/

    //this is the actual recursive method to run ID3
    public void id3(){

    }

    //this will be the method that mostly runs the algorithm, as it will perform our verification
    public void tenFold(){

    }
}