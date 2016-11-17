package CSCI446.Project3;

import java.io.IOException;
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
	private int foldToWrite;	// which fold are we actually writing on
	private Writer fileOutput;
	private DataContainer dataContainer;
	private int currentFold;
	private int testingFold;
	private Map<String, Integer> classCounter;
	private int k;
	private int correct;

	kNearestNeighbor(Writer writer, DataContainer dataContainer) {
		fileOutput = writer;
		this.dataContainer = dataContainer;
		currentFold = 0;
		testingFold = 10;
		classCounter = new HashMap<>();
		k = (int)Math.ceil(dataContainer.getDataFold().get(0).size() * .1);
		correct = 0;
		foldToWrite = 0;

		if(k < 3){
			k = 3;
		}
	}

	List<String> getNearestNeighborsClassification(List<DistanceIndex> distanceIndices) {
		ArrayList<String> toReturn = new ArrayList<>();
		Collections.sort(distanceIndices);

		List<String> classReference = dataContainer.getClassificationFold().get(currentFold);

		for(int i = 0; i < k && i < distanceIndices.size(); i++){
			toReturn.add(classReference.get(distanceIndices.get(i).index));
		}

		if(foldToWrite == currentFold){
			try {
				fileOutput.append("k nearest neighbors classes ");
				fileOutput.append(toReturn.toString());
				fileOutput.append("\n");
			} catch (IOException ex){
				ex.printStackTrace();
				System.out.println("error printing in get Nearest Neighbor");
			}
		}

		return toReturn;
	}

	List<DistanceIndex> getNearestNeighbors(int index) {
		if(foldToWrite == currentFold){
			try {
				fileOutput.append("Getting nearest neighbor for ");
				fileOutput.append(dataContainer.getDataFold().get(currentFold).get(index).toString());
				fileOutput.append("\n");
			} catch (IOException ex){
				ex.printStackTrace();
				System.out.println("error printing in get Nearest Neighbor");
			}
		}

		ArrayList<DistanceIndex> toReturn = new ArrayList<>();

		List<List<String>> currentFoldList = dataContainer.getDataFold().get(currentFold);

		for (int i = 0; i < currentFoldList.size(); i++) {
			if(i == index){
				continue;
			}
			DistanceIndex temp = new DistanceIndex();
			temp.distance = calculateDistance(currentFoldList.get(i), currentFoldList.get(index));
			temp.index = i;

			toReturn.add(temp);
		}


		return toReturn;
	}

	double calculateDistance(List<String> point1, List<String> point2) {
		double distance = 0;
		for(int i = 0; i < point1.size() && i < point2.size(); i++){
			//Minkowski Distance
			String point1CurrentValue = point1.get(i);
			String point2CurrentValue = point2.get(i);

			point1CurrentValue = point1CurrentValue.equals("?") ? "0" : point1CurrentValue;
			point2CurrentValue = point2CurrentValue.equals("?") ? "0" : point2CurrentValue;

			try {
				distance += Math.pow(
						Double.valueOf(point1CurrentValue) - Double.valueOf(point2CurrentValue), point1.size());
			} catch (NumberFormatException ex) {
				distance += Math.pow(
						(int)point1.get(i).charAt(0) - (int)point2.get(i).charAt(0), point1.size());
			}

		}

		distance = Math.pow(Math.abs(distance), 1.0 / point1.size());

		return distance;
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

		if(foldToWrite == currentFold){
			try {
				fileOutput.append("Calculating highest probable class");
				fileOutput.append(classCounter.toString());
				fileOutput.append("\nGuessing ");
				fileOutput.append(toReturn);
				fileOutput.append("\n");
			} catch (IOException ex){
				ex.printStackTrace();
				System.out.println("error printing in get Nearest Neighbor");
			}
		}

		classCounter.clear();

		return toReturn;
	}

	void classify(){
		for (int i = 0; i < dataContainer.getDataFold().size(); i++) {
			for(int j = 0; j < dataContainer.getDataFold().get(i).size(); j++){
				String guess =
						getClassification(getNearestNeighborsClassification(getNearestNeighbors(j)));

				if(foldToWrite == currentFold){
					try {
						fileOutput.append("Guess :");
						fileOutput.append(guess);
						fileOutput.append("\nCorrect Class: ");
						fileOutput.append(dataContainer.getClassificationFold().get(i).get(j));
						fileOutput.append("\n");
					} catch (IOException ex){
						ex.printStackTrace();
						System.out.println("error printing in classify");
					}
				}

				if(dataContainer.getClassificationFold().get(i).get(j).equals(guess)){
					correct++;
					if(foldToWrite == currentFold) {
						try {
							fileOutput.append("Correct guess! Total number of correct guesses ");
							fileOutput.append(String.valueOf(correct));
							fileOutput.append("\nTotal Guesses ");
							fileOutput.append(String.valueOf(j + 1));
							fileOutput.append("\nPercent guessed correctly: ");
							fileOutput.append(String.valueOf((double) correct / (double) (j + 1)));
							fileOutput.append("\n");
						} catch (IOException ex) {
							ex.printStackTrace();
							System.out.println("error printing in classify");
						}
					}
				}else{

					if(foldToWrite == currentFold) {
						try {
							fileOutput.append("Incorrect guess. Total number of correct guesses ");
							fileOutput.append(String.valueOf(correct));
							fileOutput.append("\nTotal Guesses ");
							fileOutput.append(String.valueOf(j + 1));
							fileOutput.append("\nPercent guessed correctly: ");
							fileOutput.append(String.valueOf((double) correct / (double) (j + 1)));
							fileOutput.append("\n");
						} catch (IOException ex) {
							ex.printStackTrace();
							System.out.println("error printing in classify");
						}
					}
				}
			}
			currentFold++;
			correct = 0;
		}
	}
}
