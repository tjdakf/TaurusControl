package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.domain.terminal.Terminal;

import java.util.function.BiConsumer;

public class PasswordDialog extends StackPane {

    private final PasswordField passwordField;
    private final BiConsumer<Terminal, String> onConfirm;
    private final Runnable onCancel;

    public PasswordDialog(Terminal terminal, BiConsumer<Terminal, String> onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(350, 200);
        card.setMaxSize(350, 200);
        card.setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );
        card.setPadding(new Insets(30));

        Label titleLabel = new Label(terminal.getAliasName() + " 로그인");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // 비밀번호 필드와 토글 버튼을 담을 컨테이너
        StackPane passwordContainer = new StackPane();
        passwordContainer.setPrefWidth(290);

        passwordField = new PasswordField();
        passwordField.setPromptText("비밀번호");
        passwordField.setPrefWidth(290);
        passwordField.setStyle(
                "-fx-background-color: #323232;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888888;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 40 10 10;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );

        TextField textField = new TextField();
        textField.setPromptText("비밀번호");
        textField.setPrefWidth(290);
        textField.setStyle(
                "-fx-background-color: #323232;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888888;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 40 10 10;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );
        textField.setVisible(false);
        textField.setManaged(false);

        // 텍스트 동기화
        passwordField.textProperty().bindBidirectional(textField.textProperty());

        // 토글 버튼
        Button toggleButton = new Button("👁");
        toggleButton.setPrefSize(30, 30);
        toggleButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #cccccc;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0;"
        );
        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleButton, new Insets(0, 5, 0, 0));

        toggleButton.setOnMousePressed(event -> {
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            textField.setVisible(true);
            textField.setManaged(true);
        });

        toggleButton.setOnMouseReleased(event -> {
            textField.setVisible(false);
            textField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
        });

        passwordContainer.getChildren().addAll(passwordField, textField, toggleButton);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("확인");
        Button cancelButton = new Button("취소");

        styleDialogButton(confirmButton, true);
        styleDialogButton(cancelButton, false);

        confirmButton.setOnAction(event -> {
            String password = passwordField.getText();
            if (!password.isEmpty()) {
                onConfirm.accept(terminal, password);
            }
        });

        cancelButton.setOnAction(event -> onCancel.run());

        passwordField.setOnAction(event -> confirmButton.fire());
        textField.setOnAction(event -> confirmButton.fire());

        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        card.getChildren().addAll(titleLabel, passwordContainer, buttonBox);
        getChildren().add(card);

        // 다이얼로그가 렌더링된 후 포커스 설정
        javafx.application.Platform.runLater(() -> passwordField.requestFocus());
    }

    private void styleDialogButton(Button button, boolean isPrimary) {
        button.setPrefWidth(100);
        String bgColor = isPrimary ? "#1E88E5" : "#5a5a5a";
        String hoverColor = isPrimary ? "#1976D2" : "#323232";

        button.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
    }
}