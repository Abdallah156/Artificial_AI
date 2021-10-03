package code;

public class State {

    int x;
    int y;


    public State(int x, int y){
        this.x=x;
        this.y=y;
    }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }

    public void setState(int x, int y){
        this.x=x;
        this.y=y;
    }



}

