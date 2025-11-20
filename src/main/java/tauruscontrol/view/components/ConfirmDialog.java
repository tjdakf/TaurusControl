package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ConfirmDialog extends StackPane {

    public ConfirmDialog(String message, Runnable onConfirm, Runnable onCancel) {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(350, 180);
        card.setMaxSize(350, 180);
        card.setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );
        card.setPadding(new Insets(30));

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(290);
        messageLabel.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("예");
        Button cancelButton = new Button("아니오");

        styleDialogButton(confirmButton, true);
        styleDialogButton(cancelButton, false);

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

    private void styleDialogButton(Button button, boolean isPrimary) {
        button.setPrefWidth(100);

        String bgColor = "#5a5a5a";
        String hoverColor = "#323232";
        if (isPrimary) {
            bgColor = "#1E88E5";
            hoverColor = "#1976D2";
        }

        String normalStyle = buildButtonStyle(bgColor);
        String hoverStyle = buildButtonStyle(hoverColor);

        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    private String buildButtonStyle(String bgColor) {
        return "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'System';" +
                "-fx-border-color: #6a6a6a;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;";
    }
}
