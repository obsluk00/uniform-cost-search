import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;


public class StateSpaceTest {
    static final int RANDOM_WALK_LENGTH = 7;

    public static void main(String args[]) {
        StateSpace stateSpace = createStateSpace(args);

        System.out.println("Dumping initial state...");
        State init = stateSpace.init();
        dumpStateInfo(stateSpace, init);

        System.out.println("Dumping successor pairs of initial state...");
        ArrayList<ActionStatePair> successors = stateSpace.succ(init);
        dumpSuccessorInfo(stateSpace, successors);

        System.out.println("Pick first successor:");
        State s = successors.get(0).state;
        dumpStateInfo(stateSpace, s);

        System.out.println("Dumping successor pairs of this state...");
        successors = stateSpace.succ(s);
        dumpSuccessorInfo(stateSpace, successors);

        System.out.println("Perform a random walk from this state...");
        // Use a fixed random seed to simplify debugging.
        Random rand = new Random(4052169);
        for (int i = 0; i < RANDOM_WALK_LENGTH; i++) {
            successors = stateSpace.succ(s);
            if (successors.isEmpty()) {
                System.out.println("no successors: stopping random walk");
                break;
            }
            int succNo = rand.nextInt(successors.size());
            ActionStatePair succPair = successors.get(succNo);
            System.out.println("picking action: " + succPair.action);
            s = succPair.state;
            dumpStateInfo(stateSpace, s);
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

    private static void dumpSuccessorInfo(StateSpace stateSpace,
                                          ArrayList<ActionStatePair> succ) {
        for (int i = 0; i < succ.size(); i++) {
            ActionStatePair pair = succ.get(i);
            System.out.println("successor #" + i + ":");
            System.out.println("    action:    " + pair.action);
            System.out.println("    cost:      " + stateSpace.cost(pair.action));
            System.out.println("    new state: " + pair.state);
        }
    }
}
