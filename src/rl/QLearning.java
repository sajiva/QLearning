package rl;

import java.util.*;

public class QLearning {

    private static long seed = 12345;
    private static Random random = new Random(seed);
    private static int steps = 10000;
    private static double gamma = 0.3;
    private static int currentState; 
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
    private static int[][] states = new int[50][3];
    private static double[][] QTable = new double[50][6];
    private static List<Character> operators = Arrays.asList('N', 'S', 'E', 'W', 'P', 'D');
    private static List<String> policies = Arrays.asList("PRandom","PExploit1","PExploit2");

    public static void initialize() {
        int count = 0;

        // fill states[0..24] with {1,1,0 to 5,5,0} and states[25..49] with {1,1,1 to 5,5,1}
        for (int x = 0; x <= 1; x++) {
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 5; j++) {
                    states[count] = new int[]{i, j, x};
                    count++;
                }
            }
        }

        // initialize QTable values to zero
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 6; j++) {
                QTable[i][j] = 0;
            }
        }

        currentState = 4; // {5,1,0}
    }

    public static void runExperiment(String policy, double alpha) {

        initialize();

        for (int step = 0; step < steps; step++) {

            if (isTerminalState()) {
                resetPDworld();
                System.out.println("Number of steps: " + step);
                printQTable();
            }

            char operator = selectOperator(policy);
            currentState = applyAction(operator, alpha);
        }

        System.out.println("After 10000 steps:");
        printQTable();
    }

    private static void resetPDworld() {

        for (int i = 0; i < 3; i++) {
            pickUpLocations[i][2] = 5;
            dropOffLocations[i][2] = 0;
        }
    }

    private static int applyAction(char operator, double alpha) {

        int nextState = 0;

        switch (operator) {
            case 'P':
                nextState = currentState + 25;
                takeBlockFromPickUpLocation(states[currentState]);
                break;
            case 'D':
                nextState = currentState - 25;
                dropBlockInDropOffLocation(states[currentState]);
                break;
            case 'N':
                nextState = currentState - 5;
                break;
            case 'S':
                nextState = currentState + 5;
                break;
            case 'E':
                nextState = currentState + 1;
                break;
            case 'W':
                nextState = currentState - 1;
                break;
        }

        updateQtable(operator, nextState, alpha);
        return nextState;
    }

    public static void updateQtable(char operator, int nextState, double alpha) {
        int op = operators.indexOf(operator);

        QTable[currentState][op] = (1 - alpha) * QTable[currentState][op] + alpha * (reward(operator) + gamma * maxQvalue(nextState));
    }

    public static double reward(char operator) {

        return (operator == 'D' || operator == 'P') ? 13 : -1;
    }

    public static double maxQvalue(int state) {

       return  Arrays.stream(QTable[state]).max().getAsDouble();
    }

    public static void takeBlockFromPickUpLocation(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (pickUpLocations[i][0] == state[0] && pickUpLocations[i][1] == state[1]) {
                pickUpLocations[i][2]--;
                break;
            }
        }
    }

    public static void dropBlockInDropOffLocation(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (dropOffLocations[i][0] == state[0] && dropOffLocations[i][1] == state[1]) {
                dropOffLocations[i][2]++;
                break;
            }
        }
    }

    public static char selectOperator(String policy) {

        int[] state = states[currentState];

        if (state[2] == 0 && pickUpApplicable(state))
            return 'P';

        if (state[2] == 1 && dropOffApplicable(state))
            return 'D';

        List<Character> validOperators = getValidOperators(state);

        int policyNo = policies.indexOf(policy);

		return operatorChosenByPolicy(policyNo, validOperators);
    }

    private static char operatorChosenByPolicy(int policyNo, List<Character> validOperators) {
    	
    	char selectedOperator = '\0';
    	
    	switch (policyNo) {
    	
    	case 0:
    		//PRandom
    		selectedOperator = chooseRandomOperator(validOperators);
    		break;
    	
    	case 1:
    		//PExploit1
    		selectedOperator = chooseExploit1Operator(validOperators);
    		break;
    	
    	case 2:
    		//PExploit2
    		selectedOperator = chooseExploit2Operator(validOperators);
    		break;
    	}

		return selectedOperator;
    }

	public static char chooseRandomOperator(List<Character> validOperators) {

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
	
	private static char chooseExploit1Operator(List<Character> validOperators) {
        double p = random.nextDouble();

        return (p < 0.65) ? chooseMaxQValueOperator(validOperators) : chooseRandomOperator(validOperators);
	}
	
	private static char chooseExploit2Operator(List<Character> validOperators) {
        double p = random.nextDouble();

        return (p < 0.9) ? chooseMaxQValueOperator(validOperators) : chooseRandomOperator(validOperators);
	}

	private static char chooseMaxQValueOperator(List<Character> validOperators) {
        List<Double> validOperatorQValue = new ArrayList<>();
        List<Character>  maxQValueOperators = new ArrayList<>();

        for (int i = 0; i < 4; i++) {   //operators.size() can be hard-coded '4' as 'P' || 'D' is not applicable here

            if (validOperators.contains(operators.get(i))) {
                validOperatorQValue.add(QTable[currentState][i]);
            }
        }

        double maxQValue = Collections.max(validOperatorQValue);
        for (int i = 0; i < validOperatorQValue.size(); i++) {

            if(validOperatorQValue.get(i) == maxQValue) {
                maxQValueOperators.add(validOperators.get(i));
            }
        }
        if(maxQValueOperators.size() == 1) {
            //Only one Operator has highest Value
            return maxQValueOperators.get(0);
        }
        else {
            return chooseRandomOperator(maxQValueOperators);
        }
    }

    public static List<Character> getValidOperators(int[] state) {
        List<Character> operators = new ArrayList<>();

        if (state[0] > 1)
            operators.add('N');
        if (state[0] < 5)
            operators.add('S');
        if (state[1] > 1)
            operators.add('W');
        if (state[1] < 5)
            operators.add('E');

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
            if (dropOffLocations[i][0] == state[0] && dropOffLocations[i][1] == state[1] && dropOffLocations[i][2] < 5)
                return true;
        }

        return false;
    }


    public static boolean isTerminalState() {

        return (dropOffLocations[0][2] == 5
                && dropOffLocations[1][2] == 5
                && dropOffLocations[2][2] == 5);
    }

    public static void printQTable() {
        System.out.println("\t\t\tN\t\tS\t\tE\t\tW\t\tP\t\tD\n");
        for (int i = 0; i < 50; i++) {
            System.out.print("(" + states[i][0] + "," + states[i][1] + "," + states[i][2] + ")" + "\t\t");
            for (int j = 0; j < 6; j++) {
                System.out.print(String.format("%.2f\t", QTable[i][j]));
            }
            System.out.print("\n");
        }
    }

    public static void main(String args[]) {

        // Experiment 1
//        runExperiment("PRandom", 0.3);
        // Experiment 2
        runExperiment("PExploit1", 0.3);
//        // Experiment 3
//        runExperiment("PExploit2", 0.3);
//        // Experiment 4
//        runExperiment("PExploit2", 0.5);
    }
}

