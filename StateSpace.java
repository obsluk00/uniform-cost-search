import java.util.ArrayList;


public interface StateSpace {
    public State init();
    public boolean isGoal(State s);
    public ArrayList<ActionStatePair> succ(State state);
    public int cost(Action a);
}
