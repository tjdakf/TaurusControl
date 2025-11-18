package tauruscontrol.view;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainView extends BorderPane {

    public MainView(Stage stage) {
        setPrefSize(1200, 700);
        setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );

        TitleBar titleBar = new TitleBar(stage);
        setTop(titleBar);
    }
}