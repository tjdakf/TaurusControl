package tauruscontrol.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.util.UIConstants;

public class ProgressDialog extends StackPane {

    public ProgressDialog(String message) {
        getStylesheets().add(getClass().getResource("/styles/dialog-components.css").toExternalForm());
        getStyleClass().add("dialog-overlay");
        setAlignment(Pos.CENTER);

        setOnMouseClicked(e -> e.consume());
        setOnMousePressed(e -> e.consume());
        setOnMouseReleased(e -> e.consume());

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(UIConstants.DIALOG_SMALL_WIDTH, UIConstants.DIALOG_SMALL_HEIGHT);
        box.setMaxSize(UIConstants.DIALOG_SMALL_WIDTH, UIConstants.DIALOG_SMALL_HEIGHT);
        box.getStyleClass().add("dialog-card-small");

        Label label = new Label(message);
        label.getStyleClass().add("dialog-message-large");

        box.getChildren().add(label);
        getChildren().add(box);
    }
}