package CSCI446.Project3;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Created by Alan Fraticelli on 11/11/2016.
 */
public class ID3 {
    //private int foldToWrite;
    private Writer writer;
    private DataContainer container;
    //private int currentFold;
    private List<List<String>> trainingSet;
    private List<List<String>> trainingSetClass;
    //private int testingFold;
    private Tree tree;
    private List<List<Bin>> binAtt; //first list reps the attributes, second list the bins of those atts
    private List<List<String>> classification;
    private List<String> classTypes;
    private final int MAX_NUM_BINS = 6;

    public ID3(Writer inWriter, DataContainer inContainer) {
        this.writer = inWriter;
        this.container = inContainer;
        tree = new Tree(new Node());
        binAtt = new ArrayList<>();
        classTypes = container.getClassTypes();
        classification = container.getClassificationFold();
    }

    //takes in a transposed training set (to make rows  attributes) and bin it
    public void discretize(List<List<String>> inFold) {
        //iterating through the attributes
        try{
            writer.append("ID3 Discretizing:\n");
        } catch (IOException x){}
        for (int i = 0; i < inFold.size(); i++) {
            //we are going to determine the size of unique data of each attribute,
            //and bin the attributes with a max of 5 unique data
            try{
                writer.append(" Calculating number of bins for attribute " + i + ":\n");
            } catch (IOException x){}
            //find out how many bins we
            int numBin = findNumBin(inFold.get(i));
            try{
                writer.append(" Give our bins bounds and ID numbers. \n");
            } catch(IOException x){}
            //this will generate the bins for an attribute and "fill" them with a frequency value
            bin(inFold.get(i), numBin);
        }
    }

    public int findNumBin(List<String> inFold) {
        int answer = 0;
        HashSet noDupes = new HashSet();
        for (int j = 0; j < inFold.size(); j++) { //iterate through an attribute and develope a distinct HashSet with the values
            if (!inFold.get(j).equals("?")) { //make sure we are not adding a question mark to our HashSet
                //a HashSet only adds distinct values, so only fills with unique values
                noDupes.add(inFold.get(j));
            }
        }
        //we want to keep the amount of possible values for an attribute low, so we set a max value and hard set it
        //to that max value if it goes over. This prevents continuous data from generating giant amounts of branches and children
        if (noDupes.size() <= MAX_NUM_BINS - 1) {
            try{
                writer.append("     Found " + answer + " unique values for the attribute\n");
            } catch(IOException x){}
            return answer;
        } else {
            try{
                writer.append("     Found over " + MAX_NUM_BINS + " unique values for the attribute. Setting number of bins to " + MAX_NUM_BINS + "\n");
            } catch(IOException x){}
            return MAX_NUM_BINS;
        }
    }

    public void bin(List<String> inAtt, int numBin) {
        if (numBin <= 5) {
            List<Bin> bins = new ArrayList<>();
            binAtt.add(bins);
            //obtain a list of the unique values in this attribute
            List<String> uniqueValue = new ArrayList<>();
            for(String s : inAtt){
                if(!uniqueValue.contains(s)){
                    uniqueValue.add(s);
                }
            }
            //create the unique doubles that will bound our bins
            List<Double> stringHash = new ArrayList<>();
            for (String s : uniqueValue) {
                stringHash.add((double) s.hashCode());
            } //create the bins
            int i;
            bins.add(new Bin(Double.MIN_VALUE, stringHash.get(0), -1));
            for (i = 0; i <= stringHash.size(); i++) {
                Bin binToAdd = new Bin(stringHash.get(i), stringHash.get(i + 1), i);
                binToAdd.setIsCont(false);
                bins.add(binToAdd);
            }
            bins.add(new Bin(stringHash.get(i), Double.MAX_VALUE, i));
            //fill the bins
            for (int x = 0; x < inAtt.size(); x++) {
                for (int j = 0; j < bins.size(); j++) {
                    if (bins.get(j).binContains((double) inAtt.get(x).hashCode()) && !inAtt.get(j).equals("?")) {
                        bins.get(j).incrementFreq();
                        continue;
                    }
                }
            }
        } else {
            int max = findMax(inAtt);
            int min = findMin(inAtt);
            int range = max - min;
            float binRange = range / MAX_NUM_BINS;
            float lowDiv = min;
            float nextDiv = lowDiv + binRange;
            List<Bin> bins = new ArrayList<>();
            binAtt.add(bins);
            Bin binToAdd = new Bin(Double.MIN_VALUE, lowDiv, -1);
            binToAdd.setIsCont(true);
            bins.add(binToAdd);
            for (int i = 0; i < MAX_NUM_BINS; i++) {
                bins.add(new Bin(lowDiv, nextDiv, i));
                lowDiv = nextDiv;
                nextDiv = nextDiv + binRange;
            }
            bins.add(new Bin(lowDiv, Double.MAX_VALUE, MAX_NUM_BINS + 1));
            for (int i = 0; i < inAtt.size(); i++) {
                for (int j = 0; j < bins.size(); j++) {
                    if (bins.get(j).binContains(Double.valueOf(inAtt.get(i))) && !inAtt.get(j).equals("?")) {
                        bins.get(j).incrementFreq();
                        j = Integer.MAX_VALUE;
                    }
                }
            }
        }
    }

    public int findMax(List<String> inAtt) {
        int answer = Integer.MIN_VALUE;
        for (int i = 0; i < inAtt.size(); i++) {
            int curNum = Integer.getInteger(inAtt.get(i));
            if (curNum > answer) {
                answer = curNum;
            }
        }
        return answer;
    }

    public int findMin(List<String> inAtt) {
        int answer = Integer.MAX_VALUE;
        for (int i = 0; i < inAtt.size(); i++) {
            int curNum = Integer.getInteger(inAtt.get(i));
            if (curNum < answer) {
                answer = curNum;
            }
        }
        return answer;
    }

    //this is the actual recursive method to run ID3
    public Tree id3(List<List<Integer>> examples, List<String> exampleClass, List<Integer> attributes, List<List<Bin>> parentExamples, List<String> parentExClass) {
        Tree curTree;
        if(examples.isEmpty()){
            String maxString = pluralityValue(parentExClass);
            curTree = new Tree(new Node());
            curTree.getRoot().setData(maxString);
            return curTree;
        } else if(isSameClass(exampleClass)){
            String s = exampleClass.get(0);
            curTree = new Tree(new Node());
            curTree.getRoot().setData(s);
            return curTree;
        } else if(attributes.isEmpty()){
            String maxString = pluralityValue(exampleClass);
            curTree = new Tree(new Node());
            curTree.getRoot().setData(maxString);
            return curTree;
        } else {
            int attributeA = bestAttribute(examples, exampleClass, attributes);
        }
        return null;
    }

    public String pluralityValue(List<String> parentExClass){
        List<String> uniqueClass = new ArrayList<>();
        List<Integer> classFreq = new ArrayList<>();
        for(String s : parentExClass){
            if(!uniqueClass.contains(s)){
                uniqueClass.add(s);
                classFreq.add(1);
            } else {
                int i = uniqueClass.indexOf(s);
                int oldVal = classFreq.get(i);
                classFreq.set(i, oldVal+1);
            }
        }
        int maxFreq = Integer.MIN_VALUE;
        String maxString = "";
        for(int i = 0; i < classFreq.size(); i++){
            if(maxFreq < classFreq.get(i)){
                maxFreq = classFreq.get(i);
                maxString = uniqueClass.get(i);
            }
        }
        return maxString;
    }

    public boolean isSameClass(List<String> exampleClass){
        String testS = exampleClass.get(0);
        for(String s : exampleClass){
            if(!s.equals(testS)){
                return false;
            }
        }
        return true;
    }

    public int bestAttribute(List<List<Integer>> examples, List<String> exampleClass, List<Integer> attributes){
        //iterate through our attributes and find their gain
        List<Double> attributeGain = new ArrayList<>();
        for(int i : attributes){
            //gather unique classes
            List<String> uniqueClass = new ArrayList<>();
            for(String s : exampleClass){
                if(!uniqueClass.contains(s)){
                    uniqueClass.add(s);
                }
            }
            //create a list to gather the freq of a unique class per bin
            List<Integer> classFreq = new ArrayList<>();
            //populate for increment purposes
            for(int y = 0; y < binAtt.get(i).size(); y++){
                classFreq.add(0);
            }
            //find entropy
            //find the examples where attribute i is set to x and store index into list
            int[] binFreq = new int[binAtt.get(i).size()];
            for(int j = 0; j < examples.size(); j++){ //iterate through the rows
                for(int k = 0; k < binFreq.length; k++) { //iterate through the bins of the attribute i
                    if (binAtt.get(i).get(k).getBinID() == examples.get(j).get(i)){
                        binFreq[k]++;
                        int oldVal = classFreq.get(k);
                        classFreq.set(k, oldVal++);
                    }
                }
            } //we now have freq of classes for each value of attribute i
            double sum = 0;
            int denominator = 0;
            for(int j = 0; j < uniqueClass.size(); j++){ //iterate through the j classes we have
                denominator = denominator + classFreq.get(j);
            }
            for(int j = 0; j < uniqueClass.size(); j++){ //sum over j classes
                int numerator = classFreq.get(j);
                double term = (numerator/denominator) * (Math.log(2) / Math.log(numerator/denominator));
                sum = sum + term;
            }
            //find remain
            //find entropy - remain, or gain
        }
        return 0;
    }

    public void prune(Tree decisionTree){

    }

    //this will be the method that mostly runs the algorithm, as it will perform our verification
    public void tenFold() {
        List<List<List<String>>> allFolds = container.getDataFold();
        //iterate which fold is currently the test set
        for (int i = 0; i < 10; i++) {
            trainingSet = new ArrayList<>();
            trainingSetClass = new ArrayList<>();
            //combine the training folds into one fold j=folds, k=rows to add
            for(int j = 0; j < 10; j++) {
                for(int k = 0; k < allFolds.get(j).get(k).size(); k++) {
                    if (j == i) {
                    } else {
                        trainingSet.add(allFolds.get(j).get(k));
                    }
                }
            }
            //transpose that fold to make the attributes the rows
            container.transposeList(trainingSet);
            //call discretize sending our fold
            discretize(trainingSet);
            //then we learn
            List<Integer> attributes = new ArrayList<>();
            int x = 0;
            for(List<String> n : trainingSet){
                attributes.add(x);
                x++;
            }
            List<List<Integer>> binMap = makeBinMap(trainingSet);
            container.transposeList(trainingSet);
            List<String> classCol = combineClassificationFolds(i);

            tree = id3(binMap, classCol, attributes, null, null);
            //then we prune
            //then we test (a run)
        }
    }

    public List<String> combineClassificationFolds(int i){
        List<String> combClass = new ArrayList<>();
        for(int j = 0; j < 10; j++){
            for(int k = 0; k < classification.get(j).size(); k++)
            if(j == i){} else{
                combClass.add(classification.get(j).get(k));
            }
        }
        return combClass;
    }

    private List<List<Integer>> makeBinMap(List<List<String>> inSet){
        List<List<Integer>> map = new ArrayList<>();
        for(int i = 0; i < inSet.size(); i++){ //iterates attributes
            for(int j = 0; j < inSet.get(i).size(); j++){ //iterates the row of the selected att
                //differentiate what type of attribute this is, disc or cont
                if(binAtt.get(i).get(0).isCont()){
                    for(int k = 0; k < binAtt.get(i).size(); k++) { //iterate through the bins
                        if(inSet.get(i).get(j).equals("?")) {
                            map.get(i).set(j, -10);
                        } else if (binAtt.get(i).get(k).binContains(Double.valueOf(inSet.get(i).get(j)))) {
                            int binNum = binAtt.get(i).get(k).getBinID();
                            map.get(i).set(j, binNum);
                        }
                    }
                } else {
                    for (int x = 0; x < binAtt.get(i).size(); x++) {
                        if(inSet.get(i).get(j).equals("?")){
                            map.get(i).set(j, -10);
                        } else if (binAtt.get(i).get(x).binContains((double) inSet.get(i).get(j).hashCode())) {
                            int binNum = binAtt.get(i).get(x).getBinID();
                            map.get(i).set(j, binNum);
                        }
                    }
                }
            }
        }
        return map;
    }
}