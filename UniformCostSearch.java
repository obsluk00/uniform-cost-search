import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class UniformCostSearch extends SearchAlgorithmBase {

    private PriorityQueue<ActionStatePair> openList;
    private HashSet<State> closedList;
    private ArrayList<Action> optimalPath;

    public UniformCostSearch(String[] args) {
        super(args);
    }

    @Override
    protected ArrayList<Action> run() {
        openList = new PriorityQueue<ActionStatePair>(1, (a, b) -> stateSpace.cost(a.action) - stateSpace.cost(b.action));
        ArrayList<Action> optimalPath = new ArrayList<Action>();
        if(stateSpace.isGoal(stateSpace.init())){
            System.out.println("State-Space was initialized in a goal state");
            return optimalPath;
        }
        else {
            closedList = new HashSet<>();
            closedList.add(stateSpace.init());
            for (ActionStatePair pair: stateSpace.succ(stateSpace.init())) {
                openList.add(pair);
            }
            while(!openList.isEmpty()){
                ActionStatePair n = openList.remove();
                if(!closedList.contains(n.state)){
                    closedList.add(n.state);
                    optimalPath.add(n.action);
                    if(this.stateSpace.isGoal(n.state)){
                        return optimalPath;
                    }
                    for (ActionStatePair successor: this.stateSpace.succ(n.state)) {
                        openList.add(successor);
                    }
                }
            }
            System.out.println("State-Space is unsolvable.");
            return new ArrayList<Action>();
        }
    }
}
