package CSCI446.Project3;

import com.sun.glass.ui.Size;

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
 * @author Robert Putnam
 */
class DataContainer {

	private List<List<String>> data;
	private List<String> classification;
	private List<String> classTypes;  // names of classifiers for data set
	private List<List<List<String>>> dataFold;
	private List<List<String>> classificationFold;

	/**
	 * Object to hold all the data from the file.
	 */
	DataContainer(){
		data = new ArrayList<>();
		classification = new ArrayList<>();
		classTypes = new ArrayList<>();
	}

	/**
	 * Get the data from the files formatted as a csv.
	 * @param filePath Path of the file.
	 * @param classificationLocation What column (zero indexed) contains the classification.
	 */
	void populateData(String filePath, int classificationLocation){
        // setup FileReader for reading in CSV files
		ArrayList<String> rows = new ArrayList<>();
		FileReader fileReader = null;
		try {
			//tries to open the file
			fileReader = new FileReader(filePath);
		} catch (FileNotFoundException e) {
            // double check directories and confirm filePaths listed in Main.java
			//if the file doesn't exist show the error and exit the program.
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
		// shuffle data around (used in 10-fold verification), makes sure the folds are random
		// for each run.
		Collections.shuffle(rows);

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
		//gets all the possible class types with no duplicates.
		classTypes = classification.stream().distinct().collect(Collectors.toList());
	}

	private void generateFolds(){
		// round data into larger groups to minimize number of small folds.
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

	//getters and setters
    List<List<String>> getData() {
		return data;
	}

	/**
	 * gets the total list that has the classes corresponding to the data
	 *
	 */
	List<String> getClassification() {
		return classification;
	}

	/**
	 * the unique list of class
	 *
	 */
	List<String> getClassTypes() {
		return classTypes;
	}

	/**
	 * the list that contains the partitioned data
	 *
	 */
	List<List<List<String>>> getDataFold() {
		return dataFold;
	}

	/**
	 * the list that contains the partitioned classes that correspond to the data rows.
	 *
	 */
	List<List<String>> getClassificationFold() {
		return classificationFold;
	}

	/**
	 * Transposes a list to group attributes instead of rows of data.
	 * @param toTranspose the Matrix to transpose
	 * @return The transposed lists.
	 */
	List<List<String>> transposeList(List<List<String>> toTranspose){
		List<List<String>> toReturn = new ArrayList<>();
		for(int i = 0; i < toTranspose.get(0).size(); i++){
			ArrayList<String> column = new ArrayList<>();
			for (int j = 0; j < toTranspose.size(); j++) {
				List<String> row = toTranspose.get(j);
				column.add(row.get(i));
			}
			toReturn.add(column);
		}

		return toReturn;
	}
}
