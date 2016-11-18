package CSCI446.Project3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	private static final ArrayList<String> fileNameList = new ArrayList<>();
	private static final ArrayList<Integer> classificationLocationList = new ArrayList<>();
	private static final String CANCER = "data" + File.separator + "breast-cancer-wisconsin.data.txt";
	private static final int CANCER_CLASS = 10;
	private static final String GLASS = "data" + File.separator + "glass.data.txt";
	private static final int GLASS_CLASS = 10;
	private static final String HOUSE = "data" + File.separator + "house-votes-84.data.txt";
	private static final int HOUSE_CLASS = 0;
	private static final String IRIS = "data" + File.separator + "iris.data.txt";
	private static final int IRIS_CLASS = 4;
	private static final String SOYBEAN = "data" + File.separator + "soybean-small.data.txt";
	private static final int SOYBEAN_CLASS = 35;

	public static void main(String[] args) {
		// write your code here

		fileNameList.add(CANCER);
		fileNameList.add(GLASS);
		fileNameList.add(HOUSE);
		fileNameList.add(IRIS);
		fileNameList.add(SOYBEAN);

		classificationLocationList.add(CANCER_CLASS);
		classificationLocationList.add(GLASS_CLASS);
		classificationLocationList.add(HOUSE_CLASS);
		classificationLocationList.add(IRIS_CLASS);
		classificationLocationList.add(SOYBEAN_CLASS);

		FileWriter fileWriter = null;

		String outputFilePath = "runs" + File.separator + "testRuns.txt";


		try {

			fileWriter = new FileWriter(outputFilePath);
			for (int i = 0; i < fileNameList.size(); i++) {

				fileWriter.append("Analyzing ");
				fileWriter.append(fileNameList.get(i));
				fileWriter.append("\n");
				DataContainer container = new DataContainer();
				container.populateData(fileNameList.get(i), classificationLocationList.get(i));

				kNearestNeighbor kNearestNeighbor = new kNearestNeighbor(fileWriter, container);

				kNearestNeighbor.classify();

				NaiveBayes naiveBayes = new NaiveBayes(fileWriter, container);
				naiveBayes.classify();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("could not find file");
			System.exit(2);
		}

		if (fileWriter != null) {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
