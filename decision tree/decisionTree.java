import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Shivu Gururaj
 */
public class decisionTree {

	public static void main(String[] args) throws Exception {
		//entry of execution
		int numberOfNodes = Integer.parseInt(args[0]);
		ArrayList<ArrayList<Data>> trainingDataSet = readFile(args[1]);
		ArrayList<ArrayList<Data>> validationDataSet = readFile(args[2]);
		ArrayList<ArrayList<Data>> testDataSet = readFile(args[3]);
		int bool = Integer.parseInt(args[4]);
		//builds tree on training data set
		ID3 id3 = new ID3();
		id3.buildId3Tree(trainingDataSet, new Node());
		System.out.println("Accuracy before pruning: " + id3.id3Accuracy(testDataSet, id3.root) + "%\n");
		if (bool == 1) {
			System.out.println("Tree before pruning: ");
			System.out.println(id3);
		}

		id3.postPruning(numberOfNodes, validationDataSet);
		System.out.println("Accuracy after pruning: " + id3.id3Accuracy(testDataSet, id3.root) + "%\n");

		if (bool == 1) {
			System.out.println("After pruning: ");
			System.out.println(id3);
		}

	}

	private static ArrayList<ArrayList<Data>> readFile(String filePath) {
		//this function reads the file and its content
		ArrayList<ArrayList<Data>> dataValues = new ArrayList<>();
		BufferedReader bufferReader = null;
		String attrValues = "";

		try {
			bufferReader = new BufferedReader(new FileReader(filePath));
			String[] attrName = bufferReader.readLine().split(",");

			while ((attrValues = bufferReader.readLine()) != null) {

				String[] splitValues = attrValues.split(",");

				ArrayList<Data> parametersList = new ArrayList<>();
				for (int i = 0; i < splitValues.length; i++) {
					parametersList.add(new Data(attrName[i], Integer.parseInt(splitValues[i])));
				}
				dataValues.add(parametersList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return dataValues;
		}
	}
}

class Data {
	public String name;
	public int value;

	public Data(String name, int value) {
		this.name = name;
		this.value = value;
	}
}

class ID3 {
	
	private int index;
	public Node root;

	private void findDelete(Node root, int index) {
		//find and remove for postpruning
		if (root.index == index) {
			if (root.children[0].data.size() > root.children[1].data.size()) {
				int[] valuecount = new int[2];
				for (ArrayList<Data> data : root.children[0].data) {
					valuecount[data.get(data.size() - 1).value]++;
				}
				root.children[0].value = valuecount[0] > valuecount[1] ? 0 : 1;
				root.children[0].children = null;
				root.children[0].index = -1;
			} else {
				int[] valueCount = new int[2];
				for (ArrayList<Data> data : root.children[1].data) {
					valueCount[data.get(data.size() - 1).value]++;
				}

				root.children[1].value = valueCount[0] > valueCount[1] ? 0 : 1;
				root.children[1].children = null;
				root.children[1].index = -1;
			}
		} else if (root.children != null) {
			findDelete(root.children[0], index);
			findDelete(root.children[1], index);
		}
	}

	private boolean validateValues(Node root, ArrayList<Data> data) {

		if (root.children == null) {
			return data.get(data.size() - 1).value == root.value;
		} else {
			int paramVal = -1;
			for (Data dataVal : data) {
				if (dataVal.name.equals(root.ruleParameter.name)) {
					paramVal = dataVal.value;
					break;
				}
			}
			return validateValues(root.children[paramVal], data);
		}
	}

	public void postPruning(int nodesToPrune, ArrayList<ArrayList<Data>> data) throws CloneNotSupportedException {
		//performs post pruning
		Node efficientNode = (Node) root.clone();
		for (int i = 0; i < nodesToPrune; i++) {
			Node copyOfNode = (Node) root.clone();
			Random rand = new Random();
			int randomNumberInRange = rand.nextInt(index);
			findDelete(copyOfNode, randomNumberInRange);
			if (id3Accuracy(data, copyOfNode) > id3Accuracy(data, efficientNode)) {
				efficientNode = (Node) copyOfNode.clone();
			}
		}
		root = (Node) efficientNode.clone();
	}

	public double id3Accuracy(ArrayList<ArrayList<Data>> data, Node root) {
		//returns accuracy
		double accuracy = 0.0;
		for (ArrayList<Data> dataList : data) {
			if (validateValues(root, dataList)) {
				accuracy++;
			}
		}
		return (accuracy * 100 / data.size());
	}

	private String displayTree(Node node, int nodeLevel) {
		//prints the tree
		StringBuilder stringBuilder = new StringBuilder();

		for (int j = 0; j < node.children.length; j++) {
			for (int i = 0; i < nodeLevel; i++) {
				stringBuilder.append("| ");
			}

			stringBuilder.append(node.ruleParameter.name + " = " + j + " :");

			if (node.children[j].children != null) {
				stringBuilder.append("\n" + displayTree(node.children[j], nodeLevel + 1));
			} else {
				stringBuilder.append(" " + node.children[j].value + "\n");
			}
		}

		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		return displayTree(this.root, 0);
	}

	@SuppressWarnings("unchecked")
	public void buildId3Tree(ArrayList<ArrayList<Data>> dataValues, Node node) {
		//function to build the tree
		ArrayList<ArrayList<Data>> right = new ArrayList<>();
		ArrayList<ArrayList<Data>> left = new ArrayList<>();
		Data buildingRule = null;
		double maximumGain = 0.0;
		int index = -1;
		node.entropy = findEntropy(dataValues);

		for (int i = 0; i < dataValues.get(0).size() - 1; i++) {
			ArrayList<ArrayList<Data>> leftSet = new ArrayList<>();
			ArrayList<ArrayList<Data>> rightSet = new ArrayList<>();

			for (int j = 0; j < dataValues.size(); j++) {
				ArrayList<Data> myList = new ArrayList<>();
				myList.addAll(dataValues.get(j));

				if (dataValues.get(j).get(i).value == 0) {
					leftSet.add(myList);
				} else {
					rightSet.add(myList);
				}
			}

			ArrayList<Double> otherEntorpies = new ArrayList<>();
			otherEntorpies.add(findEntropy(leftSet));
			otherEntorpies.add(findEntropy(rightSet));
			ArrayList<Integer> subSetSize = new ArrayList<>();
			subSetSize.add(leftSet.size());
			subSetSize.add(rightSet.size());
			double gain = calculateInformationGain(node.entropy, otherEntorpies, subSetSize, dataValues.size());
			if ((int) (gain * 100000000) > (int) (maximumGain * 100000000)) {
				maximumGain = gain;
				buildingRule = dataValues.get(0).get(i);
				index = i;
				left = (ArrayList<ArrayList<Data>>) leftSet.clone();
				right = (ArrayList<ArrayList<Data>>) rightSet.clone();
			}
		}
		if (index > -1) {
			for (ArrayList<Data> data : left) {
				data.remove(index);
			}
			for (ArrayList<Data> data : right) {
				data.remove(index);
			}

			Node lSubTree = new Node();
			lSubTree.data = left;
			Node rSubTree = new Node();
			rSubTree.data = right;

			node.children = new Node[2];
			node.children[0] = lSubTree;
			node.children[1] = rSubTree;
			node.ruleParameter = buildingRule;
			node.index = ++this.index;

			buildId3Tree(right, rSubTree);
			buildId3Tree(left, lSubTree);

		} else {
			node.value = dataValues.get(0).get(dataValues.get(0).size() - 1).value;
			return;
		}
		this.root = node;
	}

	public double findEntropy(ArrayList<ArrayList<Data>> data) {
		//calculates entropy
		double[] numberOfValues = new double[2];
		for (ArrayList<Data> Parameter : data) {
			numberOfValues[Parameter.get(Parameter.size() - 1).value]++;
		}
		double entropy = 0;
		for (int j = 0; j < numberOfValues.length; j++) {
			if ((int) numberOfValues[j] != 0) {
				entropy -= numberOfValues[j] * (Math.log(numberOfValues[j]) / Math.log(2));
			}
		}
		entropy /= data.size();
		return entropy + (Math.log(data.size()) / Math.log(2));
	}

	public double calculateInformationGain(double currentEntropy, ArrayList<Double> subsetEntropies,
			ArrayList<Integer> subsetSize, double noOfValues) {
		//calculates information gain
		double info_gain = currentEntropy;
		for (int j = 0; j < subsetEntropies.size(); j++) {
			info_gain -= (subsetSize.get(j) / noOfValues) * subsetEntropies.get(j);
		}
		return info_gain;
	}

}

class Node implements Cloneable {
	public Node[] children;
	public int value;
	public Data ruleParameter;
	public int index;
	public double entropy;
	public ArrayList<ArrayList<Data>> data;

	public Node() {
		value = 0;
		ruleParameter = new Data("", 0);
		entropy = 0.0;
		index = -1;
		children = null;
		data = new ArrayList<>();

	}

	public Object clone() throws CloneNotSupportedException {
		Node copyOfNode = new Node();
		copyOfNode.entropy = this.entropy;
		copyOfNode.ruleParameter = new Data(this.ruleParameter.name, this.ruleParameter.value);
		copyOfNode.value = this.value;
		copyOfNode.index = this.index;

		for (ArrayList<Data> attrValues : this.data) {
			ArrayList<Data> copyOfAttrValues = new ArrayList<>();
			for (Data attrVal : attrValues) {
				copyOfAttrValues.add(new Data(attrVal.name, attrVal.value));
			}
			copyOfNode.data.add(copyOfAttrValues);
		}

		if (this.children != null) {
			copyOfNode.children = new Node[2];
			for (int i = 0; i < this.children.length; i++) {
				copyOfNode.children[i] = (Node) this.children[i].clone();
			}
		}

		return copyOfNode;
	}
}
