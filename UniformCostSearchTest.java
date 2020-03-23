import java.util.Arrays;
import java.util.ArrayList;


public class UniformCostSearchTest {
    public static void main(String args[]){
        UniformCostSearch search = new UniformCostSearch(args);
        StateSpace stateSpace = search.stateSpace;
        State state = search.stateSpace.init();

        System.out.println("Calculating optimal solution...");
        ArrayList<Action> optimalPath = search.run();
        for (Action a: optimalPath) {
            for(ActionStatePair pair: stateSpace.succ(state)){
                if(pair.action.equals(a)){
                    state = pair.state;
                    System.out.println("Dumping optimal successor...");
                    dumpStateInfo(stateSpace, state);
                }
            }
        }
    }

    private static StateSpace createStateSpace(String args[]) {
        if (args.length == 0) {
            Errors.usageError("no state space given");
        }

        ArrayList<String> params = new ArrayList<String>(Arrays.asList(args));
        params.remove(0);

        if (args[0].equals("elevators")) {
            return ElevatorsStateSpace.buildFromCmdline(params);
        } else {
            Errors.usageError("unknown state space: " + args[0]);
        }
        return null;
    }

    private static void dumpStateInfo(StateSpace stateSpace, State s) {
        System.out.println(s);
        System.out.print("Is it a goal state? ");
        if (stateSpace.isGoal(s)) {
            System.out.println("yes");
        } else {
            System.out.println("no");
        }
        System.out.println();
    }
}
