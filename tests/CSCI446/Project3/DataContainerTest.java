package CSCI446.Project3;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


}
