package src.rl;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.List;

public class PathVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Q-learning Path Visualization");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setGridLinesVisible(true);

        Scene scene = new Scene(grid, 1500, 1500);
        primaryStage.setScene(scene);

        String upArrow = "\u2191";
        String downArrow = "\u2193";
        String leftArrow = "\u2190";
        String rightArrow = "\u2192";

        List<List<Character>> attractivePaths = QLearning.findAttractivePaths();
        int r = 0;
        for (int i = 0; i < 50; i++) {
            int c = i % 5;
//            for (int j = 0; j < 5; j++) {
            List<Character> paths = attractivePaths.get(i);
            Text arrows = null;
            System.out.println("State No : " + i);
            System.out.println("Path size : " + paths.size());
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
                    arrows = new Text(leftArrow + "\n" + upArrow);
                } else if (paths.contains('N') && paths.contains('S')) {
                    arrows = new Text(upArrow + "\n" + downArrow);
                } else if (paths.contains('S') && paths.contains('E')) {
                    arrows = new Text(downArrow + "\n" + rightArrow);
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
            } else if (paths.size() == 4) {
                if (paths.contains('N') && paths.contains('S') && paths.contains('E') && paths.contains('W')) {
                    arrows = new Text(upArrow + "\n" + leftArrow + rightArrow + "\n" + downArrow  );
                } 
            }
            	//Text arrows = new Text("\u2191\n\u2190   \u2192\n\u2193");
                arrows.setFont(Font.font("Tahoma", FontWeight.NORMAL, 32));
                arrows.setTextAlignment(TextAlignment.CENTER);
                if(r == 0){
                	grid.add(new Text(" States "), 0, r);
                	grid.add(new Text(" with"), 1, r);
                	grid.add(new Text(" x ="), 2, r);
                	grid.add(new Text(" 0"), 3, r);
                	grid.add(new Text(""), 4, r);
                	r++;
                }
                if(r == 6){
                	grid.add(new Text(" States "), 0, r);
                	grid.add(new Text(" with"), 1, r);
                	grid.add(new Text(" x ="), 2, r);
                	grid.add(new Text(" 1"), 3, r);
                	grid.add(new Text(""), 4, r);
                	r++;
                }
                grid.add(arrows, c, r);
//            }
            if (c == 4) r++;
        }

        primaryStage.show();
    }
}