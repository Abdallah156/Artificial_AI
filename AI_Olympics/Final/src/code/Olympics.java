package code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Olympics extends GenericSearchProblem {

    static int m,n; //Grid size
    static int jx,jy; //Jarvis initial Position
    static int fx,fy; //Flame position
    static int pN,cN; //Number of poison,component cells
    static int cx,cy; // For component locations
    static int px,py; // For poison locations
    static ArrayList<Integer> components = new ArrayList<Integer>(); //List representing the location of each component
    static ArrayList<Integer> poison = new ArrayList<Integer>(); //List representing the location of poisoned cells
    static String gridString="";
    static Strategy strategyChosen;
    static String finalPlan;
    static String[][] grid;

    public Olympics(Operator operators, State initialState, GoalTest goalTest, ArrayList stateSpace) {
        super(operators, initialState, goalTest, stateSpace);
    }


    //Generating random numbers for the grid
    public static int getRandomInt(int max, int min) {

        Random r = new Random();
        int rand = r.nextInt(max-min) + min;
        return rand;
    }
    public static void genGrid() {

        //Grid size
        m = Olympics.getRandomInt(16, 5);//Random Grid size between 15x15 and 5x5
        n=m;

        //Gird visualization
        grid = new String[m][n];
        Arrays.stream(grid).forEach(e -> Arrays.fill(e, "E"));


        //Initial Jarvis location
        jx = getRandomInt(m, 0);
        jy = getRandomInt(m, 0);
        grid[jx][jy] = "J";


        //Flame location
        int flag1 = 0;
        //Generating a unique location for the flame
        while(flag1 == 0) {
            fx = getRandomInt(m, 0);
            fy = getRandomInt(n, 0);
            if(grid[fx][fy].equals("E")) {
                grid[fx][fy] = "F";
                flag1 = 1;
            }

        }


        //Components
        cN = getRandomInt(11, 5); //random number of components between 5 and 10
        //Generating a unique location for each component
        for(int i = 0; i < cN; i++) {
            int flag2 = 0;
            while(flag2 == 0) {
                cx = getRandomInt(m, 0);
                cy = getRandomInt(n, 0);
                if(grid[cx][cy].equals("E")) {
                    grid[cx][cy] = "C";
                    components.add(cx);
                    components.add(cy);
                    flag2 = 1;
                }

            }
        }



        //Poisons
        pN = getRandomInt(26, 2); ////random number of components between 2 and 25
        //Generating a unique location for each poison
        for(int i = 0; i < pN; i++) {
            int flag3 = 0;
            while(flag3 == 0) {
                px = getRandomInt(m, 0);
                py = getRandomInt(n, 0);
                if(grid[px][py].equals("E")) {
                    grid[px][py] = "P";
                    poison.add(px);
                    poison.add(py);
                    flag3 = 1;
                }

            }
        }

//        Show the grid
        for (String[] row : grid) {
            System.out.println(Arrays.toString(row));
        }


        //Components String
        String comp = "";
        for(int i = 0; i < components.size(); i++) {
            if(i == components.size()-1)
                comp = comp  + components.get(i);
            else
                comp = comp  + components.get(i) + ',';
        }

        //Poison String
        String pois = "";
        for(int i = 0; i < poison.size(); i++) {
            if(i == poison.size()-1)
                pois = pois  + poison.get(i);
            else
                pois = pois  + poison.get(i) + ',';
        }

        //Final grid string
        gridString = m + "," + n + ";" +  jx + "," + jy + ";" + fx + "," + fy + ";" + comp + ";" + pois;

    }
    public static String solve(String grid, String strategy, boolean visualize) {
        Problem aq =new Problem(grid);


        strategyChosen = Strategy.valueOf(strategy);
        System.out.println(gridString);

        finalPlan= GenericSearchProblem.genericSearch(aq, strategyChosen);
        return finalPlan;

    }
    public static void main(String [] args){
       
    	genGrid();
    	String solution =solve(gridString,"AS2",false);
        System.out.println(solution);
    	
        /*long start = System.currentTimeMillis();
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        String solution =solve(gridString,"AS2",false);
        System.out.println(solution);
        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;
        long actualMemUsed=afterUsedMem-beforeUsedMem;
        System.out.println("RAM"+actualMemUsed);
        System.out.println("CPU"+elapsedTime);*/
    	
    	//genGrid();
        //String solution =solve("5,5;2,3;4,1;3,4,2,0,3,2,3,0,1,3;4,2,0,2,0,4,1,0,1,4,2,1,0,0","AS2",false);
        //System.out.println(solution);

//5,5;2,3;4,1;3,4,2,0,3,2,3,0,1,3;4,2,0,2,0,4,1,0,1,4,2,1,0,0
//11,11;5,7;9,0;0,7,7,2,8,4,8,5,6,10,5,9,1,3,7,1,3,10,7,5;10,2,6,6,3,6,2,3,6,7,9,9,1,9,3,2,9,4,7,7,2,4,1,4

    }



}
