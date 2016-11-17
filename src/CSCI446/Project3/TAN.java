package CSCI446.Project3;

import java.io.Writer;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Mathew Gostnell on 11/17/2016.
 */
public class TAN {

    private int documentedFold; // the nth fold we choose to log our decisions
    private int currentFold;    // used to grab fold that we are looking at currently
    private DataContainer dc;   // the container that holds our folds of data
    private Writer logger;      // logger used for testing and decision making

    public TAN(DataContainer dc, Writer logger) {
        this.dc = dc;
        this.logger = logger;
    }

    private void discretizeData() {
        /**
         * take all doubles for attributes and recalculate their values into discrete values
         */
        int currentAttribute = 0;   // index tracks the current index we are discretizing
        List<List<String>> currentData = dc.getDataFold().get(currentFold);

    }

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
