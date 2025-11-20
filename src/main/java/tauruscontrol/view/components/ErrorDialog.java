package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.util.UIConstants;

public class ErrorDialog extends StackPane {

    public ErrorDialog(String title, String message) {
        getStylesheets().add(getClass().getResource("/styles/dialog-components.css").toExternalForm());
        getStyleClass().add("dialog-overlay");

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(UIConstants.DIALOG_MEDIUM_WIDTH, UIConstants.DIALOG_MEDIUM_HEIGHT);
        box.setMaxSize(UIConstants.DIALOG_MEDIUM_WIDTH, UIConstants.DIALOG_MEDIUM_HEIGHT);
        box.getStyleClass().add("dialog-card-small");
        box.setPadding(new Insets(UIConstants.SPACING_LARGE));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title-error");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Label messageLabel = new Label(message != null ? message : "알 수 없는 오류");
        messageLabel.getStyleClass().add("dialog-message");
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