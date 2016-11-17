package CSCI446.Project3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Robert Putnam on 11/4/16.
 *
 * @author Robert Putname
 */
class DataContainer {

	private List<List<String>> data;
	private List<String> classification;
	private List<String> classTypes;  // names of classifiers for data set
	private List<List<List<String>>> dataFold;
	private List<List<String>> classificationFold;

	DataContainer(){
		data = new ArrayList<>();
		classification = new ArrayList<>();
		classTypes = new ArrayList<>();
	}

	void populateData(String filePath, int classificationLocation){
        // setup FileReader for reading in CSV files
		ArrayList<String> rows = new ArrayList<>();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath);
		} catch (FileNotFoundException e) {
            // double check directories and confirm filePaths listed in Main.java
			e.printStackTrace();
			System.out.println("File not found, exiting");
			System.exit(2);
		}
		// setup BufferedReader for reading in CSV file
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;  // used to read in line-by-line in BufferedReader
		try {  // while we have data to read, add it to rows of data
			while((line = bufferedReader.readLine()) != null){
				rows.add(line);
			}
		} catch (IOException e) {
            // Double check the data files for errors/corruption.  Re-download into project
            // directory if necessary
			e.printStackTrace();
			System.out.println("Error reading from file, exiting");
			System.exit(3);
		}

		Collections.shuffle(rows);  // shuffle data around (used in 10-fold verification)

		for (String row : rows) {
			// modify data to remove new-lines and carriage returns
			row = row.replace("\n", "").replace("\r", "");
			// split csv data into array
			String[] columns = row.split(",");
            // Array-list for placing non-classification data
			ArrayList<String> toPutInData = new ArrayList<>();
            // grab classification for data for use in classification algorithms
			for(int i = 0; i < columns.length; i++){
				if(i == classificationLocation){
					classification.add(columns[i]);
				}else{
					toPutInData.add(columns[i]);
				}
			}
			data.add(toPutInData);  // add non-classification data
		}
		fillUniqueClassList();  // final processing of data
		generateFolds();  // generate fold for testing, verification, and analysis
	}

	private void fillUniqueClassList() {
		classTypes = classification.stream().distinct().collect(Collectors.toList());
	}

	private void generateFolds(){
		// round data into larger groups to minimize number of small folds
		int sizeOfFolds = (int)Math.ceil(data.size() / 10.0);

		dataFold = new ArrayList<>();
		classificationFold = new ArrayList<>();
		// add data into folds based on size of folds (without going out of index) and type of fold
		for(int i = 0; i < data.size(); i += sizeOfFolds){
			dataFold.add(data.subList(i,
					i + sizeOfFolds < data.size() ? i + sizeOfFolds : data.size()));
			classificationFold.add(classification.subList(i,
					i + sizeOfFolds < data.size() ? i + sizeOfFolds : data.size()));
		}
	}

    List<List<String>> getData() {
		return data;
	}

	List<String> getClassification() {
		return classification;
	}

	List<String> getClassTypes() {
		return classTypes;
	}

	public List<List<List<String>>> getDataFold() {
		return dataFold;
	}

	public List<List<String>> getClassificationFold() {
		return classificationFold;
	}
}
