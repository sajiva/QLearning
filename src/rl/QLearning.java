package rl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QLearning {

    private static double gamma = 0.3;
    private static long seed = 12345;
    private static Random random = new Random(seed);
    private static int[] startState = new int[]{1, 5, 0};
    private static int[][] pickUpLocations = new int[][]{
            {1, 1, 5},
            {3, 3, 5},
            {5, 5, 5}
    };
    private static int[][] dropOffLocations = new int[][] {
            {5, 1, 0},
            {5, 3, 0},
            {2, 5, 0}
    };
    private enum Action {
        PICKUP, DROPOFF, MOVENORTH, MOVESOUTH, MOVEEAST, MOVEWEST
    }
    private static double[][] QTable = new double[50][6];


    public static void runExperiment() {

        int[] currentState = startState;

        for (int step = 0; step < 10; step++) {
            Action operator = selectOperator(currentState);
            currentState = applyAction(currentState, operator);
        }


    }

    private static int[] applyAction(int[] state, Action operator) {

        switch (operator) {
            case PICKUP:
                state[2] = 1;
                break;
            case DROPOFF:
                state[2] = 0;
                break;
            case MOVENORTH:
                state[0]--;
                break;
            case MOVESOUTH:
                state[0]++;
                break;
            case MOVEEAST:
                state[1]++;
                break;
            case MOVEWEST:
                state[1]--;
                break;
        }

        return state;
    }

    public static void updateQtable() {

    }

    public static Action selectOperator(int[] state) {

        if (state[2] == 0 && pickUpApplicable(state))
            return Action.PICKUP;

        if (state[2] == 1 && dropOffApplicable(state))
            return Action.DROPOFF;

        List<Action> validOperators = getValidOperators(state);
        return chooseRandomOperator(validOperators);
    }

    private static Action chooseRandomOperator(List<Action> validOperators) {
        double p = random.nextDouble();
        int index = 0;
        int N = validOperators.size();

        for (int i = 0; i < N; i++) {
            if (p < (double)(i+1)/N) {
                index = i;
                break;
            }
        }

        return validOperators.get(index);
    }

    public static List<Action> getValidOperators(int[] state) {
        List<Action> operators = new ArrayList<>();

        if (state[0] > 1)
            operators.add(Action.MOVENORTH);
        if (state[0] < 5)
            operators.add(Action.MOVESOUTH);
        if (state[1] > 1)
            operators.add(Action.MOVEWEST);
        if (state[1] < 5)
            operators.add(Action.MOVEEAST);

        return operators;
    }

    public static boolean pickUpApplicable(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (pickUpLocations[i][0] == state[0] && pickUpLocations[i][1] == state[1] && pickUpLocations[i][2] > 0)
                return true;
        }

        return false;
    }

    public static boolean dropOffApplicable(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (dropOffLocations[i][0] == state[0] && pickUpLocations[i][1] == state[1] && pickUpLocations[i][2] < 5)
                return true;
        }

        return false;
    }


    public static boolean isTerminalState() {

        return (dropOffLocations[0][3] == 5
                && dropOffLocations[1][3] == 5
                && dropOffLocations[2][3] == 5);
    }

    public static void main(String args[]) {
        runExperiment();
    }
}
