package CSCI446.Project3;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;

/**
 * Created by Alan Fraticelli on 11/11/2016.
 */
public class ID3 {
	//private int foldToWrite;
	private Writer writer;
	private DataContainer container;
	//private int currentFold;
	private List<List<String>> trainingSet;
	private List<List<String>> trainingSetClass;
	private List<List<String>> transposedTrainingSet;
	//private int testingFold;
	private Tree tree;
	private List<List<Bin>> binAtt; //first list reps the attributes, second list the bins of those atts
	private List<List<String>> classification;
	private List<String> classTypes;
	private final int MAX_NUM_BINS = 6;

	public ID3(Writer inWriter, DataContainer inContainer) {
		this.writer = inWriter;
		this.container = inContainer;
		tree = new Tree(new Node());
		binAtt = new ArrayList<>();
		classTypes = container.getClassTypes();
		classification = container.getClassificationFold();
	}

	//takes in a transposed training set (to make rows  attributes) and bin it
	public void discretize(List<List<String>> inFold) {
		//iterating through the attributes
		for (int i = 0; i < inFold.size(); i++) {
			//we are going to determine the size of unique data of each attribute,
			//and bin the attributes with a max of 5 unique data
			//find out how many bins we
			int numBin = findNumBin(inFold.get(i));
			/*try {
				writer.append(" Give our bins bounds and ID numbers. \n");
				System.out.println(" Give our bins bounds and ID numbers.");
			} catch (IOException x) {
			}*/
			//this will generate the bins for an attribute and "fill" them with a frequency value
			bin(inFold.get(i), numBin);
		}
	}

	public int findNumBin(List<String> inFold) {
		int answer = 0;
		HashSet noDupes = new HashSet();
		for (int j = 0; j < inFold.size(); j++) { //iterate through an attribute and develop a distinct HashSet with the values
			if (!inFold.get(j).equals("?")) { //make sure we are not adding a question mark to our HashSet
				//a HashSet only adds distinct values, so only fills with unique values
				noDupes.add(inFold.get(j));
			}
		}
		answer = noDupes.size();
		//we want to keep the amount of possible values for an attribute low, so we set a max value and hard set it
		//to that max value if it goes over. This prevents continuous data from generating giant amounts of branches and children
		if (answer < MAX_NUM_BINS) {
			return answer;
		} else {
			return MAX_NUM_BINS;
		}
	}

	public void bin(List<String> inAtt, int numBin) {
		if (!inAtt.get(0).chars().allMatch(Character::isDigit) && !inAtt.get(0).contains(".")) { //we are going to create bins that will hold only one value, this is where attributes with type String get caught
			//create a list to hold the bins we are about to create for the attribute
			List<Bin> bins = new ArrayList<>();
			//add this list to the list that holds the lists of bins
			binAtt.add(bins);
			//obtain a list of the unique values in this attribute
			List<String> uniqueValue = new ArrayList<>();
			for (String s : inAtt) { //iterate through the attribute to find a data value s
				//only add to uniqueValue if the s value is not currently in the list
				if (!uniqueValue.contains(s) && !s.equals("?")) {
					uniqueValue.add(s);
				}
			}
			//create the unique doubles that will bound our bins
			List<Double> stringHash = new ArrayList<>();
			for (String s : uniqueValue) { //iterate through our unique values
				double hash = (double) s.hashCode();
				stringHash.add(hash);
			} //create the bins
			int i;
			//create the lowest bin that catches any value lower than what's in the training data
			bins.add(new Bin((double) Integer.MIN_VALUE, stringHash.get(0), -1));
			for (i = 0; i < stringHash.size() - 1; i++) {
				//iterate through our hash and create a bin that spans from the current hash
				//number to the next has number
				Bin binToAdd = new Bin(stringHash.get(i), stringHash.get(i + 1), i);
				//to keep track of which attributes had continuous values, set the bin to false in this if()
				binToAdd.setIsCont(false);
				//add this new bin to the list of the bins that represent this attribute
				bins.add(binToAdd);
			}
			//bins.add(new Bin(stringHash.get(i), Double.MAX_VALUE, i));
			//fill the bins
			//add a last bin to catch the values that are greater than what is in the training set
			bins.add(new Bin(stringHash.get(i - 1), (double) Integer.MAX_VALUE, i));
			//fill the bins to make a sort of histogram UNUSED
            /*for (int x = 0; x < inAtt.size(); x++) {
                for (int j = 0; j < bins.size(); j++) {
                    if (bins.get(j).binContains((double) inAtt.get(x).hashCode()) && !inAtt.get(j).equals("?")) {
                        bins.get(j).incrementFreq();
                        continue;
                    }
                }
            }*/
			//findFreq(inAtt, bins, false);
		} else {
			double max = findMax(inAtt);
			double min = findMin(inAtt);
			double range = max - min;
			double binRange = range / numBin;
			double lowDiv = min;
			double nextDiv = lowDiv + binRange;
			List<Bin> bins = new ArrayList<>();
			binAtt.add(bins);
			Bin binToAdd = new Bin((double) Integer.MIN_VALUE, lowDiv, -1);
			binToAdd.setIsCont(true);
			bins.add(binToAdd);
			for (int i = 0; i < numBin; i++) {
				bins.add(new Bin(lowDiv, nextDiv, i));
				lowDiv = nextDiv;
				nextDiv = nextDiv + binRange;
			}
			bins.add(new Bin(lowDiv, (double) Integer.MAX_VALUE, numBin + 1));
			//findFreq(inAtt, bins, true);
		}
	}

	//this method must always be called before trying to use the frequency of a bin to update to the correct
	//amount considering the amount of examples left
	public void findFreq(List<Integer> inAtt, List<Bin> bins) {
		//boolean isCont = bins.get(0).isCont();
		//if(isCont) {
		for (int i = 0; i < inAtt.size(); i++) {
			for (int j = 0; j < bins.size(); j++) {
				if (inAtt.get(i) != -10 && bins.get(j).getBinID() == inAtt.get(i)) {
					bins.get(j).incrementFreq();
					break;
				}
			}
		}
		/*} else {
			for (int i = 0; i < inAtt.size(); i++) {
				for (int j = 0; j < bins.size(); j++) {
					if (inAtt.get(i) != -10 && bins.get(j).binContains(inAtt.hashCode())) {
						bins.get(j).incrementFreq();
						continue;
					}
				}
			}
		}*/
	}

	public double findMax(List<String> inAtt) {
		double answer = Integer.MIN_VALUE;
		for (int i = 0; i < inAtt.size(); i++) {
			if (!inAtt.get(i).equals("?")) {
				double curNum = Double.valueOf(inAtt.get(i));
				if (curNum > answer) {
					answer = curNum;
				}
			}
		}
		return answer;
	}

	public double findMin(List<String> inAtt) {
		double answer = Integer.MAX_VALUE;
		for (int i = 0; i < inAtt.size(); i++) {
			if (!inAtt.get(i).equals("?")) {
				double curNum = Double.valueOf(inAtt.get(i));
				if (curNum < answer) {
					answer = curNum;
				}
			}
		}
		return answer;
	}

	//this is the actual recursive method to run ID3
	public Tree id3(List<List<Integer>> examples, List<String> exampleClass, List<Integer> attributes, List<String> parentExClass) {
		Tree curTree;
		if (examples.get(0).isEmpty()) {
			String maxString = pluralityValue(parentExClass);
			curTree = new Tree(new Node());
			curTree.getRoot().setData(maxString);
			curTree.getRoot().setClass(true);
			return curTree;
		} else if (isSameClass(exampleClass)) {
			String s = exampleClass.get(0);
			curTree = new Tree(new Node());
			curTree.getRoot().setData(s);
			curTree.getRoot().setClass(true);
			return curTree;
		} else if (attributes.isEmpty()) {
			String maxString = pluralityValue(exampleClass);
			curTree = new Tree(new Node());
			curTree.getRoot().setData(maxString);
			curTree.getRoot().setClass(true);
			return curTree;
		} else {
			int attributeA = bestAttribute(examples, exampleClass, attributes);
			curTree = new Tree(new Node());
			curTree.getRoot().setData(attributes.get(attributeA).toString());
			List<Integer> subExamplesIndex;
			for (int i = 0; i < binAtt.get(attributeA).size(); i++) { //iterate through the bins of A (the vk value)
				subExamplesIndex = new ArrayList<>();
				for (int j = 0; j < examples.get(attributeA).size(); j++) { //iterate through our examples to find our subsets to rec call on
					int binId = binAtt.get(attributeA).get(i).getBinID();
					if (binId == examples.get(attributeA).get(j)) { //see if this example is going to be in our vk subset
						subExamplesIndex.add(j); //if so, add the index for future adding
					}
				}
				List<List<Integer>> subExamples = new ArrayList<>(); //we need to make a new list to send through
				for (int x = 0; x < attributes.size(); x++) {//iterate the columns of examples (same as attribute)
					List<Integer> column = examples.get(x);
					List<Integer> subColumn = new ArrayList<>();
					for (int y = 0; y < column.size(); y++) { //then iterate through that column and make a subColumn selecting the previous indexes
						if (subExamplesIndex.contains(y)) { //make sure the current index is in our indexes
							subColumn.add(column.get(y));
						}
					}
					subExamples.add(subColumn); //then add our subColumn to our subExamples
				} //we now have a subSet of examples that are of bin i in attributeA
				List<String> subClassifications = new ArrayList<>(); //we need to grab a list of the classes that correspond as well
				for (int x = 0; x < exampleClass.size(); x++) {
					if (subExamplesIndex.contains(x)) {
						subClassifications.add(exampleClass.get(x));
					}
				}
				//now we make a list of the subAttributes
				List<Integer> subAttributes = new ArrayList<>();
				for (int x = 0; x < attributes.size(); x++) {
					if (attributes.get(x) == attributes.get(attributeA)) {
					} //don't add the attribute we just subdivided on
					else {
						subAttributes.add(attributes.get(x));
					}
				}
				Tree subTree = id3(subExamples, subClassifications, subAttributes, exampleClass);
				//Node nextBranch = new Node();
				//nextBranch.setData(binAtt.get(attributeA).get(i).getBranch());
				//curTree.addBranch(nextBranch);
				curTree.addIdBranch(binAtt.get(attributeA).get(i));
				curTree.addSubtree(subTree);
			}
			return curTree;
		}
	}

	public String pluralityValue(List<String> parentExClass) {
		List<String> uniqueClass = new ArrayList<>();
		List<Integer> classFreq = new ArrayList<>();
		for (String s : parentExClass) {
			if (!uniqueClass.contains(s)) {
				uniqueClass.add(s);
				classFreq.add(1);
			} else {
				int i = uniqueClass.indexOf(s);
				int oldVal = classFreq.get(i);
				classFreq.set(i, oldVal + 1);
			}
		}
		int maxFreq = Integer.MIN_VALUE;
		String maxString = "";
		for (int i = 0; i < classFreq.size(); i++) {
			if (maxFreq < classFreq.get(i)) {
				maxFreq = classFreq.get(i);
				maxString = uniqueClass.get(i);
			}
		}
		return maxString;
	}

	public boolean isSameClass(List<String> exampleClass) {
		String testS = exampleClass.get(0);
		for (String s : exampleClass) {
			if (!s.equals(testS)) {
				return false;
			}
		}
		return true;
	}

	public List<String> binToString(List<Integer> inList, int attNum) {
		List<String> outList = new ArrayList<>();
		List<String> fullAttList = transposedTrainingSet.get(attNum);
		for (int i = 0; i < inList.size(); i++) { //iterate through our list of Integers

		}
		return outList;
	}

	public int bestAttribute(List<List<Integer>> examples, List<String> exampleClass, List<Integer> attributes) {
		//iterate through our attributes and find their gain
		List<Double> attributeGain = new ArrayList<>();
		for (int i = 0; i < attributes.size(); i++) {
			List<String> uniqueClass = new ArrayList<>();
			for (String s : exampleClass) {
				if (!uniqueClass.contains(s)) {
					uniqueClass.add(s);
				}
			}
			int attNum = attributes.get(i);
			List<Integer> attColumn = examples.get(i);
			List<Bin> attBins = binAtt.get(attNum);
			List<List<Integer>> binsOfFreq = new ArrayList<>(uniqueClass.size());
			List<Integer> exClassFreq;
			for (int j = 0; j < attBins.size(); j++) { //iterate through the bins for target attribute
				exClassFreq = new ArrayList<>();
				for (String s : uniqueClass) { //intial population of exClassFreq
					exClassFreq.add(0);
				}
				binsOfFreq.add(exClassFreq);
				for (int k = 0; k < attColumn.size(); k++) { //iterate through the column of target attribute
					if (attBins.get(j).getBinID() == attColumn.get(k)) {
						for (int x = 0; x < uniqueClass.size(); x++) { //iterate through our uniqueClass list to find necessary index
							if (exampleClass.get(k).equals(uniqueClass.get(x))) {
								int temp = exClassFreq.get(x);
								temp++;
								exClassFreq.set(x, temp);
								x = uniqueClass.size();
							}
						}
					}
				}
			} //we now have all the necessary frequencies contained in a list representing the attribute values (bins) indexes
			//and holding integer lists that represent the frequency of a corresponding class in uniqueClass
			double sum = 0; //represents the H of the given attribute
			for (int x = 0; x < binsOfFreq.size(); x++) { //iterate through the bins of given attribute, finds Hsunny
				int denominator = 0;
				for (int j = 0; j < uniqueClass.size(); j++) { //iterate through the j classes we have
					denominator = denominator + binsOfFreq.get(x).get(j);
				}
				if (denominator != 0) {
					for (int j = 0; j < uniqueClass.size(); j++) { //sum over j classes
						double numerator = binsOfFreq.get(x).get(j);
						double term = (numerator / denominator) * (Math.log(numerator / denominator) / Math.log(2));
						sum = sum - term;
					}
				}
			}
			attributeGain.add(sum);
			//find remain
			//find entropy - remain, or gain
		} //we now have the gain of each attribute
		int minIndex = 0;
		double minNum = Double.MIN_VALUE;
		for (int i = 0; i < attributeGain.size(); i++) {

			if (minNum < attributeGain.get(i)) {
				minNum = attributeGain.get(i);
				minIndex = i;
			}
		}
		return minIndex;
	}

	//this will be the method that mostly runs the algorithm, as it will perform our verification
	public void tenFold() {
		List<List<List<String>>> allFolds = container.getDataFold();
		//iterate which fold is currently the test set
		for (int i = 0; i < 10; i++) {
			trainingSet = new ArrayList<>();
			trainingSetClass = new ArrayList<>();
			/*try {
				writer.append("Test Fold = " + i + "\n");
			} catch (IOException x) {
			}*/
			//combine the training folds into one fold j=folds, k=rows to add
			for (int j = 0; j < 10; j++) {
				for (int k = 0; k < allFolds.get(j).size(); k++) {
					if (j == i) {
					} else {
						trainingSet.add(allFolds.get(j).get(k));
					}
				}
			}
			//transpose that fold to make the attributes the rows
			transposedTrainingSet = container.transposeList(trainingSet);
			//call discretize sending our fold
			discretize(transposedTrainingSet);
			//then we learn
			List<Integer> attributes = new ArrayList<>();
			int x = 0;
			for (List<String> n : transposedTrainingSet) {
				attributes.add(x);
				x++;
			}
			List<List<Integer>> binMap = makeBinMap(transposedTrainingSet);
			//container.transposeList(trainingSet);
			List<String> classCol = combineClassificationFolds(i);

			tree = id3(binMap, classCol, attributes, null);
			//then we prune
			//then we test (a run)
			test(allFolds.get(i), container.getClassificationFold().get(i), tree);
		}
	}

	public List<String> combineClassificationFolds(int i) {
		List<String> combClass = new ArrayList<>();
		for (int j = 0; j < 10; j++) {
			for (int k = 0; k < classification.get(j).size(); k++)
				if (j == i) {
				} else {
					combClass.add(classification.get(j).get(k));
				}
		}
		return combClass;
	}

	private List<List<Integer>> makeBinMap(List<List<String>> inSet) {
		List<List<Integer>> map = new ArrayList<>();
		for (int i = 0; i < inSet.size(); i++) { //iterates attributes
			List<Integer> attribute = new ArrayList<>();
			map.add(attribute);
			for (int j = 0; j < inSet.get(i).size(); j++) { //iterates the row of the selected att
				attribute.add(0);
				//differentiate what type of attribute this is, disc or cont
				if (binAtt.get(i).get(0).isCont()) {
					for (int k = 0; k < binAtt.get(i).size(); k++) { //iterate through the bins
						if (inSet.get(i).get(j).equals("?")) {
							map.get(i).set(j, -10);
						} else if (binAtt.get(i).get(k).binContains(Double.valueOf(inSet.get(i).get(j)))) {
							int binNum = binAtt.get(i).get(k).getBinID();
							attribute.set(j, binNum);
						}
					}
				} else {
					for (int x = 0; x < binAtt.get(i).size(); x++) {
						if (inSet.get(i).get(j).equals("?")) {
							map.get(i).set(j, -10);
						} else if (binAtt.get(i).get(x).binContains((double) inSet.get(i).get(j).hashCode())) {
							int binNum = binAtt.get(i).get(x).getBinID();
							map.get(i).set(j, binNum);
						}
					}
				}
			}
		}
		return map;
	}

	//important to note that inFold is a list of rows, not a list of columns
	public void test(List<List<String>> inFold, List<String> classFold, Tree decisionTree) {
		int correct = 0;
		int fail = 0;
		for (int i = 0; i < inFold.size(); i++) { //iterate through the rows
			boolean done = false;
			String predicted = "";
			while (!done) { //test one row
				if(decisionTree.getRoot().isClass()) {
					done = true;
					predicted = decisionTree.getRoot().getData();

				} else {
					int attNum = Integer.valueOf(decisionTree.getRoot().getData()); //grab the att number of the current subtree's root
					String s = inFold.get(i).get(attNum); //grab the data in that attribute for this row
					boolean isCont = decisionTree.getIdBranches().get(0).isCont();
					if (isCont) { //iterate through the bins and find which one
						double value = Double.valueOf(s);
						for (int j = 0; j < decisionTree.getIdBranches().size(); j++) {
							Bin b = decisionTree.getIdBranches().get(j);
							if (b.binContains(value)) {
								decisionTree = decisionTree.getSubtrees().get(j);
								break;
							}
						}
					} else {
						double hash = (double) s.hashCode();
						for (int j = 0; j < decisionTree.getIdBranches().size(); j++) {
							Bin b = decisionTree.getIdBranches().get(j);
							if (b.binContains(hash)) {
								decisionTree = decisionTree.getSubtrees().get(j);
								break;
							}
						}
					}
				}
			} if(predicted.equals(classFold.get(i))){
				correct++;
			} else {
				fail++;
			}
		}
		double percentage =(double) correct / (double) (correct + fail);
		try{
			writer.append(Double.toString(percentage) + "\n");
		} catch(IOException x){}
	}
}