package rl;

import javafx.application.Application;

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
    private static List<Character> operators = Arrays.asList('N', 'E', 'W', 'S', 'P', 'D');
    private static List<String> policies = Arrays.asList("PRandom","PExploit1","PExploit2");
    private static double bankAccount = 0;
    private static int noOfBlocksDelivered = 0;

    private static void initialize() {
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

        int terminalState = 0;

        for (int step = 0; step < steps; step++) {

            if (step % 100 == 0 && step <= 2000 && step > 0) {
                System.out.println("Steps: " + step);
                printQTable(step);
            }

            if (isTerminalState()) {

                terminalState++;
                System.out.println("Terminal state " + terminalState + " Number of steps: " + step);
                printQTable(step);

                // Exit if terminal state reached 4th time for PExploit1 and PExploit2
                if (terminalState == 4
                        && !policy.equalsIgnoreCase(policies.get(0))) {
                    return;
                }

                resetPDworld();
            }

            // Use PRandom for first 100 steps
            char operator = (step < 100) ? selectOperator(policies.get(0)) : selectOperator(policy);
            currentState = applyAction(operator, alpha);
        }

        System.out.println("Steps: 10000");
        printQTable(10000);
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
                noOfBlocksDelivered++;
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

    private static void updateQtable(char operator, int nextState, double alpha) {
        int op = operators.indexOf(operator);

        QTable[currentState][op] = (1 - alpha) * QTable[currentState][op] + alpha * (reward(operator) + gamma * maxQvalue(nextState));
        bankAccount += reward(operator);
    }

    private static double reward(char operator) {

        return (operator == 'D' || operator == 'P') ? 13 : -1;
    }

    private static double maxQvalue(int state) {

       return  Arrays.stream(QTable[state]).max().getAsDouble();
    }

    private static void takeBlockFromPickUpLocation(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (pickUpLocations[i][0] == state[0] && pickUpLocations[i][1] == state[1]) {
                pickUpLocations[i][2]--;
                break;
            }
        }
    }

    private static void dropBlockInDropOffLocation(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (dropOffLocations[i][0] == state[0] && dropOffLocations[i][1] == state[1]) {
                dropOffLocations[i][2]++;
                break;
            }
        }
    }

    private static char selectOperator(String policy) {

        int[] state = states[currentState];

        if (state[2] == 0 && pickUpApplicable(state))
            return 'P';

        if (state[2] == 1 && dropOffApplicable(state))
            return 'D';

        List<Character> validOperators = getValidOperators(state);

        int policyNo = 0;

        for (int i = 0; i < policies.size(); i++) {
            if (policies.get(i).equalsIgnoreCase(policy)) {
                policyNo = i;
                break;
            }
        }

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

	private static char chooseRandomOperator(List<Character> validOperators) {

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

    private static List<Character> getValidOperators(int[] state) {
        List<Character> operators = new ArrayList<>();

        if (state[0] > 1)
            operators.add('N');
        if (state[1] < 5)
            operators.add('E');
        if (state[1] > 1)
            operators.add('W');
        if (state[0] < 5)
            operators.add('S');

        return operators;
    }

    private static boolean pickUpApplicable(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (pickUpLocations[i][0] == state[0] && pickUpLocations[i][1] == state[1] && pickUpLocations[i][2] > 0)
                return true;
        }

        return false;
    }

    private static boolean dropOffApplicable(int[] state) {

        for (int i = 0; i < 3; i++) {
            if (dropOffLocations[i][0] == state[0] && dropOffLocations[i][1] == state[1] && dropOffLocations[i][2] < 5)
                return true;
        }

        return false;
    }


    private static boolean isTerminalState() {

        return (dropOffLocations[0][2] == 5
                && dropOffLocations[1][2] == 5
                && dropOffLocations[2][2] == 5);
    }

    private static void printQTable(int step) {

        System.out.println("Bank account of the agent: " + bankAccount);
        System.out.println(String.format("Rewards received/Number of operators: %.4f", bankAccount/step));
        System.out.println(String.format("Blocks delivered/Number of operators: %.4f",  (double)noOfBlocksDelivered/step));
        System.out.println("Q-Table: ");
        System.out.println("\t\t\tN\t\tE\t\tW\t\tS\n");
        for (int i = 0; i < 50; i++) {
            System.out.print("(" + states[i][0] + "," + states[i][1] + "," + states[i][2] + ")" + "\t\t");
            for (int j = 0; j < 4; j++) {
                System.out.print(String.format("%.2f\t", QTable[i][j]));
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static List<List<Character>> findAttractivePaths() {
        List<List<Character>> attractivePaths = new ArrayList<>();

        for (int i = 0; i < 50; i++) {

            List<Character> validOperators = getValidOperators(states[i]);
            List<Double> qValues = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                if (validOperators.contains(operators.get(j)))
                qValues.add(QTable[i][j]);
            }
            double maxQValue = Collections.max(qValues);
            List<Character> paths = new ArrayList<>();

            for (int k = 0; k < 4; k++) {
                if (Double.compare(QTable[i][k], maxQValue) == 0
                        && validOperators.contains(operators.get(k))) {
                    paths.add(operators.get(k));
                }
            }

            attractivePaths.add(paths);
        }

        return attractivePaths;
    }

    public static void main(String args[]) {

    	Scanner scanner = new Scanner(System.in);

    	System.out.println("Enter the Experiment No : ");

    	int experimentNo = scanner.nextInt();

    	if (experimentNo ==1)
    		runExperiment("PRandom", 0.3);		// Experiment 1

    	else if (experimentNo == 2)
        	runExperiment("PExploit1", 0.3);    // Experiment 2

    	else if (experimentNo == 3)
        	runExperiment("PExploit2", 0.3);	// Experiment 3

    	else if (experimentNo == 4)
    		runExperiment("PExploit2", 0.5);	// Experiment 4

    	scanner.close();

        Application.launch(PathVisualizer.class);

    }
}

