package CSCI446.Project3;

/**
 * Created by Mathew Gostnell on 11/16/2016.
 */
public class Bin {
    private final double minValue;    // minimum value (inclusive) for this bin
    private final double maxValue;    // maximum value (exclusive) for this bin
    private final int binID;          // int used for the attribute associated with this bin
    private int freq;

    public Bin(double minVale, double maxValue, int binID) {
        this.minValue = minVale;
        this.maxValue = maxValue;
        this.binID = binID;
        freq = 0;
    }

    public boolean binContains(double queryValue) {
        return minValue <= queryValue && queryValue < maxValue;
    }

    public double getMinValue() { return this.minValue; }
    public double getMaxValue() { return this.maxValue; }
    public int getBinID() { return this.binID; }
    public void incrementFreq(){freq++;}
    public int getFreq(){return freq;}
    public String getBranch(){return this.minValue + " to " + this.maxValue;}
}
