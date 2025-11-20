package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.util.UIConstants;

public class ConfirmDialog extends StackPane {

    public ConfirmDialog(String message, Runnable onConfirm, Runnable onCancel) {
        getStylesheets().add(getClass().getResource("/styles/dialog-components.css").toExternalForm());
        getStyleClass().add("dialog-overlay");

        VBox card = new VBox(UIConstants.SPACING_LARGE);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(UIConstants.DIALOG_LARGE_WIDTH, 180);
        card.setMaxSize(UIConstants.DIALOG_LARGE_WIDTH, 180);
        card.getStyleClass().add("dialog-card-large");
        card.setPadding(new Insets(UIConstants.PANEL_GAP));

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message-large");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(290);
        messageLabel.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(UIConstants.SPACING_MEDIUM);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("예");
        Button cancelButton = new Button("아니오");

        confirmButton.setPrefWidth(UIConstants.BUTTON_MEDIUM);
        cancelButton.setPrefWidth(UIConstants.BUTTON_MEDIUM);
        confirmButton.getStyleClass().add("dialog-button-primary-bordered");
        cancelButton.getStyleClass().add("dialog-button-secondary-bordered");

        confirmButton.setOnAction(event -> {
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        cancelButton.setOnAction(event -> {
            if (onCancel != null) {
                onCancel.run();
            }
        });

        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        card.getChildren().addAll(messageLabel, buttonBox);
        getChildren().add(card);
    }
}
