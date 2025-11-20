package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.domain.terminal.Terminal;

public abstract class BaseSettingDialog extends StackPane {

    protected final Terminal terminal;
    protected final Runnable onClose;

    public BaseSettingDialog(Terminal terminal, String title, Runnable onClose) {
        this.terminal = terminal;
        this.onClose = onClose;

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        setAlignment(Pos.CENTER);

        setOnMouseClicked(e -> e.consume());
        setOnMousePressed(e -> e.consume());
        setOnMouseReleased(e -> e.consume());

        addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (onClose != null) {
                    onClose.run();
                }
                event.consume();
            }
        });

        VBox card = new VBox(0);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefSize(600, 400);
        card.setMaxSize(600, 400);
        card.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );
        card.setPadding(new Insets(5, 15, 0, 0));

        HBox header = createHeader(title);

        card.getChildren().add(header);
        getChildren().add(card);

        requestFocus();
    }

    protected void initializeContent() {
        VBox card = (VBox) getChildren().get(0);
        VBox content = createContent();
        card.getChildren().add(content);
    }

    private HBox createHeader(String title) {
        HBox header = new HBox();
        header.setAlignment(Pos.TOP_RIGHT);
        header.setMinHeight(40);
        header.setPrefHeight(40);

        Button backButton = new Button("↩");
        backButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0;" +
                        "-fx-padding: 0 5 0 5;"
        );
        backButton.setOnAction(event -> {
            if (onClose != null) {
                onClose.run();
            }
        });

        backButton.setOnMouseEntered(e ->
                backButton.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 20px;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-width: 0;" +
                                "-fx-padding: 0 5 0 5;" +
                                "-fx-background-radius: 5;"
                )
        );

        backButton.setOnMouseExited(e ->
                backButton.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 20px;" +
                                "-fx-cursor: hand;" +
                                "-fx-border-width: 0;" +
                                "-fx-padding: 0 5 0 5;"
                )
        );

        header.getChildren().add(backButton);
        return header;
    }

    protected abstract VBox createContent();
}
