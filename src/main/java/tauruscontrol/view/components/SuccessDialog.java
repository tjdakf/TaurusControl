package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SuccessDialog extends StackPane {

    public SuccessDialog(String title, String message) {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(250, 150);
        box.setMaxSize(250, 150);
        box.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );
        box.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #1E88E5; -fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Label messageLabel = new Label(message != null ? message : "성공");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(210);
        messageLabel.setAlignment(Pos.CENTER);

        box.getChildren().addAll(titleLabel, messageLabel);
        getChildren().add(box);
    }

    public void showTemporary(int durationMs) {
        new Thread(() -> {
            try {
                Thread.sleep(durationMs);
                javafx.application.Platform.runLater(() -> {
                    if (getParent() != null) {
                        ((javafx.scene.layout.Pane) getParent()).getChildren().remove(this);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}