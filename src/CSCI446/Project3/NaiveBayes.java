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
	private int foldToWrite;
	private List<List<Bin>> attribBins;

	/**
	 * Naive Bayes Classifier
	 * @param fileOutput The writer to output data.
	 * @param dataContainer The Object that holds all of the data
	 */
	public NaiveBayes(Writer fileOutput, DataContainer dataContainer){
		this.fileOutput = fileOutput;
		this.dataContainer = dataContainer;
		testingFold = 9;
		attribBins = new ArrayList<>();
		currentFold = 0;
		foldToWrite = 8;
	}

	/**
	 * Gets the probability of any given class in our data set.
	 * @param classType The class type to find the probability of.
	 * @return The probability of the class in the data set. P(c_i)
	 */
	private double getClassProbability(String classType){
		double classCount = 0;
		double totalNumberOfClasses = 0;
		//so for every fold
		for(int i = 0; i < dataContainer.getClassificationFold().size(); i++){
			//if not a data fold continue
			if(i == testingFold){
				continue;
			}

			//otherwise check to see how many times that class appears.
			for(int j = 0; j < dataContainer.getClassificationFold().get(i).size(); j++){
				if(dataContainer.getClassificationFold().get(i).get(j).equals(classType)){
					classCount++;
				}
				totalNumberOfClasses++;
			}
		}

		if(foldToWrite == currentFold){
			try {
				//output the nearest classes
				fileOutput.append("Calculating the probability of Class: ");
				fileOutput.append(classType);
				fileOutput.append("\n");
				fileOutput.append("Probability: ");
				fileOutput.append(String.valueOf(classCount / totalNumberOfClasses));
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get class Probability");
			}
		}

		//return the probability of that.
		return classCount / totalNumberOfClasses;
	}

	/**
	 * Gets the probability of any given attribute
	 * @param attribute the attribute you want to find how many times it exists.
	 * @param attributeIndex Which column the attribute you are looking at resides in.
	 * @return The general probability of that attribute P(x_i)
	 */
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
				//check how many times it shows up based on our bins.
				totalSelectedAttribute = bin.getFreq();
				break;
			}
		}

		//count the total attributes
		int totalAttributes = 0;
		for(int i = 0; i < dataContainer.getDataFold().size(); i++){
			if(i == testingFold){
				continue;
			}else{
				totalAttributes += dataContainer.getDataFold().get(i).size();
			}
		}

		if(foldToWrite == currentFold){
			try {
				//output the nearest classes
				fileOutput.append("Calculating the probability of Attribute: ");
				fileOutput.append(attribute);
				fileOutput.append("\n");
				fileOutput.append("Probability: ");
				fileOutput.append(String.valueOf(totalSelectedAttribute / totalAttributes));
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get class Probability");
			}
		}

		//return the general probability of that attribute
		return totalSelectedAttribute / totalAttributes;
	}

	/**
	 * Gets the probability of an attribute given the class.
	 * @param attribute The attribute you are finding the probability for.
	 * @param attributeIndex Where the attribute exists in the columns.
	 * @param classType The given class type.
	 * @return P(x_i | c_i)
	 */
	private double getProbabilityOfAttributeGivenClass(String attribute, int attributeIndex,
			String classType){
		int totalSelectedAttribute = 0;
		double totalGivenClass = 0;

		//checks if the class matches first
		for(int i = 0; i < dataContainer.getClassificationFold().size(); i++){
			if(i == testingFold){
				continue;
			}

			//checks ever class that is in the data set, but not in the training set
			for(int j = 0; j < dataContainer.getClassificationFold().get(i).size(); j++){
				//if it finds it.
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
						classValue = Double.valueOf(dataContainer.getDataFold().get(i).get(j).get(attributeIndex));
					}else{
						classValue = (double) dataContainer.getDataFold().get(i).get(j).get(attributeIndex).hashCode();
					}

					//check if it matches the attribute, if so, count it up.
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


		if(foldToWrite == currentFold){
			try {
				//output the nearest classes
				fileOutput.append("Calculating the probability of Attribute: ");
				fileOutput.append(attribute);
				fileOutput.append(" Given Class: ");
				fileOutput.append(classType);
				fileOutput.append("\n");
				fileOutput.append("Probability: ");
				fileOutput.append(String.valueOf(totalSelectedAttribute / totalGivenClass));
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get class Probability");
			}
		}

		//return the probability of P(x_i | c_i)
		return totalSelectedAttribute / totalGivenClass;
	}

	/**
	 * Calculate the probability of P(c_i | x_i) using Baye's rule, converting it to P(c_i)P(x_i | c_i)/P(x_i)
	 * @param classType The class type you are checking for
	 * @param attribute the attribute you are checking against
	 * @param attributeIndex where that attribute ends up in the columns of data
	 * @return P(c_i | x_i)
	 */
	public double calculateProbability(String classType, String attribute, int attributeIndex){
		double probabilityOfClass, probabilityOfAttribute, probabilityOfAttributeGivenClass;

		probabilityOfClass = getClassProbability(classType);
		probabilityOfAttribute = getAttributeProbability(attribute, attributeIndex);
		probabilityOfAttributeGivenClass = getProbabilityOfAttributeGivenClass(attribute, attributeIndex, classType);

		if(foldToWrite == currentFold){
			try {
				//output the nearest classes
				fileOutput.append("Calculating the total Probability: ");
				fileOutput.append(String.valueOf(probabilityOfClass * probabilityOfAttributeGivenClass / probabilityOfAttribute));
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get class Probability");
			}
		}

		return probabilityOfClass * probabilityOfAttributeGivenClass / probabilityOfAttribute;
	}

	/**
	 * change all the data and bin it so everything is discrete.
	 */
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

	/**
	 * makes the bins based off of a transposed row in our data set, but not training set
	 * @param rawData the raw transposed data to make discrete
	 * @return The bins we are going to be using for our data.
	 */
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

	/**
	 * what actually tries to classify stuff in the the data set
	 */
	public void classify(){
		try {
			fileOutput.append("NaiveBayes classification");
			fileOutput.append("\n");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("error printing in classify");
		}

		//run 10-fold validation.
		for(int numberOfRuns = 0; numberOfRuns < 10; numberOfRuns++) {
			discretizeData();
			//train on 9 of the folds
			for (int i = 0; i < dataContainer.getDataFold().size(); i++) {
				//if testing fold skip it
				if(i == testingFold){
					continue;
				}
				currentFold = i;

				//fill the bins with the data
				fillBins();
			}

			//test against the test fold
			test();
			//clear the bins for the next run
			attribBins.clear();
			//move the testing fold.
			testingFold--;
		}
	}

	/**
	 * Fill the bins with all the training data
	 */
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

	/**
	 * Test against the test set.
	 */
	private void test(){
		//How many times it classified the row correctly
		int correctGuess = 0;
		for(int i = 0; i < dataContainer.getDataFold().get(testingFold).size(); i++){
			String classGuess = "";
			double probability = 1;
			double guessProbability = 0;
			//for every class type
			for(String potentialClass : dataContainer.getClassTypes()) {
				//calculate the probability for all the given attributes
				for (int j = 0; j < dataContainer.getDataFold().get(testingFold).get(i).size(); j++) {
					probability *= calculateProbability(potentialClass,
							dataContainer.getDataFold().get(testingFold).get(i).get(j),
							j);
				}

				//if it has a higher probability than the last probability calculated swap the guess
				if(probability > guessProbability){
					classGuess = potentialClass;
				}  // end if
				probability = 1;
			} // end for

			//check if it was correct.
			if(classGuess.equals(dataContainer.getClassificationFold().get(testingFold).get(i))){
				if(foldToWrite == currentFold){
					try {
						//output the nearest classes
						fileOutput.append("Classified: ");
						fileOutput.append(classGuess);
						fileOutput.append(" Correctly!\n");
						fileOutput.append(classGuess);
						fileOutput.append("\n");
						fileOutput.append("Percent classified correctly ");
						fileOutput.append(String.valueOf((double)(correctGuess) / dataContainer.getDataFold().get(testingFold).size()));
						fileOutput.append("\n");
					} catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("error printing in get class Probability");
					}
				}
				correctGuess++;
			}else{
				try {
					//output the nearest classes
					fileOutput.append("Classified: ");
					fileOutput.append(dataContainer.getClassificationFold().get(testingFold).get(i));
					fileOutput.append(" Incorrectly.\n");
					fileOutput.append(classGuess);
					fileOutput.append("\n");
					fileOutput.append("Percent classified correctly ");
					fileOutput.append(String.valueOf((double)(correctGuess) / dataContainer.getDataFold().get(testingFold).size()));
					fileOutput.append("\n");
				} catch (IOException ex) {
					ex.printStackTrace();
					System.out.println("error printing in get class Probability");
				}
			}
		}

	}
}
