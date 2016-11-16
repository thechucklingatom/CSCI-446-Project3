package CSCI446.Project3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thechucklingatom on 11/14/16.
 */
class Node {
	//some sort of information to hold

	private String data;
	private boolean isClass;

	private List<Node> children;
	private Node parent;
	public Node(){
		children = new ArrayList<>();
		parent = null;
	}

	public List<Node> getChildren() {
		return children;
	}

	public Node getParent() {
		return parent;
	}

	public void addChild(Node toAdd){
		toAdd.parent = this;
		children.add(toAdd);
	}

	public void removeChild(Node toRemove){
		children.remove(toRemove);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isClass() {
		return isClass;
	}

	public void setClass(boolean aClass) {
		isClass = aClass;
	}
}
