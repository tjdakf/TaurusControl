package tauruscontrol.view;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TitleBar extends HBox {

    private double xOffset = 0;
    private double yOffset = 0;

    public TitleBar(Stage stage) {
        setPrefHeight(40);
        setStyle("-fx-background-color: #5a5a5a;" +
                "-fx-background-radius: 10 10 0 0;" +  // 상단만 라운드
                "-fx-border-radius: 10 10 0 0;"
        );

        Region leftSpacer = new Region();
        leftSpacer.setPrefWidth(80);

        // 드래그 가능한 영역 (타이틀 포함)
        HBox dragRegion = new HBox();
        HBox.setHgrow(dragRegion, Priority.ALWAYS);
        dragRegion.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Taurus Control");
        titleLabel.setStyle("-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;"
        );

        dragRegion.getChildren().add(titleLabel);

// 드래그 이벤트 (dragRegion에 그대로 적용)
        dragRegion.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        dragRegion.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // 최소화 버튼
        Button minimizeButton = new Button("—");
        styleMinimizeButton(minimizeButton);
        minimizeButton.setOnAction(event -> stage.setIconified(true));
        minimizeButton.setFocusTraversable(false);

        // 닫기 버튼
        Button closeButton = new Button("✕");
        styleCloseButton(closeButton);
        closeButton.setOnAction(event -> stage.close());
        closeButton.setFocusTraversable(false);

        getChildren().addAll(leftSpacer, dragRegion, minimizeButton, closeButton);
    }

    private void styleMinimizeButton(Button button) {
        button.setPrefSize(40, 40);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(event ->
                button.setStyle(
                        "-fx-background-color: #707070;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-border-width: 0;" +
                                "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(event ->
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-border-width: 0;" +
                                "-fx-cursor: hand;"
                )
        );
    }

    private void styleCloseButton(Button button) {
        button.setPrefSize(40, 40);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(event ->
                button.setStyle(
                        "-fx-background-color: #e74c3c;" +  // 빨간색
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-border-width: 0;" +
                                "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(event ->
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-border-width: 0;" +
                                "-fx-cursor: hand;"
                )
        );
    }
}