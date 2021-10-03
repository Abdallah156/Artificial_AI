package code;

public class Node {

	
	State state;
	Node parentNode;
	Operator operator;
	int depth;
	int pathCost;
	
	
	public Node(State state,Node parentNode,Operator operator,int depth,int pathCost) {
			
		this.state = state;
		this.parentNode = parentNode;
		this.operator = operator;
		this.depth = depth;
		this.pathCost = pathCost;
	}
	public Node(State state,Operator operator,int depth,int pathCost) {

		this.state = state;
		this.operator = operator;
		this.depth = depth;
		this.pathCost = pathCost;
	}


	public State getState() {

		return state;
	}


	public void setState(State state) {

		this.state = state;
	}

	
	
}
