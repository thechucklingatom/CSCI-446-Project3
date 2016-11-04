package CSCI446.Project3;

import java.io.File;
import java.util.List;

public class Main {

	static final String CANCER = "data" + File.separator + "breast-cancer-wisconsin.data.txt";

	public static void main(String[] args) {
	// write your code here
		DataContainer container = new DataContainer();
		container.populateData(CANCER, 10);

		List data = container.getData();
		List<String> classification = container.getClassification();

		data.forEach(System.out::println);

		classification.forEach(System.out::println);

	}
}
