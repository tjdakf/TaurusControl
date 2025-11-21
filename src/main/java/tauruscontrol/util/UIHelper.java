package tauruscontrol.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class UIHelper {

    public static HBox createEmptyRow(int index) {
        HBox row = new HBox();
        row.setMinHeight(UIConstants.ROW_HEIGHT);
        row.setPrefHeight(UIConstants.ROW_HEIGHT);

        String bgColor = getAlternatingBackgroundColor(index);
        row.setStyle("-fx-background-color: " + bgColor + ";");

        return row;
    }

    public static HBox createMessageRow(String message) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(UIConstants.SPACING_MEDIUM, UIConstants.SPACING_MEDIUM,
                UIConstants.SPACING_MEDIUM, UIConstants.SPACING_LARGE));
        row.setMinHeight(UIConstants.ROW_HEIGHT);
        row.setPrefHeight(UIConstants.ROW_HEIGHT);
        row.setStyle("-fx-background-color: #3a3a3a;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");

        row.getChildren().add(messageLabel);
        return row;
    }

    public static String getAlternatingBackgroundColor(int index) {
        if (index % 2 == 1) {
            return "#323232";
        }
        return "#3a3a3a";
    }
}
