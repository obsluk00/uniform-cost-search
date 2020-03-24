import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class UniformCostSearch extends SearchAlgorithmBase {

    private PriorityQueue<ElevatorSearchNode> openList;
    private HashSet<State> closedList;
    private ArrayList<Action> optimalPath;

    public UniformCostSearch(String[] args) {
        super(args);
    }

    @Override
    protected ArrayList<Action> run() {
        ArrayList<Action> optimalPath = new ArrayList<Action>();
        openList = new PriorityQueue<ElevatorSearchNode>(1, (a, b) -> a.pathCost - b.pathCost);
        openList.add(new ElevatorSearchNode(stateSpace.init()));
        closedList = new HashSet<State>();
        //for (ActionStatePair pair : stateSpace.succ(stateSpace.init())) {
        //    openList.add(new ElevatorSearchNode(stateSpace, new ElevatorSearchNode(), pair));
        //}
        while (!openList.isEmpty()) {
            ElevatorSearchNode n = openList.remove();
            if (!closedList.contains(n.state)) {
                closedList.add(n.state);
                if (this.stateSpace.isGoal(n.state)) {
                    return n.extractPath(n);
                }
                for (ActionStatePair successor : this.stateSpace.succ(n.state)) {
                    openList.add(new ElevatorSearchNode(n, successor, successor.action.cost()));
                }
            }
        }
        System.out.println("State-Space is unsolvable.");
        return new ArrayList<Action>();
    }


    public class ElevatorSearchNode {
        State state;
        ElevatorSearchNode parent;
        Action action;
        int pathCost;

        public ElevatorSearchNode(State init){
            this.state = init;
            this.parent = null;
            this.action = null;
            this.pathCost = 0;
        }

        public ElevatorSearchNode(ElevatorSearchNode parent, ActionStatePair pair, int actioncost){
            this.state = pair.state;
            this.parent = parent;
            this.action = pair.action;
            this.pathCost = parent.pathCost + actioncost;
        }

        public ArrayList<Action> extractPath(ElevatorSearchNode n) {
            ArrayList<Action> path = new ArrayList<Action>();
            while(n.parent != null){
                path.add(n.action);
                n = n.parent;
            }
            Collections.reverse(path);
            return path;
        }
    }
}
