package CSCI446.Project3;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Created by thechucklingatom on 11/11/2016.
 *
 * @author thechucklingatom
 */
public class kNearestNeighbor {
	private Writer fileOutput;
	private DataContainer dataContainer;
	private int currentFold;
	private int testingFold;
	private Map<String, Integer> classCounter;
	private int k;

	kNearestNeighbor(Writer writer, DataContainer dataContainer) {
		fileOutput = writer;
		this.dataContainer = dataContainer;
		currentFold = 0;
		testingFold = 10;
		classCounter = new HashMap<>();
		k = (int)Math.ceil(dataContainer.getDataFold().get(0).size() * .1);
	}

	List<String> getNearestNeighborsClassification(List<DistanceIndex> distanceIndices) {
		ArrayList<String> toReturn = new ArrayList<>();
		Collections.sort(distanceIndices);

		List<String> classReference = dataContainer.getClassificationFold().get(currentFold);

		for(int i = 0; i < k; i++){
			toReturn.add(classReference.get(distanceIndices.get(i).index));
		}
		return toReturn;
	}

	List<DistanceIndex> getNearestNeighbors(int index) {
		ArrayList<DistanceIndex> toReturn = new ArrayList<>();

		List<List<String>> currentFoldList = dataContainer.getDataFold().get(currentFold);

		for (int i = 0; i < currentFoldList.size(); i++) {
			if(i == index){
				continue;
			}
			DistanceIndex temp = new DistanceIndex();
			temp.distance = calculateDistance(currentFoldList.get(i), currentFoldList.get(index));
			temp.index = i;
		}


		return toReturn;
	}

	double calculateDistance(List<String> point1, List<String> point2) {
		return 0;
	}

	String getClassification(List<String> possibleClasses) {
		for(String classification : possibleClasses){
			if(classCounter.containsKey(classification)){
				classCounter.put(classification, classCounter.get(classification) + 1);
			}else{
				classCounter.put(classification, 1);
			}
		}

		String toReturn = "";

		for(String classes : classCounter.keySet()){
			if(toReturn.isEmpty()){
				toReturn = classes;
			}else if(classCounter.get(classes).compareTo(classCounter.get(toReturn)) == 1){
				toReturn = classes;
			}else if(Objects.equals(classCounter.get(classes), classCounter.get(toReturn))){
				Random random = new Random();
				if(random.nextBoolean()){
					toReturn = classes;
				}
			}
		}

		classCounter.clear();

		return toReturn;
	}

	void classify(){
		for (int i = 0; i < dataContainer.getDataFold().size(); i++) {
			for(int j = 0; j < dataContainer.getDataFold().get(i).size(); j++){
				//// TODO: 11/14/2016 put classification logic here
			}
		}
	}
}
