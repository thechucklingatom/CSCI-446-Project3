package CSCI446.Project3;

import java.io.File;
import java.util.List;

public class Main {

	private static final String CANCER = "data" + File.separator + "breast-cancer-wisconsin.data.txt";
	private static final String GLASS = "data" + File.separator + "glass.data.txt";

	public static void main(String[] args) {
	// write your code here
		DataContainer container = new DataContainer();
		container.populateData(GLASS, 10);

		List<List<String>> data = container.getData();
		List<String> classification = container.getClassification();

		data.forEach(System.out::println);

		classification.forEach(System.out::println);

	}
}
