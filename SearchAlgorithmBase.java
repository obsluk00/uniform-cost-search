import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import java.util.ArrayList;
import java.util.Arrays;


public abstract class SearchAlgorithmBase {
    protected StateSpace stateSpace;
    protected long expandedStates;

    public SearchAlgorithmBase(String args[]) {
        stateSpace = createStateSpace(args);
    }

    protected abstract ArrayList<Action> run();

    protected void runSearchAlgorithm() {
        System.out.println("Starting search...");
        long timeStart = getCpuTime();
        ArrayList<Action> solution = run();
        long timeEnd = getCpuTime();
        System.out.println("" + (timeEnd - timeStart) / 1000000000.0
                           + " seconds search time");
        System.out.println("number of expanded states: " + expandedStates);

        if (solution == null) {
            System.out.println("no solution");
        } else {
            int totalCost = 0;
            System.out.println("Solution:");
            for (Action action : solution) {
                System.out.println(action);
                totalCost += stateSpace.cost(action);
            }
            System.out.println("Solution length: " +solution.size());
            System.out.println("Solution cost: " + totalCost);
        }
    }

    protected static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ?
            bean.getCurrentThreadCpuTime() : 0;
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
}
