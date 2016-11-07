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
 * Created by thechucklingatom on 11/4/16.
 *
 * @author thechucklingatom
 */
class DataContainer {

	private List<List<String>> data;
	private List<String> classification;
	private List<String> classTypes;

	DataContainer(){
		data = new ArrayList<>();
		classification = new ArrayList<>();
		classTypes = new ArrayList<>();
	}

	void populateData(String filePath, int classificationLocation){
		ArrayList<String> rows = new ArrayList<>();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File not found, exiting");
			System.exit(2);
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line;

		try {
			while((line = bufferedReader.readLine()) != null){
				rows.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading from file, exiting");
			System.exit(3);
		}

		Collections.shuffle(rows);

		for (String row : rows) {
			row = row.replace("\n", "").replace("\r", "");
			String[] columns = row.split(",");

			ArrayList<String> toPutInData = new ArrayList<>();

			for(int i = 0; i < columns.length; i++){
				if(i == classificationLocation){
					classification.add(columns[i]);
				}else{
					toPutInData.add(columns[i]);
				}
			}

			data.add(toPutInData);
		}

		fillUniqueClassList();
	}

	private void fillUniqueClassList() {
		classTypes = classification.stream().distinct().collect(Collectors.toList());
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
}
