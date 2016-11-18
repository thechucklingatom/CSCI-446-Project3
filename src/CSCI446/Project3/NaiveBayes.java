package CSCI446.Project3;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author thechucklingatom
 */
public class NaiveBayes {
	private Writer writer;
	private DataContainer dataContainer;
	private int testingFold;
	private int currentFold;
	private List<List<Bin>> attribBins;

	public NaiveBayes(Writer writer, DataContainer dataContainer){
		this.writer = writer;
		this.dataContainer = dataContainer;
		testingFold = 9;
	}

	public double calculateProbability(String classType, String attribute){
		double probabilityOfClass = 1, probabilityOfAttribute = 1, probabilityOfAttributeGivenClass = 1;

		return probabilityOfClass * probabilityOfAttributeGivenClass / probabilityOfAttribute;
	}

	private void discretizeData() {
		/**
		 * take all doubles for attributes and recalculate their values into discrete values
		 */

		List<List<String>> currentData = dataContainer.getDataFold().get(currentFold);
		List<List<String>> tranData = dataContainer.transposeList(currentData); // rows of attrib

		for (int row = 0; row <= tranData.size(); row++) {
			attribBins.add(discretizeRow(tranData.get(row)));
		}

	}

	private List<Bin> discretizeRow(List<String> rawData) {
		/**
		 * Takes the data and creates a bin that captures the attributes given
		 * This will handle:
		 */
		List<Bin> binForThisAttr = new ArrayList<>();
		List<Double> procData = new ArrayList<>();
		// are we working with numbers or actual Strings
		boolean isNumber = (rawData.get(0).chars().allMatch(Character::isDigit) || rawData.get(0).contains("."));
		if (isNumber) {
			// convert strings into Double, add to data attributes
			for (String raw : rawData) {
				//convert number strings into Doubles, or strings into unique Doubles
				procData.add(Double.valueOf(raw));
			}
		} else {
			// convert Strings into unique integers, add to data attributes
			for (String raw : rawData) {
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

	public void classify(){
		for(int numberOfRuns = 0; numberOfRuns < 10; numberOfRuns++) {
			for (int i = 0; i < dataContainer.getDataFold().size(); i++) {
				if(i == testingFold){
					continue;
				}
				currentFold = i;

				discretizeData();
			}

			// TODO Testing
		}
	}
}
