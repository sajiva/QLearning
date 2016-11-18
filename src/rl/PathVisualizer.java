package rl;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.List;

public class PathVisualizer extends Application {

    private static long[] seed = new long[] {12345, 67890};
    private static String[] policies = new String[] {"PRandom","PExploit1","PExploit2", "PExploit2"};
    private static double[] alpha = new double[] {0.3, 0.3, 0.3, 0.5};

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        QLearning.initialize();

        QLearning.runExperiment(policies[0], alpha[0], 1, seed[0]);	// Experiment 1 Execution 1
        primaryStage.setTitle("Experiment 1 Execution 1");
        visualizePath(primaryStage);

        for (int i = 2; i <= 8; i++) {
            int experiment = Math.round((i+1)/2);
            int execution = (i % 2 == 0) ? 2 : 1;
//            System.out.println("experiment " + experiment + " execution " + execution);

            primaryStage = new Stage();
            primaryStage.setTitle("Experiment " + experiment + " Execution " + execution);
            QLearning.runExperiment(policies[experiment - 1], alpha[experiment - 1], experiment, seed[execution - 1]);
            visualizePath(primaryStage);
        }

        QLearning.closePrinter();
    }

    @SuppressWarnings("null")
	public static void visualizePath(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setGridLinesVisible(true);
       
        Scene scene = new Scene(grid, 1700, 1300);
        primaryStage.setScene(scene);

        String upArrow = "\u2191";
        String downArrow = "\u2193";
        String leftArrow = "\u2190";
        String rightArrow = "\u2192";

        List<List<Character>> attractivePaths = QLearning.findAttractivePaths();
        double[][] QTable = QLearning.getQTable();

        int r = 0;
        for (int i = 0; i < 50; i++) {
            int c = i % 5;
            List<Character> paths = attractivePaths.get(i);
            Text arrows = null;
            
            if (paths.size() == 1) {
                switch (paths.get(0)) {
                    case 'N':
                        arrows = new Text(upArrow);
                        break;
                    case 'E': 
                        arrows = new Text(rightArrow);
                        break;
                    case 'W':
                        arrows = new Text(leftArrow);
                        break;
                    case 'S':
                        arrows = new Text(downArrow);
                        break;
                }
            } else if (paths.size() == 2) {
                if (paths.contains('N') && paths.contains('E')) {
                    arrows = new Text(upArrow + "\n" + rightArrow);
                } else if (paths.contains('N') && paths.contains('W')) {
                    arrows = new Text(upArrow + "\n" + leftArrow);
                } else if (paths.contains('N') && paths.contains('S')) {
                    arrows = new Text(upArrow + "\n" + downArrow);
                } else if (paths.contains('S') && paths.contains('E')) {
                    arrows = new Text(rightArrow + "\n" + downArrow);
                } else if (paths.contains('S') && paths.contains('W')) {
                    arrows = new Text(leftArrow + "\n" + downArrow);
                } else if (paths.contains('E') && paths.contains('W')) {
                    arrows = new Text(leftArrow + rightArrow);
                }
            } else if (paths.size() == 3) {
                if (paths.contains('N') && paths.contains('S') && paths.contains('E')) {
                    arrows = new Text(upArrow + "\n" + "   " + rightArrow + "\n" + downArrow);
                } else if (paths.contains('N') && paths.contains('S') && paths.contains('W')) {
                    arrows = new Text(upArrow + "\n" + leftArrow + "\n" + downArrow);
                } else if (paths.contains('N') && paths.contains('E') && paths.contains('W')) {
                    arrows = new Text(upArrow + "\n" + leftArrow + rightArrow);
                } else if (paths.contains('S') && paths.contains('E') && paths.contains('W')) {
                    arrows = new Text(leftArrow + rightArrow + "\n" + downArrow);
                }
            } else  {
                arrows = new Text(" ");
            }

            if(r == 0){
            	
            	GridPane[] gridPane = new GridPane[11];
            
            	for(int g = 0 ; g <= 10 ; g++){
            		gridPane[g] = new GridPane();
            		gridPane[g].setStyle("-fx-background-color: palegreen; "); 
            		grid.add(gridPane[g], g, r);
            	}
            	            	
            	grid.add(new Text(" States"), 0, r);
                grid.add(new Text("  with "), 1, r);
                grid.add(new Text("  X = "), 2, r);
                grid.add(new Text(" 0    "), 3, r);
                grid.add(new Text("      "), 4, r);
                
                grid.add(new Text("  Q Values :"), 6, r);
                grid.add(new Text("States with X = 0"), 7, r);
              
                r++; 
            }
            if(r == 6){
            	
            	GridPane[] gridPane = new GridPane[11];
                
            	for(int g = 0 ; g <= 10 ; g++){
            		gridPane[g] = new GridPane();
            		gridPane[g].setStyle("-fx-background-color: palegreen; "); 
            		grid.add(gridPane[g], g, r);
            	}
            	
                grid.add(new Text(" States"), 0, r);
                grid.add(new Text(" with "), 1, r);
                grid.add(new Text("  X = "), 2, r);
                grid.add(new Text(" 1    "), 3, r);
                grid.add(new Text("      "), 4, r);
                
                grid.add(new Text("  Q Values :"), 6, r);
                grid.add(new Text("States with X = 1"), 7, r);
                
                r++;
            }
            //arrows.setFont(Font.font("Tahoma", FontWeight.NORMAL, 26));
            arrows.setTextAlignment(TextAlignment.CENTER);
            grid.add(arrows, c, r);
            
            GridPane emptyCol = new GridPane();
            emptyCol.setStyle("-fx-background-color: palegreen; ");
            grid.add(emptyCol, 5, r);
            grid.add(new Text("     "), 5, r);
             
            String qValueN = String.format("%.2f", QTable[i][0]);
            String qValueE = String.format("%.2f", QTable[i][1]);
            String qValueW = String.format("%.2f", QTable[i][2]);
            String qValueS = String.format("%.2f", QTable[i][3]);

            Text qValues = new Text(qValueN + "\n" + qValueW + "\t\t" + qValueE + "\n" + qValueS);
            //qValues.setFont(Font.font("Tahoma", FontWeight.NORMAL, 26));
            qValues.setTextAlignment(TextAlignment.CENTER);
            grid.add(qValues, c + 6, r);

            if (c == 4)
            	r++;
        }

        primaryStage.show();
    }
}
