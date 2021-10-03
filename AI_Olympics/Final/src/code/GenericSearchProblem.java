package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public abstract class GenericSearchProblem {



    private static boolean firstRun=true;
    private static String gridSize;
    private static String generatedGrid;
    private static int gridX; // x of grid
    private static int gridY; // y of grid
    private static String jarvisLocation; // starting location
    private static int startX; // starting x value
    private static int startY; // starting y value

    private static String flameLocation;
    private static int flameX; // x of flame
    private static int flameY; // y of flame
    private static String componentsCellLocations; // contains all the locations of the components;
    private static String poisonCellLocations; // contains all the locations of the components;
    private static String plan="";//contains the plan
    private static ArrayList<Node> queue=new ArrayList<Node>(); // contains the queue

    private static boolean flameLit=false; // true if flame is lit
    private static boolean componentsCollected=false; //true if components are collected;
    private static boolean upExists=false;// is true if direction up exists in relation too root node
    private static boolean downExists=false;// is true if direction down exists in relation too root node
    private static boolean leftExists=false;// is true if direction left exists in relation too root node
    private static boolean rightExists=false;// is true if direction right exists in relation too root node
    private static boolean lookingForFlame=false;
    private static boolean jarvisFail=false;
    private static State flameState;
    private static boolean uniformCostAdder=false;
    private static int depth=0;
    private static int testCounter=0;
    private static boolean firstComponentFound=false;

    private static State initialState=new State(0,0);
    private static Node currentNode; // current node handled from the queue
    private static int noOfExpandedNodes=1;
    private static State lastComponent;

    static ArrayList<Node> sortedNodes = new ArrayList<Node>();

    static Operator operators;
    static GoalTest goalTest;
    static ArrayList<State> stateSpace=new ArrayList<State>();


    public GenericSearchProblem(Operator operators, State initialState, GoalTest goalTest,ArrayList<State> stateSpace){


        GenericSearchProblem.operators=operators;
        GenericSearchProblem.initialState=initialState;
        GenericSearchProblem.goalTest=goalTest;
        GenericSearchProblem.stateSpace=stateSpace;
    }
    public static void rootNodeDirectionChecker(Node node){ // checks the direction restrictions on the node to avoid repetitions
        Operator op;
        while (node.operator!=Operator.root){
            op=node.operator;
            switch (op){
                case up:
                    upExists=true;break;
                case down:
                    downExists=true;break;
                case left:
                    leftExists=true;break;
                default:
                    rightExists=true;break;
            }
            node=node.parentNode;
        }
    }
    public static void falsifyNodeRestrictions(){
        upExists=false;
        downExists=false;
        leftExists=false;
        rightExists=false;
    }
    public static void componentNodeDirectionChecker(Node node){ // checks the direction restrictions on the node to avoid repetitions
        Operator op;
        while (node.operator!=Operator.pick){
            op=node.operator;
            switch (op){
                case up:
                    upExists=true;break;
                case down:
                    downExists=true;break;
                case left:
                    leftExists=true;break;
                default:
                    rightExists=true;break;
            }
            node=node.parentNode;
        }
    }


    public static void getLocations(String generatedGrid){
        //getting locations
        int count = 0;
        int b = 0;
        for (int i = 0; i < generatedGrid.length(); i++) { //looping on the grid string

            char a = generatedGrid.charAt(i);
            if (a == ';') {
                if (count == 0) {  //dividing into substrings accordingly
                    gridSize = generatedGrid.substring(b, i); //grid size substring
                    b = i + 1;
                    count++;
                }
                else if (count == 1) {
                    jarvisLocation= generatedGrid.substring(b,i); //jarvis location Substring
                    b = i + 1;
                    count++;

                }
                else if (count == 2) {
                    flameLocation= generatedGrid.substring(b,i); //flame location substring
                    b = i + 1;
                    count++;

                }
                else if (count == 3) {
                    componentsCellLocations= generatedGrid.substring(b,i); //components location substring

                    poisonCellLocations= generatedGrid.substring(i+1); // poison locations substring
                    break;

                }
            }
        }

        gridX = Integer.parseInt(gridSize.substring(0, gridSize.indexOf(","))); //resolving gridSize substring to x
        gridY = Integer.parseInt(gridSize.substring(gridSize.indexOf(",") + 1)); //resolving gridSize substring to y


        startX = Integer.parseInt(jarvisLocation.substring(0, jarvisLocation.indexOf(","))); //resolving jarvis loc substring to x
        startY = Integer.parseInt(jarvisLocation.substring(jarvisLocation.indexOf(",") + 1)); //resolving jarvis loc substring to y


        initialState.x=startX; // creating the initial state
        initialState.y=startY;

        currentNode= new Node(initialState,Operator.root,0,0);
        currentNode.setState(initialState);// setting state of current node


        flameX = Integer.parseInt(flameLocation.substring(0, flameLocation.indexOf(",")));//resolving FlameLocation substring to x
        flameY = Integer.parseInt(flameLocation.substring(flameLocation.indexOf(",") + 1));//resolving FlameLocation substring to y

        flameState=new State(flameX,flameY); //creating a state for the flame

    }

    //checks if there is a restriction on the cell, Example: Corner Cell
    public static String restrictionChecker(){
        if(currentNode.state.x==0 && currentNode.state.y==0){
            return "TOP left CORNER";
        }
        else if(currentNode.state.x==gridX-1 && currentNode.state.y==0){
            return "BOTTOM left CORNER";
        }
        else if(currentNode.state.x==gridX-1&& currentNode.state.y==gridY-1){

            return "BOTTOM right CORNER";
        }
        else if(currentNode.state.x==0 && currentNode.state.y==gridY-1){
            return "TOP right CORNER";
        }
        else if(currentNode.state.x==0 && (currentNode.state.y!=0 && currentNode.state.y!=gridY-1)){
            return  "TOP SIDE";
        }
        else  if(currentNode.state.x==gridX-1 &&(currentNode.state.y!=0 && currentNode.state.y!=gridY-1) ){
            return "BOTTOM SIDE";
        }
        else if((currentNode.state.x!=0 && currentNode.state.x!=gridX-1) && currentNode.state.y==gridY-1){
            return "right SIDE";
        }
        else if((currentNode.state.x!=0 && currentNode.state.x!=gridX-1 ) && currentNode.state.y==0){
            return "left SIDE";
        }
        else {
            return "MIDDLE";
        }

    }

    public static Node childNodeCreator(Operator direction){ // children Node creator for breadth,depth and uniform
        if(direction.equals(Operator.up)){
            State childState= new State(currentNode.state.x-1, currentNode.state.y);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+4); // up cost is 4
        }
        else if (direction.equals(Operator.down)){
            State childState= new State(currentNode.state.x+1, currentNode.state.y);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+2); // down cost is 2
        }
        else if (direction.equals(Operator.left)){
            State childState= new State(currentNode.state.x, currentNode.state.y-1);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+3);// left cost is 3
        }
        else  {
            State childState= new State(currentNode.state.x, currentNode.state.y+1);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+1);// right cost is 1
        }
    }
    public static Node greedy1ChildNodeCreator(Operator direction){ // children creator for Gready search
        if(direction.equals(Operator.up)){
            State childState= new State(currentNode.state.x-1, currentNode.state.y);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.depth+1); // cost is depth +1
        }
        else if (direction.equals(Operator.down)){
            State childState= new State(currentNode.state.x+1, currentNode.state.y);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.depth+1);
        }
        else if (direction.equals(Operator.left)){
            State childState= new State(currentNode.state.x, currentNode.state.y-1);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.depth+1);
        }
        else  {
            State childState= new State(currentNode.state.x, currentNode.state.y+1);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.depth+1);
        }
    }
    public static Node greedy2ChildNodeCreator(Node node,Operator direction){ // children creator for Gready2 (chess board)
        if(direction.equals(Operator.up)){
            State childState= new State(node.state.x-1, node.state.y);
            return new Node(childState,node,direction,currentNode.depth,node.depth);  // same depth as cost
        }
        else if (direction.equals(Operator.down)){
            State childState= new State(node.state.x+1, node.state.y);
            return new Node(childState,node,direction,currentNode.depth,node.depth); // same depth as cost
        }
        else if (direction.equals(Operator.left)){
            State childState= new State(node.state.x, node.state.y-1);
            return new Node(childState,node,direction,currentNode.depth,node.depth); // same depth as cost
        }
        else  {
            State childState= new State(node.state.x, node.state.y+1);
            return new Node(childState,node,direction,currentNode.depth,node.depth); // same depth as cost
        }
    }
    public static Node as1childNodeCreator(Operator direction){ // children Node creator for as1
        if(direction.equals(Operator.up)){
            State childState= new State(currentNode.state.x-1, currentNode.state.y);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+4 +currentNode.depth+1); // adding direction cost + depth+1
        }
        else if (direction.equals(Operator.down)){
            State childState= new State(currentNode.state.x+1, currentNode.state.y);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+2+currentNode.depth+1); // // adding direction cost + depth+1
        }
        else if (direction.equals(Operator.left)){
            State childState= new State(currentNode.state.x, currentNode.state.y-1);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+3+currentNode.depth+1);//// adding direction cost + depth+1
        }
        else  {
            State childState= new State(currentNode.state.x, currentNode.state.y+1);
            return new Node(childState,currentNode,direction,currentNode.depth+1,currentNode.pathCost+1+currentNode.depth+1);// // adding direction cost + depth+1
        }
    }
    public static Node as2ChildNodeCreator(Node node,Operator direction){ // children creator for as2 (chess board)
        if(direction.equals(Operator.up)){
            State childState= new State(node.state.x-1, node.state.y);
            return new Node(childState,node,direction,currentNode.depth,node.pathCost+4);  // same depth as cost
        }
        else if (direction.equals(Operator.down)){
            State childState= new State(node.state.x+1, node.state.y);
            return new Node(childState,node,direction,currentNode.depth,node.pathCost+2); // same depth as cost
        }
        else if (direction.equals(Operator.left)){
            State childState= new State(node.state.x, node.state.y-1);
            return new Node(childState,node,direction,currentNode.depth,node.pathCost+3); // same depth as cost
        }
        else  {
            State childState= new State(node.state.x, node.state.y+1);
            return new Node(childState,node,direction,currentNode.depth,node.pathCost+4); // same depth as cost
        }
    }

    public static void sortNodes() {

        Collections.sort(sortedNodes, new NodeCostComparator());

    }

    public static void depthNodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        if (firstComponentFound){
            componentNodeDirectionChecker(currentNode);
        }
        else {
            rootNodeDirectionChecker(currentNode);
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node

            case "TOP left CORNER": {

                if (upExists && leftExists) {
                    return;
                } else if (upExists) {
                    queue.add(childNodeCreator(Operator.right));
                } else if (leftExists) {
                    queue.add(childNodeCreator(Operator.down));
                } else {
                    queue.add(childNodeCreator(Operator.down));
                    queue.add(childNodeCreator(Operator.right));
                }

            }
            break;
            case "BOTTOM left CORNER": {

                if (downExists && leftExists) {
                    return;
                } else if (downExists) {
                    queue.add(childNodeCreator(Operator.right));
                } else if (leftExists) {
                    queue.add(childNodeCreator(Operator.up));
                } else {
                    queue.add(childNodeCreator(Operator.up));
                    queue.add(childNodeCreator(Operator.right));
                }

            }
            break;
            case "BOTTOM right CORNER": {

                if (downExists && rightExists) {
                    return;
                } else if (downExists) {
                    queue.add(childNodeCreator(Operator.left));
                } else if (rightExists) {
                    queue.add(childNodeCreator(Operator.up));
                } else {
                    queue.add(childNodeCreator(Operator.up));
                    queue.add(childNodeCreator(Operator.left));
                }

            }
            break;
            case "TOP right CORNER": {

                if (upExists && rightExists) {
                    return;
                } else if (upExists) {
                    queue.add(childNodeCreator(Operator.left));
                } else if (rightExists) {
                    queue.add(childNodeCreator(Operator.down));
                } else {
                    queue.add(childNodeCreator(Operator.down));
                    queue.add(childNodeCreator(Operator.left));
                }

            }break;
            case "TOP SIDE": {
                if (upExists) {
                    if (leftExists) {
                        queue.add(childNodeCreator(Operator.left));
                    } else if (rightExists) {
                        queue.add(childNodeCreator(Operator.right));
                    } else {
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }
                } else {
                    if (leftExists) {
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                    } else if (rightExists) {
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.right));
                    } else {
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }
                }

            }break;
            case "BOTTOM SIDE": {

                if (downExists) {
                    if (leftExists) {
                        queue.add(childNodeCreator(Operator.left));
                    } else if (rightExists) {
                        queue.add(childNodeCreator(Operator.right));
                    } else {
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }
                } else {
                    if (leftExists) {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.left));
                    } else if (rightExists) {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.right));
                    } else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }
                }


            }
            break;
            case "right SIDE": {

                if (rightExists) {
                    if (upExists) {
                        queue.add(childNodeCreator(Operator.up));
                    } else if (downExists) {
                        queue.add(childNodeCreator(Operator.down));
                    } else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                    }
                } else {
                    if (upExists) {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.left));
                    } else if (downExists) {
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                    } else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                    }
                }

            }
            break;
            case "left SIDE": {
                if (leftExists) {
                    if (upExists) {
                        queue.add(childNodeCreator(Operator.up));
                    } else if (downExists) {
                        queue.add(childNodeCreator(Operator.down));
                    } else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                    }
                } else {
                    if (upExists) {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.right));
                    } else if (downExists) {
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.right));
                    } else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.right));
                    }
                }

            }
            break;
            default: {

                if (upExists) {
                    if (leftExists){
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.left));
                    }
                    else if (rightExists){
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.right));
                    }
                    else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }
                }
                else if (downExists){

                    if (leftExists){
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                    }
                    else if (rightExists){
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.right));
                    }
                    else {
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }
                }
                else {
                    if (leftExists){
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                    }
                    else if (rightExists){
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.right));
                    }
                    else {
                        queue.add(childNodeCreator(Operator.up));
                        queue.add(childNodeCreator(Operator.down));
                        queue.add(childNodeCreator(Operator.left));
                        queue.add(childNodeCreator(Operator.right));
                    }

                }

            }break;
        }

    }

    public static void breadthNodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node
            case "TOP left CORNER":
                if (currentNode.operator!=Operator.up){
                    queue.add(childNodeCreator(Operator.down));

                }
                if (currentNode.operator!=Operator.left) {
                    queue.add(childNodeCreator(Operator.right)); // Adding node children to queue
                }
                break;

            case "BOTTOM left CORNER":
                if (currentNode.operator!=Operator.down){
                    queue.add(childNodeCreator(Operator.up));
                }
                if (currentNode.operator!=Operator.left){
                    queue.add(childNodeCreator(Operator.right));// Adding node children to queue
                }

                break;
            case "BOTTOM right CORNER":
                if (currentNode.operator!=Operator.down){
                    queue.add(childNodeCreator(Operator.up));
                }
                if (currentNode.operator!=Operator.right){
                    queue.add(childNodeCreator(Operator.left));
                }

                break;
            case "TOP right CORNER":
                if (currentNode.operator!=Operator.up){
                    queue.add(childNodeCreator(Operator.down));
                }
                if (currentNode.operator!=Operator.right){
                    queue.add(childNodeCreator(Operator.left));
                }
                break;
            case "TOP SIDE":
                if (currentNode.operator!=Operator.up){
                    queue.add(childNodeCreator(Operator.down));
                }
                if (currentNode.operator!=Operator.left){
                    queue.add(childNodeCreator(Operator.right));
                }
                if (currentNode.operator!=Operator.right){
                    queue.add(childNodeCreator(Operator.left));
                }

                break;
            case "BOTTOM SIDE":
                if (currentNode.operator!=Operator.down){
                    queue.add(childNodeCreator(Operator.up));
                }
                if (currentNode.operator!=Operator.left){
                    queue.add(childNodeCreator(Operator.right));
                }
                if (currentNode.operator!=Operator.right){
                    queue.add(childNodeCreator(Operator.left));
                }
                break;
            case "left SIDE":
                if (currentNode.operator!=Operator.down){
                    queue.add(childNodeCreator(Operator.up));
                }
                if (currentNode.operator!=Operator.up){
                    queue.add(childNodeCreator(Operator.down));
                }
                if (currentNode.operator!=Operator.left){
                    queue.add(childNodeCreator(Operator.right));
                }

                break;
            case "right SIDE":
                if (currentNode.operator!=Operator.down){
                    queue.add(childNodeCreator(Operator.up));
                }
                if (currentNode.operator!=Operator.up){
                    queue.add(childNodeCreator(Operator.down));
                }
                if (currentNode.operator!=Operator.right){
                    queue.add(childNodeCreator(Operator.left));
                }
                break;
            default:
                if (currentNode.operator!=Operator.down){
                    queue.add(childNodeCreator(Operator.up));
                }
                if (currentNode.operator!=Operator.up){
                    queue.add(childNodeCreator(Operator.down));
                }
                if (currentNode.operator!=Operator.right){
                    queue.add(childNodeCreator(Operator.left));
                }
                if (currentNode.operator!=Operator.left){
                    queue.add(childNodeCreator(Operator.right));
                }
                break;
        }
    }
    public static void uniformNodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node
            case "TOP left CORNER":
                sortedNodes.add(childNodeCreator(Operator.right));
                sortedNodes.add(childNodeCreator(Operator.down));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case right: if(currentNode.operator!=(Operator.left)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        default:
                            break;
                    }

                }
                sortedNodes.clear();
                break;

            case "BOTTOM left CORNER":
                sortedNodes.add(childNodeCreator(Operator.up));
                sortedNodes.add(childNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            case "BOTTOM right CORNER":
                sortedNodes.add(childNodeCreator(Operator.up));
                sortedNodes.add(childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP right CORNER":
                sortedNodes.add(childNodeCreator(Operator.down));
                sortedNodes.add(childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP SIDE":
                sortedNodes.add(childNodeCreator(Operator.down));
                sortedNodes.add(childNodeCreator(Operator.right));
                sortedNodes.add(childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "BOTTOM SIDE":
                sortedNodes.add(childNodeCreator(Operator.up));
                sortedNodes.add(childNodeCreator(Operator.right));
                sortedNodes.add(childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "left SIDE":
                sortedNodes.add(childNodeCreator(Operator.up));
                sortedNodes.add(childNodeCreator(Operator.down));
                sortedNodes.add(childNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "right SIDE":
                sortedNodes.add(childNodeCreator(Operator.up));
                sortedNodes.add(childNodeCreator(Operator.down));
                sortedNodes.add(childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            default:
                sortedNodes.add(childNodeCreator(Operator.up));
                sortedNodes.add(childNodeCreator(Operator.down));
                sortedNodes.add(childNodeCreator(Operator.left));
                sortedNodes.add(childNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
        }
    }

    public static void greedy1NodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node
            case "TOP left CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case right: if(currentNode.operator!=(Operator.left)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        default:
                            break;
                    }

                }
                sortedNodes.clear();
                break;

            case "BOTTOM left CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            case "BOTTOM right CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP right CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "BOTTOM SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "left SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "right SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            default:
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
        }
    }
    public static void as1NodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node
            case "TOP left CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case right: if(currentNode.operator!=(Operator.left)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        default:
                            break;
                    }

                }
                sortedNodes.clear();
                break;

            case "BOTTOM left CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            case "BOTTOM right CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP right CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "BOTTOM SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "left SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "right SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            default:
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
        }
    }

    public static void as2NodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node
            case "TOP left CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.down),Operator.right)); //down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case right: if(currentNode.operator!=(Operator.left)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        default:
                            break;
                    }

                }
                sortedNodes.clear();
                break;

            case "BOTTOM left CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.up),Operator.right)); // up right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            case "BOTTOM right CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.up),Operator.left)); // up left
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP right CORNER":
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.down),Operator.right)); //down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.down),Operator.left)); // down left
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.down),Operator.right)); // down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "BOTTOM SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.up),Operator.left)); // up left
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.up),Operator.right)); // up right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "left SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.right),Operator.up)); // right up
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.right),Operator.down)); // right down
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "right SIDE":
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.left),Operator.up)); // left up
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.left),Operator.down)); // left down
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            default:
                sortedNodes.add(as1childNodeCreator(Operator.up));
                sortedNodes.add(as1childNodeCreator(Operator.down));
                sortedNodes.add(as1childNodeCreator(Operator.left));
                sortedNodes.add(as1childNodeCreator(Operator.right));
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.up),Operator.left)); // up left
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.up),Operator.right)); // up right
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.down),Operator.left)); // down left
                sortedNodes.add(as2ChildNodeCreator(as1childNodeCreator(Operator.down),Operator.right)); // down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
        }
    }

    public static void greedy2NodeChildrenAddToQueue(){ //adds the children of current queue based on restrictions
        GoalTest checkIfPoison = new GoalTest(currentNode.state,poisonCellLocations); //creating goal test to check if poison cell;
        if (checkIfPoison.memberChecker()){ // case that this is a poison cell
            return;
        }
        switch (restrictionChecker()){ // restriction checker checks restrictions on directions possible for a node
            case "TOP left CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.down),Operator.right)); //down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case right: if(currentNode.operator!=(Operator.left)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        default:
                            break;
                    }

                }
                sortedNodes.clear();
                break;

            case "BOTTOM left CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.up),Operator.right)); // up right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            case "BOTTOM right CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.up),Operator.left)); // up left
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP right CORNER":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.down),Operator.right)); //down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "TOP SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.down),Operator.left)); // down left
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.down),Operator.right)); // down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case down: if(currentNode.operator!=(Operator.up)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "BOTTOM SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.up),Operator.left)); // up left
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.up),Operator.right)); // up right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "left SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.right),Operator.up)); // right up
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.right),Operator.down)); // right down
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
            case "right SIDE":
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.left),Operator.up)); // left up
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.left),Operator.down)); // left down
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }
                sortedNodes.clear();
                break;
            default:
                sortedNodes.add(greedy1ChildNodeCreator(Operator.up));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.down));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.left));
                sortedNodes.add(greedy1ChildNodeCreator(Operator.right));
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.up),Operator.left)); // up left
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.up),Operator.right)); // up right
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.down),Operator.left)); // down left
                sortedNodes.add(greedy2ChildNodeCreator(greedy1ChildNodeCreator(Operator.down),Operator.right)); // down right
                sortNodes();
                for (int i = 0; i < sortedNodes.size(); i++) {

                    Node n = sortedNodes.get(i);
                    Operator op = n.operator;
                    switch (op) {
                        case up: if(currentNode.operator!=(Operator.down)) {
                            queue.add(n);break;
                        }
                        case down: if(currentNode.operator!=Operator.up)
                            queue.add(n);break;
                        case left: if(currentNode.operator!=Operator.right)
                            queue.add(n);break;
                        case right: if(currentNode.operator!=Operator.left)
                            queue.add(n);break;
                        default:
                            break;
                    }
                }

                sortedNodes.clear();
                break;
        }
    }
    public static String nodeToPlanRoot(Node node){ // translates node path to plan
        Operator op;
        String la="";
        while (node.operator!=Operator.root ){ //Loops as long as node is not root or component node
            op=node.operator;
            switch (op){
                case up:
                    la=Operator.up+","+la;break;
                case down:
                    la=Operator.down+","+la;break;
                case left:
                    la=Operator.left+","+la;break;
                case right:
                    la=Operator.right+","+la;break;
                case pick:
                    la=Operator.pick+","+la;break;
                default:
                    la=Operator.light+la;break;
            }
            node=node.parentNode;
        }
        return la;
    }
    public static String nodeToPlanComponent(Node node){ // translates node path to plan
        Operator op;
        String la="";
        if (node.operator==Operator.pick){ // case that a component cell has entered the method
            node=node.parentNode;
        }
        while ( node.operator!=Operator.pick ){ //Loops as long as node is not root or component node
            op=node.operator;
            switch (op){
                case up:
                    la=Operator.up+","+la;break;
                case down:
                    la=Operator.down+","+la;break;
                case left:
                    la=Operator.left+","+la;break;
                case right:
                    la=Operator.right+","+la;break;
                case pick:
                    la=Operator.pick+","+la;break;
                default:
                    la=Operator.light+la;break;
            }
            node=node.parentNode;
            if (node.operator==Operator.pick){
                la=Operator.pick+","+la;break;

            }
        }
        return la;
    }


    public static int commaCount(String list,int i){ //method that counts commas
        int commas=0;
        char x;
        for(int j=i;j<list.length();j++){
            x=list.charAt(j);
            if(x==','){
                commas+=1;
            }
        }
        return commas;
    }
    public static void memberRemover(){ //removes component location from the component's cell location list

        String pair= currentNode.state.x + ","+ currentNode.state.y;
        String temp;
        char a;
        int counter =0;
        int b=0;
        for(int i=0;i<componentsCellLocations.length();i++){
            if(commaCount(componentsCellLocations,i)==1){// if only one pair is left
                if(i==0){
                    temp=componentsCellLocations;
                    if(temp.equals(pair)){// checks if the pair is equal to the node we want to delete
                        componentsCellLocations=""; //takes the list except the last elements
                    }
                }
                else{
                    temp=componentsCellLocations.substring(i);
                    if(temp.equals(pair)){// checks if the pair is equal to the node we want to delete
                        componentsCellLocations=componentsCellLocations.substring(0,i-1); //takes the list except the last elements
                    }
                }


            }
            else{
                a=componentsCellLocations.charAt(i); // case that more than one pair is left in the list
                if(a==','){
                    ++counter;
                    if(counter==2){ // pair is formed
                        temp=componentsCellLocations.substring(b,i); // gets pair
                        if (temp.equals(pair)){ // if pair matches we delete the pair
                            String x1=componentsCellLocations.substring(0,b); //takes first part of list
                            String x2=componentsCellLocations.substring(i+1); //takes second part of list
                            componentsCellLocations=x1+x2; //forms the list without the deleted pair

                        }
                        else { // case that the pair does not equal node we want to delete
                            b=i+1;
                            counter=0;
                        }
                    }

                }
            }

        }

    }


    public static String genericSearch (Problem problem1,Strategy strat) {

        generatedGrid=problem1.generatedGrid; // retrieving the grid string;

        if (strat.equals(Strategy.BF)) { //BREADTH FIRST SEARCH

                while (!flameLit){ // while the flame is not lit
                    if (firstRun){ // case this is the first time in this method
                        getLocations(generatedGrid); // assigns locations and coordinates to variables
                        firstRun=false;
                    }
                    else{
                        if (queue.isEmpty()){ // case the queue is empty
                            GoalTest isLastNodeLeftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                            if (isLastNodeLeftPoison.memberChecker()){ //checking if current cell is poison cell
                                plan="THERE IS NO SOLUTION";
                                break;
                            }
                        }
                        else {
                            currentNode=queue.get(0); // gets first element
                            ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                            queue.remove(0); // removes element from queue
                        }
                    }
                    if(componentsCollected){ // case that all components are collected
                        GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                        if (isFlame.isFlame()){ // checks if current node is flame cell
                            Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                           plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                           flameLit=true;
                        }
                        else { //case that components are collected but this is not the flame cell
                             breadthNodeChildrenAddToQueue(); //adds children of current Node to queue
                        }
                    }
                    else { // case that not all components are collected

                        GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                        if (isComponent.memberChecker()){// checks if current node is a component cell

                            memberRemover(); // removes component coordinates from components cell locations

                            if (firstComponentFound){ // case this is the first component we found
                                plan=plan+ nodeToPlanComponent(currentNode);
                            }
                            else {
                                plan=plan+ nodeToPlanRoot(currentNode);
                            }
                            firstComponentFound=true; // indicating we found the first component
                            currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                            queue.clear(); // emptying the queue as now we will focus on this branch
                            if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                                componentsCollected=true; // flag that all components are collected and to start looking for flame
                            }
                        }
                        breadthNodeChildrenAddToQueue(); //adds children of the node to the queue

                    }
                }
        }
        else if (strat.equals(Strategy.DF)) {

            while (!flameLit){ // while the flame is not lit
                if (firstRun){ // case this is the first time in this method
                    getLocations(generatedGrid); // assigns locations and coordinates to variables
                    firstRun=false;
                }
                else{
                    if (queue.isEmpty()){ // case the queue is empty
                        GoalTest isLastNodeLeftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                        if (isLastNodeLeftPoison.memberChecker()){ //checking if current cell is poison cell
                            plan="THERE IS NO SOLUTION";
                            break;
                        }
                    }
                    else {
                        currentNode=queue.get(queue.size()-1); // gets last element
                        ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                        queue.remove(queue.size()-1); // removes last element from queue
                    }
                }
                if(componentsCollected){ // case that all components are collected
                    GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                    if (isFlame.isFlame()){ // checks if current node is flame cell
                        Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                        plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                        flameLit=true;
                    }
                    else { //case that components are collected but this is not the flame cell
                        depthNodeChildrenAddToQueue(); //adds children of current Node to queue
                        falsifyNodeRestrictions();
                    }
                }
                else { // case that not all components are collected

                    GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                    if (isComponent.memberChecker()){// checks if current node is a component cell

                        memberRemover(); // removes component coordinates from components cell locations

                        if (firstComponentFound){ // case this is the first component we found
                            plan=plan+ nodeToPlanComponent(currentNode);
                        }
                        else {
                            plan=plan+ nodeToPlanRoot(currentNode);
                        }
                        firstComponentFound=true; // indicating we found the first component
                        currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                        queue.clear(); // emptying the queue as now we will focus on this branch
                        if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                            componentsCollected=true; // flag that all components are collected and to start looking for flame
                        }
                    }
                    depthNodeChildrenAddToQueue(); //adds children of the node to the queue
                    falsifyNodeRestrictions();


                }
            }
        }
        else if (strat.equals(Strategy.UC)) {
            while (!flameLit){ // while the flame is not lit
                if (firstRun){ // case this is the first time in this method
                    getLocations(generatedGrid); // assigns locations and coordinates to variables
                    firstRun=false;
                }
                else{
                    if (queue.isEmpty()){ // case the queue is empty
                        GoalTest isLastNodeleftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                        if (isLastNodeleftPoison.memberChecker()){ //checking if current cell is poison cell
                            plan="THERE IS NO SOLUTION";
                            break;
                        }
                    }
                    else {
                        currentNode=queue.get(0); // gets first element
                        ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                        queue.remove(0); // removes element from queue
                    }
                }
                if(componentsCollected){ // case that all components are collected
                    GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                    if (isFlame.isFlame()){ // checks if current node is flame cell
                        Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                        plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                        flameLit=true;
                    }
                    else { //case that components are collected but this is not the flame cell
                       uniformNodeChildrenAddToQueue(); //adds children of current Node to queue
                    }
                }
                else { // case that not all components are collected

                    GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                    if (isComponent.memberChecker()){// checks if current node is a component cell

                        memberRemover(); // removes component coordinates from components cell locations

                        if (firstComponentFound){ // case this is the first component we found
                            plan=plan+ nodeToPlanComponent(currentNode);
                        }
                        else {
                            plan=plan+ nodeToPlanRoot(currentNode);
                        }
                        firstComponentFound=true; // indicating we found the first component
                        currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                        queue.clear(); // emptying the queue as now we will focus on this branch
                        if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                            lastComponent= new State(currentNode.state.x,currentNode.state.y);
                            componentsCollected=true; // flag that all components are collected and to start looking for flame
                        }
                    }
                    uniformNodeChildrenAddToQueue(); //adds children of the node to the queue

                }
            }

            // CALL UCS method
        }
        else if (strat.equals(Strategy.GR1)) {
            while (!flameLit){ // while the flame is not lit
                if (firstRun){ // case this is the first time in this method
                    getLocations(generatedGrid); // assigns locations and coordinates to variables
                    firstRun=false;
                }
                else{
                    if (queue.isEmpty()){ // case the queue is empty
                        GoalTest isLastNodeleftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                        if (isLastNodeleftPoison.memberChecker()){ //checking if current cell is poison cell
                            plan="THERE IS NO SOLUTION";
                            break;
                        }
                    }
                    else {
                        currentNode=queue.get(0); // gets first element
                        ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                        queue.remove(0); // removes element from queue
                    }
                }
                if(componentsCollected){ // case that all components are collected
                    GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                    if (isFlame.isFlame()){ // checks if current node is flame cell
                        Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                        plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                        flameLit=true;
                    }
                    else { //case that components are collected but this is not the flame cell
                        greedy1NodeChildrenAddToQueue(); //adds children of current Node to queue
                    }
                }
                else { // case that not all components are collected

                    GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                    if (isComponent.memberChecker()){// checks if current node is a component cell

                        memberRemover(); // removes component coordinates from components cell locations

                        if (firstComponentFound){ // case this is the first component we found
                            plan=plan+ nodeToPlanComponent(currentNode);
                        }
                        else {
                            plan=plan+ nodeToPlanRoot(currentNode);
                        }
                        firstComponentFound=true; // indicating we found the first component
                        currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                        queue.clear(); // emptying the queue as now we will focus on this branch
                        if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                            lastComponent= new State(currentNode.state.x,currentNode.state.y);
                            componentsCollected=true; // flag that all components are collected and to start looking for flame
                        }
                    }
                    greedy1NodeChildrenAddToQueue(); //adds children of the node to the queue

                }
            }


            // CALL Greedy with 1st H. method
        }
        else if (strat.equals(Strategy.GR2)) {
            while (!flameLit){ // while the flame is not lit
                if (firstRun){ // case this is the first time in this method
                    getLocations(generatedGrid); // assigns locations and coordinates to variables
                    firstRun=false;
                }
                else{
                    if (queue.isEmpty()){ // case the queue is empty
                        GoalTest isLastNodeleftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                        if (isLastNodeleftPoison.memberChecker()){ //checking if current cell is poison cell
                            plan="THERE IS NO SOLUTION";
                            break;
                        }
                    }
                    else {
                        currentNode=queue.get(0); // gets first element
                        ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                        queue.remove(0); // removes element from queue
                    }
                }
                if(componentsCollected){ // case that all components are collected
                    GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                    if (isFlame.isFlame()){ // checks if current node is flame cell
                        Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                        plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                        flameLit=true;
                    }
                    else { //case that components are collected but this is not the flame cell
                        greedy2NodeChildrenAddToQueue(); //adds children of current Node to queue
                    }
                }
                else { // case that not all components are collected

                    GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                    if (isComponent.memberChecker()){// checks if current node is a component cell

                        memberRemover(); // removes component coordinates from components cell locations

                        if (firstComponentFound){ // case this is the first component we found
                            plan=plan+ nodeToPlanComponent(currentNode);
                        }
                        else {
                            plan=plan+ nodeToPlanRoot(currentNode);
                        }
                        firstComponentFound=true; // indicating we found the first component
                        currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                        queue.clear(); // emptying the queue as now we will focus on this branch
                        if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                            lastComponent= new State(currentNode.state.x,currentNode.state.y);
                            componentsCollected=true; // flag that all components are collected and to start looking for flame
                        }
                    }
                    greedy2NodeChildrenAddToQueue(); //adds children of the node to the queue

                }
            }


            // CALL Greedy with 2nd H. method
        }
        else if (strat.equals(Strategy.AS1)) {
            while (!flameLit){ // while the flame is not lit
                if (firstRun){ // case this is the first time in this method
                    getLocations(generatedGrid); // assigns locations and coordinates to variables
                    firstRun=false;
                }
                else{
                    if (queue.isEmpty()){ // case the queue is empty
                        GoalTest isLastNodeleftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                        if (isLastNodeleftPoison.memberChecker()){ //checking if current cell is poison cell
                            plan="THERE IS NO SOLUTION";
                            break;
                        }
                    }
                    else {
                        currentNode=queue.get(0); // gets first element
                        ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                        queue.remove(0); // removes element from queue
                    }
                }
                if(componentsCollected){ // case that all components are collected
                    GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                    if (isFlame.isFlame()){ // checks if current node is flame cell
                        Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                        plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                        flameLit=true;
                    }
                    else { //case that components are collected but this is not the flame cell
                        as1NodeChildrenAddToQueue(); //adds children of current Node to queue
                    }
                }
                else { // case that not all components are collected

                    GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                    if (isComponent.memberChecker()){// checks if current node is a component cell

                        memberRemover(); // removes component coordinates from components cell locations

                        if (firstComponentFound){ // case this is the first component we found
                            plan=plan+ nodeToPlanComponent(currentNode);
                        }
                        else {
                            plan=plan+ nodeToPlanRoot(currentNode);
                        }
                        firstComponentFound=true; // indicating we found the first component
                        currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                        queue.clear(); // emptying the queue as now we will focus on this branch
                        if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                            lastComponent= new State(currentNode.state.x,currentNode.state.y);
                            componentsCollected=true; // flag that all components are collected and to start looking for flame
                        }
                    }
                    as1NodeChildrenAddToQueue(); //adds children of the node to the queue

                }
            }

            // CALL A* with 1st H. method
        }
        else if (strat.equals(Strategy.AS2)) {
            while (!flameLit){ // while the flame is not lit
                if (firstRun){ // case this is the first time in this method
                    getLocations(generatedGrid); // assigns locations and coordinates to variables
                    firstRun=false;
                }
                else{
                    if (queue.isEmpty()){ // case the queue is empty
                        GoalTest isLastNodeleftPoison =new GoalTest(currentNode.state,poisonCellLocations);
                        if (isLastNodeleftPoison.memberChecker()){ //checking if current cell is poison cell
                            plan="THERE IS NO SOLUTION";
                            break;
                        }
                    }
                    else {
                        currentNode=queue.get(0); // gets first element
                        ++noOfExpandedNodes; // incrementing the number of expanded Nodes
                        queue.remove(0); // removes element from queue
                    }
                }
                if(componentsCollected){ // case that all components are collected
                    GoalTest isFlame =new GoalTest(currentNode.state,flameState); //creating new goal test for the flame
                    if (isFlame.isFlame()){ // checks if current node is flame cell
                        Node goalNode =new Node(currentNode.state,currentNode,Operator.light,currentNode.depth,currentNode.pathCost);
                        plan=plan+ nodeToPlanComponent(goalNode); // adding node mapping to plan;
                        flameLit=true;
                    }
                    else { //case that components are collected but this is not the flame cell
                        as2NodeChildrenAddToQueue(); //adds children of current Node to queue
                    }
                }
                else { // case that not all components are collected

                    GoalTest isComponent =new GoalTest(currentNode.state,componentsCellLocations); //creating new goal test for the component
                    if (isComponent.memberChecker()){// checks if current node is a component cell

                        memberRemover(); // removes component coordinates from components cell locations

                        if (firstComponentFound){ // case this is the first component we found
                            plan=plan+ nodeToPlanComponent(currentNode);
                        }
                        else {
                            plan=plan+ nodeToPlanRoot(currentNode);
                        }
                        firstComponentFound=true; // indicating we found the first component
                        currentNode =new Node(currentNode.state,currentNode,Operator.pick,currentNode.depth,currentNode.pathCost);
                        queue.clear(); // emptying the queue as now we will focus on this branch
                        if (componentsCellLocations.isEmpty()){ //checks if all components are collected
                            lastComponent= new State(currentNode.state.x,currentNode.state.y);
                            componentsCollected=true; // flag that all components are collected and to start looking for flame
                        }
                    }
                    as2NodeChildrenAddToQueue(); //adds children of the node to the queue

                }
            }


            // CALL A* with 2nd H. method
        }
        firstRun=true;
        return plan+";"+noOfExpandedNodes;

    }
    public static String printQueue(){ // tester method (used for testing and debugging)
        String la="";
        String node="";
        Node temp;
        for(int i=0;i< queue.size();i++){
            temp=queue.get(i);
            if (temp==null){
                System.out.println("QUEUE IS EMPTY");
                break;
            }

            la+=  printNode(temp) + " ";
            node="";
        }
        return la;
    }
    public static String printUniformCostList(){ // tester method (used for testing and debugging)
        String la="";
        String node="";
        Node temp;
        for(int i=0;i< sortedNodes.size();i++){
            temp=sortedNodes.get(i);
            if (temp==null){
                System.out.println("QUEUE IS EMPTY");
                break;
            }

            la+=  printNode(temp) + " ";
            node="";
        }
        return la;
    }
    public static String printNode(Node node){ // tester method (used for testing and debugging)
        String completeNode="";
        while (node.operator!=Operator.root){
            Operator a=node.operator;
            switch (a){
                case up:
                    completeNode="U"+completeNode;
                    node=node.parentNode;break;
                case down:
                    completeNode="D"+completeNode;
                    node=node.parentNode;break;
                case left:
                    completeNode="L"+completeNode;
                    node=node.parentNode;break;
                case right:
                    completeNode="R"+completeNode;
                    node=node.parentNode;break;
                case pick:
                    completeNode="P"+completeNode;
                    node=node.parentNode;break;
                case light:
                    completeNode="F"+completeNode;
                    node=node.parentNode;break;
            }
        }
        return completeNode;

    }
//         if (testCounter==00){
//        System.out.println("INITIAL STATE IS: "+initialState.x+","+initialState.y);
//        System.out.println("CURRENT NODE IS: "+printNode(currentNode));
//        System.out.println("CURRENT NODE STATE IS: "+currentNode.state.x+","+currentNode.state.y);
//        System.out.println("QUEUE IS: "+printQueue());
//        System.out.println("PLAN IS: "+plan);
//        System.out.println("NODE COST IS: "+currentNode.pathCost);
//        return "STOP STOP STOP ";
//    }
//    testCounter++;


}
