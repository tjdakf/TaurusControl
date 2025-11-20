package tauruscontrol.view.components;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.util.UIConstants;

public class ProgressWithPercentDialog extends StackPane {

    private final Label messageLabel;
    private final Label percentLabel;

    public ProgressWithPercentDialog(String message) {
        getStylesheets().add(getClass().getResource("/styles/dialog-components.css").toExternalForm());
        getStyleClass().add("dialog-overlay");

        VBox box = new VBox(UIConstants.SPACING_MEDIUM);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(UIConstants.DIALOG_MEDIUM_WIDTH, UIConstants.DIALOG_MEDIUM_HEIGHT);
        box.setMaxSize(UIConstants.DIALOG_MEDIUM_WIDTH, UIConstants.DIALOG_MEDIUM_HEIGHT);
        box.getStyleClass().add("dialog-card-small");

        messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message-large");

        percentLabel = new Label("0%");
        percentLabel.getStyleClass().add("progress-percent-label");

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