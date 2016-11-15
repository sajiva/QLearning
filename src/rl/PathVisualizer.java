package rl;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class PathVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Q-learning Path Visualization");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setGridLinesVisible(true);

        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Text arrows = new Text("\u2191\n\u2190   \u2192\n\u2193");
                arrows.setFont(Font.font("Tahoma", FontWeight.NORMAL, 32));
                arrows.setTextAlignment(TextAlignment.CENTER);
                grid.add(arrows, i, j);
            }
        }

        primaryStage.show();
    }
}
