package CSCI446.Project3;

import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mathew Gostnell on 11/17/2016.
 */
public class TAN {

    private double[] classPriors;   // match a probability for a class in classes var
    private int currentFold;    // used to grab fold that we are looking at currently
    private int testingFold;
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
            testingFold = i;
            buildPriors();
            discretizeData();
        }

    }

    public void fillBins() {
        // skip 0 fold since it generated the bins
        for (int i = 1; i < 10; i++) {
            if (i == testingFold) {
                continue;
            } else {
                currentFold = i;
            }

            List<List<String>> currentData = dc.getDataFold().get(currentFold);
            List<List<String>> tranData = dc.transposeList(currentData);

            for (int row = 0; row < tranData.size(); row++) {

            }
        }
    }

    /**
     * Converts continuous values for attributes into discreet values
     */
    private void discretizeData() {
        boolean generatedBins = false;
        for (int i = 0; i < 10; i++) {
            if (i == testingFold) {
                continue;
            } else {
                currentFold = i;
                List<List<String>> currentData = dc.getDataFold().get(currentFold);
                List<List<String>> tranData = dc.transposeList(currentData);

                if (generatedBins == false) {
                    for (int row = 0; row < tranData.size(); row++) {
                        attribBins.add(discretizeRow(tranData.get(row)));
                    }
                } else {
                    // fill bins with data now
                }

            }
        }

    }

    /**
     * Converts attribute Strings into Bins for classifying attributes
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
            }

        }
        Collections.sort(procData);

        // generate bins based on Naive Estimator Process
        for (int i = 0; i < procData.size(); i++) {
            if (i == 0) {
                // append bin with lowest possible value
                binForThisAttr.add(new Bin(Double.MIN_VALUE, procData.get(i), i));
            } else if (i == procData.size() - 1) {
                // append bin with highest possible value
                binForThisAttr.add(new Bin(procData.get(i), Double.MAX_VALUE, i));
            } else {
                // estimate the range of bin based on nearby points of data
                double lowBound = (procData.get(i - 1) + procData.get(i)) / 2;
                double highBound = (procData.get(i) + procData.get(i + 1)) / 2;
                binForThisAttr.add(new Bin(lowBound, highBound, i));
            }
        }
        return binForThisAttr;
    }

    /**
     * Generates the Prior probabilities of a given class
     */
    public void buildPriors() {
        // grab the list of all class labels for this fold
        List<List<String>> classListHolder = dc.getClassificationFold();
        int totalClasses = 0;
        int[] totalClassOccurrence = new int[classes.size()];
        for (int i = 0; i < 10; i++) {
            if (i == testingFold) {
                continue;
            } else {
                currentFold = i;
            }
            // grab the list of all classes for this current fold
            List<String> classList = classListHolder.get(currentFold);
            // track the total number of classes in this fold and their occurrences
            totalClasses += classList.size();
            // for each class occurrence, match it to a class and track its occurrence
            for (String className : classList) {
                for (int j = 0; j < classes.size(); j++) {
                    if (className.equals(classes.get(j))) {
                        totalClassOccurrence[j]++;
                    }
                }
            }
        }

        // divide a particular class occurrence by total number of classes across training set
        for (int i = 0; i < classPriors.length; i++) {
            classPriors[i] = totalClassOccurrence[i] / totalClasses;
        }
    }

}
