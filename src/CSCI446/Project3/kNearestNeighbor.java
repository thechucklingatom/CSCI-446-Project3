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
class kNearestNeighbor {
	private int foldToWrite;
	private Writer fileOutput;
	private DataContainer dataContainer;
	private int currentFold;
	private int testingFold;
	private Map<String, Integer> classCounter;
	private int k;
	private int correct;

	/**
	 * kNearestNeighbor Class to classify objects
	 *
	 * @param writer        Writer to output the results to.
	 * @param dataContainer Data to classify.
	 */
	kNearestNeighbor(Writer writer, DataContainer dataContainer) {
		fileOutput = writer;
		this.dataContainer = dataContainer;
		currentFold = 0;
		testingFold = 10;
		classCounter = new HashMap<>();
		//start with 10% of the list as k
		k = (int) Math.ceil(dataContainer.getDataFold().get(0).size() * .1);
		correct = 0;
		foldToWrite = 0;

		//if the list is small make k at least 3 to give a good amount neighbors.
		if (k < 3) {
			k = 3;
		}
	}

	/**
	 * Gets the classifications of all the neighbors that are in k distance from the current
	 * index.
	 *
	 * @param distanceIndices The container that holds the Distance to a point, and the index of
	 *                        that point.
	 * @return A list of the of the of the distances and their corresponding indices.
	 */
	private List<String> getNearestNeighborsClassification(List<DistanceIndex> distanceIndices) {
		//list of neighbors classes to return
		ArrayList<String> toReturn = new ArrayList<>();
		//sort the distances to make the smallest at the beginning and largest at the end.
		Collections.sort(distanceIndices);

		//get the list of classifications so we can find the classes based of the index.
		List<String> classReference = dataContainer.getClassificationFold().get(currentFold);

		//the k nearest neighbors or all the items if there aren't enough values get all the
		//neighbors.
		for (int i = 0; i < k && i < distanceIndices.size(); i++) {
			toReturn.add(classReference.get(distanceIndices.get(i).index));
		}

		//output the values if on the fold to output
		if (foldToWrite == currentFold) {
			try {
				//output the nearest classes
				fileOutput.append("k nearest neighbors classes ");
				fileOutput.append(toReturn.toString());
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get Nearest Neighbor");
			}
		}

		return toReturn;
	}

	/**
	 * gets the List of of distances from a certain point.
	 * @param index the index of the point you want to find all the distances from.
	 * @return the List of the distances and the corresponding indices that go with that distance.
	 */
	private List<DistanceIndex> getNearestNeighbors(int index) {
		//again if the fold is the one we have designated to writing. output what neighbor we are
		//trying to find.
		if (foldToWrite == currentFold) {
			try {
				fileOutput.append("Getting nearest neighbor for ");
				fileOutput.append(dataContainer.getDataFold().get(currentFold).get(index).toString());
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get Nearest Neighbor");
			}
		}

		//list that we will be returning
		ArrayList<DistanceIndex> toReturn = new ArrayList<>();

		//list for the fold we are working in
		List<List<String>> currentFoldList = dataContainer.getDataFold().get(currentFold);

		//go through the list and calculate all the distances.
		for (int i = 0; i < currentFoldList.size(); i++) {
			//skip it if it is the same point.
			if (i == index) {
				continue;
			}
			DistanceIndex temp = new DistanceIndex();
			temp.distance = calculateDistance(currentFoldList.get(i), currentFoldList.get(index));
			temp.index = i;

			toReturn.add(temp);
		}

		return toReturn;
	}

	/**
	 * Calculates the distance between two points. Uses Minkowski distance.
	 * @param point1 The first point.
	 * @param point2 The second point
	 * @return The Distance between the two points.
	 */
	private double calculateDistance(List<String> point1, List<String> point2) {
		double distance = 0;
		//calculate the distance for each attribute
		for (int i = 0; i < point1.size() && i < point2.size(); i++) {
			//Minkowski Distance
			String point1CurrentValue = point1.get(i);
			String point2CurrentValue = point2.get(i);

			//if the data is missing it treat it as a 0 in that dimension
			point1CurrentValue = point1CurrentValue.equals("?") ? "0" : point1CurrentValue;
			point2CurrentValue = point2CurrentValue.equals("?") ? "0" : point2CurrentValue;

			try {
				//if numerical data calculate the distance normally.
				distance += Math.pow(
						Double.valueOf(point1CurrentValue) - Double.valueOf(point2CurrentValue), point1.size());
			} catch (NumberFormatException ex) {
				//other wise use the byte value of the string itself
				distance += Math.pow(
						(int) point1.get(i).charAt(0) - (int) point2.get(i).charAt(0), point1.size());
			}

		}

		//take the nth root
		distance = Math.pow(Math.abs(distance), 1.0 / point1.size());

		return distance;
	}

	/**
	 * Calculate the most common class
	 * @param possibleClasses The list of possible classes by the neighbor.
	 * @return The class guess based on the neighbors
	 */
	private String getClassification(List<String> possibleClasses) {
		for (String classification : possibleClasses) {
			if (classCounter.containsKey(classification)) {
				classCounter.put(classification, classCounter.get(classification) + 1);
			} else {
				classCounter.put(classification, 1);
			}
		}

		String toReturn = "";

		//count how many times each class shows up in those neighbors
		for (String classes : classCounter.keySet()) {
			if (toReturn.isEmpty()) {
				toReturn = classes;
			} else if (classCounter.get(classes).compareTo(classCounter.get(toReturn)) == 1) {
				toReturn = classes;
			} else if (Objects.equals(classCounter.get(classes), classCounter.get(toReturn))) {
				Random random = new Random();
				if (random.nextBoolean()) {
					toReturn = classes;
				}
			}
		}

		//writing if in the correct fold
		if (foldToWrite == currentFold) {
			try {
				fileOutput.append("Calculating highest probable class");
				fileOutput.append(classCounter.toString());
				fileOutput.append("\nGuessing ");
				fileOutput.append(toReturn);
				fileOutput.append(" with ");
				fileOutput.append(String.valueOf(Double.valueOf(classCounter.get(toReturn)) /
						(double) possibleClasses.size()));
				fileOutput.append(" certainty.");
				fileOutput.append("\n");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("error printing in get Nearest Neighbor");
			}
		}

		//empty object for the next go around
		classCounter.clear();

		//return the highest probable class
		return toReturn;
	}

	/**
	 * classify all the data.
	 */
	void classify() {

		try {
			fileOutput.append("k-nearest-neighbors classification");
			fileOutput.append("\n");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("error printing in classify");
		}

		//for every fold
		for (int i = 0; i < dataContainer.getDataFold().size(); i++) {
			//go through all the data
			for (int j = 0; j < dataContainer.getDataFold().get(i).size(); j++) {
				//try to classify it based off its neighbor
				String guess =
						getClassification(getNearestNeighborsClassification(getNearestNeighbors(j)));

				//if outputting data
				if (foldToWrite == currentFold) {
					try {
						fileOutput.append("Guess :");
						fileOutput.append(guess);
						fileOutput.append("\nCorrect Class: ");
						fileOutput.append(dataContainer.getClassificationFold().get(i).get(j));
						fileOutput.append("\n");
					} catch (IOException ex) {
						ex.printStackTrace();
						System.out.println("error printing in classify");
					}
				}

				//if it is correct it classified it correctly. Log that
				if (dataContainer.getClassificationFold().get(i).get(j).equals(guess)) {
					correct++;
					//print if outputting.
					if (foldToWrite == currentFold) {
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
				} else {
					//it was incorrect, print that if fold to print
					if (foldToWrite == currentFold) {
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
			//move to the next fold
			currentFold++;
			//reset the count of the correct classifications
			correct = 0;
		}
	}
}
