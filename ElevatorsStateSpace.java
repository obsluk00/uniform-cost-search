import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class ElevatorsStateSpace implements StateSpace {
    /*
      We make Elevators states and actions private since the search code
      cannot and should not look into the state. Since they are only
      accessed from this class, we can make them plain old datatypes
      without violating encapsulation.
    */

    private static class PassengerLocation {
        boolean in_elevator;
        /*
          depending on in_elevator, location represents the following:
           - in_elevator: the index of the elevator in which the passenger is
           - !in_elevator: the floor on which the passenger is
        */
        int location;

        public PassengerLocation(boolean in_elevator, int location) {
            this.in_elevator = in_elevator;
            this.location = location;
        }

        public PassengerLocation(PassengerLocation o) {
            this.in_elevator = o.in_elevator;
            this.location = o.location;
        }

        public boolean equals(Object o) {
            if (!(o instanceof PassengerLocation)) {
                return false;
            }
            PassengerLocation other = (PassengerLocation)o;
            return (this.in_elevator == other.in_elevator) && (this.location == other.location);
        }
    }


    private static class ElevatorsState implements State {
        public ArrayList<PassengerLocation> passengers;
        public ArrayList<Integer> elevators;

        public ElevatorsState(ArrayList<PassengerLocation> passengers, ArrayList<Integer> elevators) {
            this.passengers = new ArrayList<PassengerLocation>(passengers);
            this.elevators = new ArrayList<Integer>(elevators);
        }

        public ElevatorsState(ElevatorsState o) {
            this.passengers = new ArrayList<PassengerLocation>();
            for (PassengerLocation passenger : o.passengers) {
                this.passengers.add(new PassengerLocation(passenger));
            }
            this.elevators = new ArrayList<Integer>();
            for (Integer elevator : o.elevators) {
                this.elevators.add(elevator);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ElevatorsState)) {
                return false;
            }
            ElevatorsState other = (ElevatorsState)o;
            return this.passengers.equals(other.passengers) && this.elevators.equals(other.elevators);
        }

        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }

        public String toString() {
            String result = "People locations: [";
            boolean first = true;
            for (PassengerLocation passenger : passengers) {
                if (!first) {
                    result += ", ";
                } else {
                    first = false;
                }

                result += "(";
                if (passenger.in_elevator) {
                    result += "elevator ";
                } else {
                    result += "floor ";
                }
                result += passenger.location + ")";
            }
            result += "], Elevator locations: [";
            first = true;
            for (Integer elevator : elevators) {
                if (!first) {
                    result += ", ";
                } else {
                    first = false;
                }
                result += elevator;
            }
            result += "]";
            return result;
        }
    }

    private static class ElevatorsMoveUpAction implements Action {
        public int elevator;
        public int origin_location;

        public ElevatorsMoveUpAction(int elevator, int origin_location) {
            this.elevator = elevator;
            this.origin_location = origin_location;
        }

        public String toString() {
            return "move elevator " + elevator + " up from floor " + origin_location +
                   " to floor " + (origin_location+1);
        }

        public int cost() {
            return this.elevator + 10;
        }
    }

    private static class ElevatorsMoveDownAction implements Action {
        public int elevator;
        public int origin_location;

        public ElevatorsMoveDownAction(int elevator, int origin_location) {
            this.elevator = elevator;
            this.origin_location = origin_location;
        }

        public String toString() {
            return "move elevator " + elevator + " down from floor " + origin_location +
                   " to floor " + (origin_location-1);
        }

        public int cost() {
            return this.elevator + 10;
        }
    }

    private static class ElevatorsEmbarkAction implements Action {
        public int elevator;
        public int passenger;

        public ElevatorsEmbarkAction(int elevator, int passenger) {
            this.elevator = elevator;
            this.passenger = passenger;
        }

        public String toString() {
            return "Passenger " + passenger + " enters elevator " + elevator;
        }

        public int cost() {
            return 1;
        }
    }

    private static class ElevatorsDisembarkAction implements Action {
        public int elevator;
        public int passenger;

        public ElevatorsDisembarkAction(int elevator, int passenger) {
            this.elevator = elevator;
            this.passenger = passenger;
        }

        public String toString() {
            return "Passenger " + passenger + " leaves elevator " + elevator;
        }

        public int cost() {
            return 1;
        }
    }

    private int numberOfPassengers;
    private int numberOfElevators;
    private int numberOfFloors;

    private ElevatorsState initState;
    private ArrayList<Integer> goal_passengers;

    private ElevatorsStateSpace(int numberOfPassengers, int numberOfElevators,
      int numberOfFloors, ElevatorsState initState,
      ArrayList<Integer> goal_passengers) {
        this.numberOfPassengers = numberOfPassengers;
        this.numberOfElevators = numberOfElevators;
        this.numberOfFloors = numberOfFloors;
        this.initState = initState;
        this.goal_passengers = goal_passengers;
        System.out.println("Instantiating problem instance with "
        + numberOfPassengers + " passengers, "
        + numberOfElevators + " elevators and "
        + numberOfFloors + " floors...");
    }

    public State init() {
        return initState;
    }

    public boolean isGoal(State s_) {
        ElevatorsState s = (ElevatorsState) s_;
        for (int i = 0; i < s.passengers.size(); i++) {
            if (s.passengers.get(i).in_elevator ||  s.passengers.get(i).location != goal_passengers.get(i)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<ActionStatePair> succ(State s_) {
        ElevatorsState s = (ElevatorsState) s_;

        ArrayList<ActionStatePair> result = new ArrayList<ActionStatePair>();

        // loop over all passengers to check for embark/disembark actions
        for (int i = 0; i < s.passengers.size(); i++) {
            PassengerLocation passenger = s.passengers.get(i);

            if (passenger.in_elevator) {
                // passenger is in an elevator -> it can disembark
                Action a = new ElevatorsDisembarkAction(passenger.location, i);
                ElevatorsState succ = new ElevatorsState(s);
                succ.passengers.get(i).in_elevator = false;
                succ.passengers.get(i).location = s.elevators.get(passenger.location);
                result.add(new ActionStatePair(a, succ));
            } else {
                // passenger is on a floor -> check if it can embark any elevators
                for (int j = 0; j < s.elevators.size(); j++) {
                    if (s.elevators.get(j) == passenger.location) {
                        Action a = new ElevatorsEmbarkAction(j, i);
                        ElevatorsState succ = new ElevatorsState(s);
                        succ.passengers.get(i).in_elevator = true;
                        succ.passengers.get(i).location = j;
                        result.add(new ActionStatePair(a, succ));
                    }
                }
            }
        }

        // loop over all elevators for move up/down actions
        for (int i = 0; i < s.elevators.size(); i++) {
            int elevator = s.elevators.get(i);
            // not on the ground floor -> move down possible
            if (elevator > 0) {
                Action a = new ElevatorsMoveDownAction(i, elevator);
                ElevatorsState succ = new ElevatorsState(s);
                succ.elevators.set(i,elevator-1);
                result.add(new ActionStatePair(a,succ));
            }
            // not on the top floor -> move up possible
            if (elevator < numberOfFloors-1) {
                Action a = new ElevatorsMoveUpAction(i, elevator);
                ElevatorsState succ = new ElevatorsState(s);
                succ.elevators.set(i,elevator+1);
                result.add(new ActionStatePair(a,succ));
            }
        }

        return result;
    }

    public int cost(Action a) {
        return a.cost();
    }


    public static StateSpace buildFromCmdline(ArrayList<String> args) {
        if (args.size() != 1) {
            Errors.usageError("need one input file argument");
        }

        String filename = args.get(0);
        System.out.println("Reading input from file " + filename + "...");
        Scanner scanner;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            Errors.fileError("input file not found: " + filename);
            scanner = new Scanner(""); // unreachable; silences compiler
        }

        int numPassengers = scanner.nextInt();
        if (numPassengers < 0)
            Errors.fileError("invalid number of passengers");
        int numElevators = scanner.nextInt();
        if (numElevators < 0)
            Errors.fileError("invalid number of elevators");
        int numFloors = scanner.nextInt();
        if (numFloors < 0)
            Errors.fileError("invalid number of floors");

        ArrayList<PassengerLocation> init_passengers = new ArrayList<PassengerLocation>();
        for (int i = 0; i < numPassengers; i++) {
            init_passengers.add(new PassengerLocation(false, scanner.nextInt()));
        }
        ArrayList<Integer> init_elevators = new ArrayList<Integer>();
        for (int i = 0; i < numElevators; i++) {
            init_elevators.add(scanner.nextInt());
        }
        ElevatorsState init = new ElevatorsState(init_passengers, init_elevators);

        ArrayList<Integer> goal_passengers = new ArrayList<Integer>();
        for (int i = 0; i < numPassengers; i++) {
            goal_passengers.add(scanner.nextInt());
        }


        if (scanner.hasNext())
        Errors.fileError("expected end of file");
        scanner.close();

        return new ElevatorsStateSpace(numPassengers, numElevators, numFloors, init, goal_passengers);
    }
}
