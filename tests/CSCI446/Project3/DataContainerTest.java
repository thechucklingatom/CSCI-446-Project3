package CSCI446.Project3;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DataContainerTest {

	@Test
	public void testCancerGet() {
		DataContainer container = new DataContainer();

		final String CANCER = "data" + File.separator + "breast-cancer-wisconsin.data.txt";

		container.populateData(CANCER, 10);

		List<List<String>> data = container.getData();

		List<String> classification = container.getClassification();

		String[] dataToFind = {"1000025", "5", "1", "1", "1", "2", "1", "3", "1", "1"};

		assertTrue(data.contains(Arrays.asList(dataToFind)));

		assertTrue(classification.get(data.indexOf(Arrays.asList(dataToFind))).equals("2"));

		assertTrue(container.getClassTypes().size() == 2);

	}

	@Test
	public void testFold(){
		DataContainer container = new DataContainer();

		final String CANCER = "data" + File.separator + "breast-cancer-wisconsin.data.txt";

		container.populateData(CANCER, 10);

		List<List<String>> data = container.getData();

		List<List<List<String>>> dataFolds = container.getDataFold();

		boolean everyDataListContained = false;

		assertTrue(dataFolds.size() == 10);

		for(List<String> dataRow : data){
			for(List<List<String>> foldRow : dataFolds){
				if(foldRow.contains(dataRow)){
					everyDataListContained = true;
					break;
				}
			}
			assertTrue(everyDataListContained);
			everyDataListContained = false;
		}

		List<String> classes = container.getClassification();

		List<List<String>> classFolds = container.getClassificationFold();

		assertTrue(classFolds.size() == 10);

		boolean everyClassListContained = false;
		for(String classRow : classes){
			for(List<String> foldRow : classFolds){
				if(foldRow.contains(classRow)){
					everyClassListContained = true;
					break;
				}
			}
			assertTrue(everyClassListContained);
			everyClassListContained = false;
		}
	}


}
