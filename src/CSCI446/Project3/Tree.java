package CSCI446.Project3;

import java.util.List;

/**
 * Created by thechucklingatom on 11/14/16.
 */
class Tree {
	private Node root;
	private List<Node> children;
	private List<Node> branches;

	public Tree(Node root){
		this.root = root;
	}

	public void setRoot(Node root) { this.root = root;}

	public Node getRoot() { return root;}

	public void addChild(Node addNode){
		children.add(addNode);
	}

	public void removeChild(Node remNode){
		children.remove(remNode);
	}

	public List<Node> getChildren(){return children;}

	public void addBranch(Node addNode){branches.add(addNode);}

	public void removeBranch(Node remNode){branches.remove(remNode);}

	public List<Node> getBranches(){return branches;}
}
