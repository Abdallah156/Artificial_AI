package code;

import java.util.Comparator;

public class NodeCostComparator implements Comparator<Node> {

	@Override
	public int compare(Node n1, Node n2) {
		if(n1.pathCost > n2.pathCost)
			return 1;
		else if(n1.pathCost < n2.pathCost)
			return -1;
		return 0;
	}
	

	
}
