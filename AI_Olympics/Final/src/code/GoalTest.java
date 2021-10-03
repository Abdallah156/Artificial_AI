package code;

public class GoalTest {


    State currentState;
    State goalState;
    String list;

    public GoalTest(State currentState,State goalState){
        this.currentState=currentState;
        this.goalState=goalState;
    }
    public GoalTest(State currentState,String list){
        this.currentState=currentState;
        this.list =list;
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



    public boolean memberChecker(){ //checks if node belongs to certain list (components or poison)

        String pair= currentState.x+","+currentState.y;
        String temp;
        char a;
        int counter =0;
        int b=0;
        for(int i=0;i<list.length();i++){
            if(commaCount(list,i)==1){// if only one pair is left
                temp=list.substring(i);
                return temp.equals(pair); // checks if the pair is equal to the node
            }
            else{
                a=list.charAt(i); // case that more than one pair is left in the list
                if(a==','){
                    ++counter;
                    if(counter==2){ // pair is formed
                        temp=list.substring(b,i); // gets pair
                        if (temp.equals(pair)){ // if pair matches node returns true
                            return true;
                        }
                        else { // case that the pair does not equal node
                            b=i+1;
                            counter=0;
                        }
                    }

                }
            }

        }
        return false;
    }
    public boolean isFlame(){
        if(currentState.x== goalState.x && currentState.y== goalState.y){
            return true;
        }
        else {
            return false;
        }

    }


}
