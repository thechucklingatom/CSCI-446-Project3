package CSCI446.Project3;

import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mathew Gostnell on 11/17/2016.
 */
public class TAN {

    private int documentedFold; // the nth fold we choose to log our decisions
    private int currentFold;    // used to grab fold that we are looking at currently
    private DataContainer dc;   // the container that holds our folds of data
    private Writer logger;      // logger used for testing and decision making
    private List<List<Bin>> attribBins;

    public TAN(DataContainer dc, Writer logger, int currentFold) {
        this.dc = dc;
        this.logger = logger;
        this.currentFold = currentFold;
    }

    private void discretizeData() {
        /**
         * take all doubles for attributes and recalculate their values into discrete values
         */

        List<List<String>> currentData = dc.getDataFold().get(currentFold);
        List<List<String>> tranData = dc.transposeList(currentData); // rows of attrib

        for (int row = 0; row <= tranData.size(); row++) {
                attribBins.add(discretizeRow(tranData.get(row)));
        }

    }

    private List<Bin> discretizeRow(List<String> rawData) {
        List<Bin> binForThisAttr = new ArrayList<>();
        List<Double> procData = new ArrayList<>();
        for (String raw : rawData) {
            //convert string data into double data
            procData.add(Double.valueOf(raw));
        }
        Collections.sort(procData);

        for (int i = 0; i < procData.size(); i++){
            if (i == 0) {
                // append bin with lowest possible value
                binForThisAttr.add(new Bin(Double.MIN_VALUE, procData.get(i), i));
            } else if (i == procData.size() - 1) {
                // append bin with highest possible value
                binForThisAttr.add(new Bin(procData.get(i), Double.MAX_VALUE, i));
            } else {
                double lowBound = (procData.get(i-1) + procData.get(i)) / 2;
                double highBound = (procData.get(i) + procData.get(i+1)) / 2;
                binForThisAttr.add(new Bin(lowBound, highBound, i));
            }
        }

        return binForThisAttr;
    }

    @Deprecated // all attributes go through Naive Estimator process
    private boolean needsDiscretation(List<String> attribData) {
        for (String value : attribData) {
            double val = Double.valueOf(value);
            int compare = (int)val;
            if (compare != val) {
                // our value wasn't an integer and contains floating point values
                return true;
            }
        }
        // every value was an integer
        return false;
    }


}
