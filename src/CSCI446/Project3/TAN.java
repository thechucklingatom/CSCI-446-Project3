package CSCI446.Project3;

import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Mathew Gostnell on 11/17/2016.
 */
public class TAN {

    private double[] classPriors;   // match a probability for a class in classes var
    private int currentFold;    // used to grab fold that we are looking at currently
    private int testingFold;
    private int writingFold = 7;    // when testing fold is 7, write the results
    private List<String> classes;
    private DataContainer dc;   // the container that holds our folds of data
    private Writer logger;      // logger used for testing and decision making
    private List<List<Bin>> attribBins;

    public TAN(DataContainer dc, Writer logger) {
        this.dc = dc;
        this.logger = logger;
        attribBins = new ArrayList<>();
        classes = dc.getClassTypes();
        classPriors = new double[classes.size()];
    }

    /**
     * Trains the TAN on the data for everything except the testing
     */
    public void trainData() {
        for (int i = 0; i < 10; i++) {
            if ()
            testingFold = i;
            buildPriors();
            discretizeData();
        }

    }

    private void test() {
        int correctGuess = 0;
        for (int i = 0; i < dc.getDataFold().get(testingFold).size(); i++) {
            // iterate through rows of data
            String classGuess = "";  // store our guess from Node(class | attribute)
            List<String> potentialClasses = new ArrayList<>(); // leaf Nodes that have high probability
            // grab our testing data to begin testing
            List<String> row = dc.getDataFold().get(testingFold).get(i);
            // for each row in the testing fold ...
            for (int j = 0; j < row.size(); j++) {
                // grab the current attribute value
                String curAttrib = row.get(j);
                double highProb = 0.0;
                // track the highest probability among nodes for the given attribute
                for (int k = 0; k < classes.size(); k++) {
                    // compare our attribute Nodes against our Class Nodes
                    String classCheck = classes.get(k);
                    // calculate the probability given Class_k, attribute_i, in column_k
                    double curProb = calculateProbability(classCheck, curAttrib, k);
                    // if the Node probability is larger than any other Nodes, store it
                    if (curProb > highProb) {
                        highProb = curProb;
                        classGuess = classCheck;
                    }
                }
                // our class guess is the highest probability among classes for this attribute, repeat
                potentialClasses.add(classGuess);
            }
            // count the occurrence of our leaf Nodes and grab the highest occurrence  for our guess
            classGuess = getClassGuess(potentialClasses);
            if (classGuess.equals(dc.getClassificationFold().get(testingFold).get(i))) {
                correctGuess += 1;

            }
        }
    }

    /**
     * Takes our list of Class nodes and returns our guess as the highest occurrence.
     * Use random selection on ties.
     * @param guesses List of class Nodes of type String we think are possible guesses
     * @return The highest occurring class that is split on ties
     */
    private String getClassGuess(List<String> guesses) {
        // build structures to count occurrence of unique class guesses
        ArrayList<Integer> countGuesses = new ArrayList<>();
        ArrayList<String> uniqueEntry = new ArrayList<>();

        // build list to be populated with likely guesses
        ArrayList<String> highCountGuess = new ArrayList<>();

        for (String check : guesses) {
            // if we have already seen this guess
            if (uniqueEntry.contains(check)) {
                // grab the index of guess, and increment count
                int sharedIndex = uniqueEntry.indexOf(check);
                int oldVal = countGuesses.get(sharedIndex);
                countGuesses.set(sharedIndex, oldVal+1);
            } else {
                // new class guess, add to uniqueEntry
                countGuesses.add(1);
                uniqueEntry.add(check);
            }  // end if-else
        } // end for

        int highestCount = 0;  // largest occurrence
        for (int i = 0; i < uniqueEntry.size(); i++) {
            if (countGuesses.get(i) > highestCount) {
                highCountGuess.clear();
                highCountGuess.add(uniqueEntry.get(i));
            } else if (countGuesses.get(i) == highestCount) {
                highCountGuess.add(uniqueEntry.get(i));
            }  // end if-else
        }  // end for
        Random rng = new Random();
        return highCountGuess.get(rng.nextInt(highCountGuess.size()));
    }

    /**
     * Calculate P(C | a_i)
     * @param classType The class we are looking at for probability
     * @param attribute The attribute value we are looking at for probability
     * @param attributeIndex  The location of the attribute value we need to grab in data
     * @return  Probability P; 0 <= P <= 1; Probability of class C given the value of attribute a_i
     */
    public double calculateProbability(String classType, String attribute, int attributeIndex){
        double probabilityOfClass = 1, probabilityOfAttribute = 1, probabilityOfAttributeGivenClass = 1;

        int priorIndex = 0;
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).equals(classType)) {
                priorIndex = i;
            }  // end if
        }  // end for

        // use priors to calculate probability of a class
        probabilityOfClass = classPriors[priorIndex];
        // find the probability of a class given the attribute value and index
        probabilityOfAttribute = getAttributeProbability(attribute, attributeIndex);
        // return the probability calculate using Naive Bayes
        return probabilityOfClass * probabilityOfAttributeGivenClass / probabilityOfAttribute;
    }  // end calculateProbability()

    /**
     * Calculate P(attribute_i)
     * @param attribute  := value of attribute we are calculating a probability of
     * @param attributeIndex  := location of the attribute within our current Fold
     * @return a probability P; 0 <= P <= 1; the probability of this attribute's value
     *
     */
    private double getAttributeProbability(String attribute, int attributeIndex){
        Double value;  // store value of raw data
        if(attribute.chars().allMatch(Character::isDigit) || attribute.contains(".")){
            value = Double.valueOf(attribute);
        }else{
            value = (double) attribute.hashCode();
        }  // end if-else


        double totalSelectedAttribute = 0;
        for(Bin bin : attribBins.get(attributeIndex)){
            if(bin.binContains(value)){
                totalSelectedAttribute = bin.getFreq();
                break;
            }  // end if
        }  // end for

        int totalAttributes = 0;
        for(int i = 0; i < dc.getDataFold().size(); i++){
            if(i == testingFold){
                continue;
            }else{
                totalAttributes += dc.getDataFold().get(i).size();
            }  // end if-else
        }  // end for
        return totalSelectedAttribute / totalAttributes;
    }  // end getAttributeProbability()

    /**
     * After we generate our bins we now fill the Bins to use for calculating probability
     *
     */
    public void fillBins() {
        // grab our current data and then transpose it
        List<List<String>> currentData = dc.getDataFold().get(currentFold);
        List<List<String>> tranData = dc.transposeList(currentData);
        for (int row = 0; row < tranData.size(); row++) {
            // for each row of attribute values, discretize it
            List<Double> procData = new ArrayList<>();
            // for each String of raw data, convert it into a value to place into a bin
            for (String rawData : tranData.get(row)) {
                if (rawData.chars().allMatch(Character::isDigit) || rawData.contains(".")) {
                    procData.add(Double.parseDouble(rawData));
                } else {
                    // not perfect, but useable in the small domain of values we use for data
                    procData.add((double) rawData.hashCode());
                }  // end if-else
            }  // end for

            // for each value we have, place it into a corresponding bin
            for (double value : procData) {
                for (Bin bin : attribBins.get(row)) {
                    if (bin.binContains(value)) {
                        bin.incrementFreq();
                    }  // end if
                }  // end for
            }  // end for
        }  // end for
    }  // end fillBins()


    /**
     * Converts attribute Strings into Bins for classifying attributes
     *
     * @param rawData List of String data for attribute values
     * @return List of Bin that summarize the attribute value parameter
     */
    private List<Bin> discretizeRow(List<String> rawData) {
        List<Bin> binForThisAttr = new ArrayList<>();
        List<Double> procData = new ArrayList<>();
        // are we working with numbers or actual Strings
        // convert strings into Double, add to data attributes
        for (String raw : rawData) {
            // check current attribute value for String type or floating point type
            if ((rawData.get(0).chars().allMatch(Character::isDigit) || rawData.get(0).contains("."))) {
                //convert number strings into Doubles, or strings into unique Doubles
                procData.add(Double.valueOf(raw));
            } else {
                // convert Strings into unique integers, add to data attributes
                procData.add((double) raw.hashCode());
            }  // end if-else
        }  // end for

        // sort data into ascending list of values
        Collections.sort(procData);

        // generate bins based on Naive Estimator Process
        for (int i = 0; i < procData.size(); i++) {
            if (i == 0) {
                // append bin with lowest possible value
                binForThisAttr.add(new Bin(Double.MIN_VALUE, procData.get(i), i));
                // binForThisAttr.get(binForThisAttr.size() - 1).incrementFreq();
            } else if (i == procData.size() - 1) {
                // append bin with highest possible value
                binForThisAttr.add(new Bin(procData.get(i), Double.MAX_VALUE, i));
                // binForThisAttr.get(binForThisAttr.size() - 1).incrementFreq();
            } else {
                // estimate the range of bin based on nearby points of data
                double lowBound = (procData.get(i - 1) + procData.get(i)) / 2;
                double highBound = (procData.get(i) + procData.get(i + 1)) / 2;
                binForThisAttr.add(new Bin(lowBound, highBound, i));
                // binForThisAttr.get(binForThisAttr.size() - 1).incrementFreq();
            } // end if-else statement
        }  // end for
        return binForThisAttr;
    }

    /**
     * Converts continuous values for attributes into discreet values
     */
    private void discretizeData() {
        boolean generatedBins = false;  // track bin generation
        for (int i = 0; i < 10; i++) {
            if (i == testingFold) {
                continue;  // skip over testing data
            } else {
                currentFold = i;
                // grab our current data fold and transpose it
                List<List<String>> currentData = dc.getDataFold().get(currentFold);
                List<List<String>> tranData = dc.transposeList(currentData);

                if (generatedBins == false) {  // build bins first
                    for (int row = 0; row < tranData.size(); row++) {
                        attribBins.add(discretizeRow(tranData.get(row)));
                    }
                    generatedBins = true;
                } else {  // fill bins with data now
                    fillBins();
                }  // end if-else
            }  // end if-else
        }  // end for
    }  // end discretizeData()


    /**
     * Generates the Prior probabilities for all classes in data
     */
    public void buildPriors() {
        // grab the list of all class labels for this fold
        List<List<String>> classListHolder = dc.getClassificationFold();
        int totalClasses = 0;  // track ALL class occurrences for this fold
        int[] totalClassOccurrence = new int[classes.size()]; // track respective class occurrence
        for (int i = 0; i < 10; i++) {
            if (i == testingFold) {
                continue;  // skip testing fold
            } else {
                currentFold = i;
            }  // end if

            // grab the list of all classes for this current fold
            List<String> classList = classListHolder.get(currentFold);
            // track the total number of classes in this fold and their occurrences
            totalClasses += classList.size();
            // for each class occurrence, match it to a class and track its occurrence
            for (String className : classList) {
                for (int j = 0; j < classes.size(); j++) {
                    if (className.equals(classes.get(j))) {
                        totalClassOccurrence[j]++;
                    }  // end if
                }  // end for
            }  // end for
        } // end for

        // divide a particular class occurrence by total number of classes across training set
        for (int i = 0; i < classPriors.length; i++) {
            classPriors[i] = totalClassOccurrence[i] / totalClasses;
        }  // end for
    } // end buildPriors()

}  // end Tan.java
