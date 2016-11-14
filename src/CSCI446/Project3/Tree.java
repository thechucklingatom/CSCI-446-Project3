package CSCI446.Project3;

import java.util.List;

/**
 * Created by thechucklingatom on 11/14/16.
 */
class Tree {
	private Node root;
	private List<Node> children;

	public Tree(){

	}

	public void addChild(Node addNode){
		children.add(addNode);
	}

	public void removeChild(Node remNode){
		children.remove(remNode);
	}
}
