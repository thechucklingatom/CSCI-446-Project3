package CSCI446.Project3;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author thechucklingatom
 */
public class NaiveBayes {
	private Writer fileOutput;
	private DataContainer dataContainer;
	private int testingFold;
	private int currentFold;
	private List<List<Bin>> attribBins;

	public NaiveBayes(Writer fileOutput, DataContainer dataContainer){
		this.fileOutput = fileOutput;
		this.dataContainer = dataContainer;
		testingFold = 9;
		attribBins = new ArrayList<>();
	}

	private double getClassProbability(String classType){
		double classCount = 0;
		double totalNumberOfClasses = 0;
		for(int i = 0; i < dataContainer.getClassificationFold().size(); i++){
			if(i == testingFold){
				continue;
			}
			for(int j = 0; j < dataContainer.getClassificationFold().get(i).size(); j++){
				if(dataContainer.getClassificationFold().get(i).get(j).equals(classType)){
					classCount++;
				}
				totalNumberOfClasses++;
			}
		}

		return classCount / totalNumberOfClasses;
	}

	private double getAttributeProbability(String attribute, int attributeIndex){

		Double value;
		if(attribute.chars().allMatch(Character::isDigit) || attribute.contains(".")){
			value = Double.valueOf(attribute);
		}else{
			value = (double) attribute.hashCode();
		}
		double totalSelectedAttribute = 0;
		for(Bin bin : attribBins.get(attributeIndex)){
			if(bin.binContains(value)){
				totalSelectedAttribute = bin.getFreq();
				break;
			}
		}

		int totalAttributes = 0;
		for(int i = 0; i < dataContainer.getDataFold().size(); i++){
			if(i == testingFold){
				continue;
			}else{
				totalAttributes += dataContainer.getDataFold().get(i).size();
			}
		}

		return totalSelectedAttribute / totalAttributes;
	}

	private double getProbabilityOfAttributeGivenClass(String attribute, int attributeIndex,
			String classType){
		int totalSelectedAttribute = 0;
		double totalGivenClass = 0;
		for(int i = 0; i < dataContainer.getClassificationFold().size(); i++){
			if(i == testingFold){
				continue;
			}
			for(int j = 0; i < dataContainer.getClassificationFold().get(i).size(); i++){
				if(dataContainer.getClassificationFold().get(i).get(j).equals(classType)){
					totalGivenClass++;
					double value;
					if(attribute.chars().allMatch(Character::isDigit) || attribute.contains(".")){
						value = Double.valueOf(attribute);
					}else{
						value = (double) attribute.hashCode();
					}

					double classValue;
					if(dataContainer.getDataFold().get(i).get(j).get(attributeIndex).chars().allMatch(Character::isDigit)
							|| dataContainer.getDataFold().get(i).get(j).get(attributeIndex).contains(".")){
						classValue = Double.valueOf(attribute);
					}else{
						classValue = (double) attribute.hashCode();
					}

					for(Bin bin : attribBins.get(attributeIndex)){
						if(bin.binContains(value) && bin.binContains(classValue)){
							totalSelectedAttribute++;
							break;
						}
					}
					break;
				}
			}
		}

		return totalSelectedAttribute / totalGivenClass;
	}

	public double calculateProbability(String classType, String attribute, int attributeIndex){
		double probabilityOfClass = 1, probabilityOfAttribute = 1, probabilityOfAttributeGivenClass = 1;

		probabilityOfClass = getClassProbability(classType);
		probabilityOfAttribute = getAttributeProbability(attribute, attributeIndex);

		return probabilityOfClass * probabilityOfAttributeGivenClass / probabilityOfAttribute;
	}

	private void discretizeData() {
		/**
		 * take all doubles for attributes and recalculate their values into discrete values
		 */

		List<List<String>> currentData = dataContainer.getDataFold().get(currentFold);
		List<List<String>> tranData = dataContainer.transposeList(currentData); // rows of attrib

		for (int row = 0; row < tranData.size(); row++) {
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
		// convert strings into Double, add to data attributes
		for (int i = 0; i < rawData.size(); i++) {
			String raw = rawData.get(i);
			// check current attribute value for String type or floating point type
			if ((rawData.get(i).chars().allMatch(Character::isDigit) || rawData.get(i).contains("."))) {
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

	public void classify(){
		try {
			fileOutput.append("NaiveBayes classification");
			fileOutput.append("\n");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("error printing in classify");
		}
		for(int numberOfRuns = 0; numberOfRuns < 10; numberOfRuns++) {
			discretizeData();
			for (int i = 0; i < dataContainer.getDataFold().size(); i++) {
				if(i == testingFold){
					continue;
				}
				currentFold = i;

				fillBins();
			}

			// TODO Testing
			attribBins.clear();
			testingFold--;
		}
	}

	private void fillBins(){
		for(List<String> row : dataContainer.getDataFold().get(currentFold)){
			for(int i = 0; i < row.size(); i++){
				String raw = row.get(i);
				Double value;
				if(raw.chars().allMatch(Character::isDigit) || raw.contains(".")){
					value = Double.valueOf(raw);
				}else{
					value = (double) raw.hashCode();
				}
				for(Bin bin : attribBins.get(i)){
					if(bin.binContains(value)){
						bin.incrementFreq();
						break;
					}
				}
			}
		}
	}
}
