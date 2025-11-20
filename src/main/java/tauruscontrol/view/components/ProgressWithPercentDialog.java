package tauruscontrol.view.components;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProgressWithPercentDialog extends StackPane {

    private final Label messageLabel;
    private final Label percentLabel;

    public ProgressWithPercentDialog(String message) {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(250, 150);
        box.setMaxSize(250, 150);
        box.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );

        messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        percentLabel = new Label("0%");
        percentLabel.setStyle("-fx-text-fill: #1E88E5; -fx-font-size: 24px; -fx-font-weight: bold;");

        box.getChildren().addAll(messageLabel, percentLabel);
        getChildren().add(box);
    }

    public void updateProgress(int percent) {
        Platform.runLater(() -> percentLabel.setText(percent + "%"));
    }

    public void updateMessage(String message) {
        Platform.runLater(() -> messageLabel.setText(message));
    }
}