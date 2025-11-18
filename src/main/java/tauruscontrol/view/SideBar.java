package tauruscontrol.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.StackPane;

public class SideBar extends VBox {

    private Button selectedButton = null;

    public SideBar() {
        setPrefWidth(220);
        setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-background-radius: 0 0 0 10;" +
                        "-fx-border-radius: 0 0 0 10;"
        );

        // 로고 영역
        StackPane logo = createLogo();

        // 메뉴 버튼들
        Button loginButton = createMenuButton("로그인 관리");
        Button playButton = createMenuButton("재생 관리");
        Button scheduleButton = createMenuButton("스케줄 관리");
        Button settingButton = createMenuButton("터미널 설정");

        // 첫 번째 버튼 기본 선택
        selectButton(loginButton);

        getChildren().addAll(logo, loginButton, playButton, scheduleButton, settingButton);
    }

    private StackPane createLogo() {
        StackPane logoPane = new StackPane();
        logoPane.setPrefHeight(200);
        logoPane.setAlignment(Pos.CENTER);

        // 검은 원
        Circle circle = new Circle(70);
        circle.setFill(Color.BLACK);

        // 텍스트
        Text text = new Text("Taurus\nControl");
        text.setFill(Color.WHITE);
        text.setFont(Font.font("System", FontWeight.BOLD, 20));
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        logoPane.getChildren().addAll(circle, text);
        return logoPane;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(220);
        button.setPrefHeight(80);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-alignment: center;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(event -> {
            if (selectedButton != button) {
                button.setStyle(
                        "-fx-background-color: #4a4a4a;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 18px;" +
                                "-fx-alignment: center;" +
                                "-fx-border-width: 0;" +
                                "-fx-cursor: hand;"
                );
            }
        });

        button.setOnMouseExited(event -> {
            if (selectedButton != button) {
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 18px;" +
                                "-fx-alignment: center;" +
                                "-fx-border-width: 0;" +
                                "-fx-cursor: hand;"
                );
            }
        });

        button.setOnAction(event -> selectButton(button));

        return button;
    }

    private void selectButton(Button button) {
        // 이전 선택 해제
        if (selectedButton != null) {
            selectedButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-alignment: center;" +
                            "-fx-border-width: 0;" +
                            "-fx-cursor: hand;"
            );
        }

        // 새로운 버튼 선택
        selectedButton = button;
        button.setStyle(
                "-fx-background-color: #1E88E5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-alignment: center;" +
                        "-fx-border-width: 0;" +
                        "-fx-cursor: hand;"
        );
    }
}