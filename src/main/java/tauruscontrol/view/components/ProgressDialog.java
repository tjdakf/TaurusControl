package tauruscontrol.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProgressDialog extends StackPane {

    public ProgressDialog(String message) {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(180, 120);
        box.setMaxSize(180, 120);
        box.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );

        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        box.getChildren().add(label);
        getChildren().add(box);
    }
}