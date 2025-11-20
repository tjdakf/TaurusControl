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
import tauruscontrol.util.UIConstants;

import java.util.function.BiConsumer;

public class PasswordDialog extends StackPane {

    private final PasswordField passwordField;
    private final BiConsumer<Terminal, String> onConfirm;
    private final Runnable onCancel;

    public PasswordDialog(Terminal terminal, BiConsumer<Terminal, String> onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;

        getStylesheets().add(getClass().getResource("/styles/dialog-components.css").toExternalForm());
        getStyleClass().add("dialog-overlay");

        VBox card = new VBox(UIConstants.SPACING_LARGE);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(UIConstants.DIALOG_LARGE_WIDTH, UIConstants.DIALOG_LARGE_HEIGHT);
        card.setMaxSize(UIConstants.DIALOG_LARGE_WIDTH, UIConstants.DIALOG_LARGE_HEIGHT);
        card.getStyleClass().add("dialog-card-large");
        card.setPadding(new Insets(UIConstants.PANEL_GAP));

        Label titleLabel = new Label(terminal.getAliasName() + " 로그인");
        titleLabel.getStyleClass().add("dialog-title");

        // 비밀번호 필드와 토글 버튼을 담을 컨테이너
        StackPane passwordContainer = new StackPane();
        passwordContainer.setPrefWidth(290);

        passwordField = new PasswordField();
        passwordField.setPromptText("비밀번호");
        passwordField.setPrefWidth(290);
        passwordField.getStyleClass().add("dialog-password-field");

        TextField textField = new TextField();
        textField.setPromptText("비밀번호");
        textField.setPrefWidth(290);
        textField.getStyleClass().add("dialog-password-field");
        textField.setVisible(false);
        textField.setManaged(false);

        // 텍스트 동기화
        passwordField.textProperty().bindBidirectional(textField.textProperty());

        // 토글 버튼
        Button toggleButton = new Button("👁");
        toggleButton.setPrefSize(30, 30);
        toggleButton.getStyleClass().add("dialog-toggle-button");
        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleButton, new Insets(0, UIConstants.SPACING_SMALL, 0, 0));

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

        HBox buttonBox = new HBox(UIConstants.SPACING_MEDIUM);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("확인");
        Button cancelButton = new Button("취소");

        confirmButton.setPrefWidth(UIConstants.BUTTON_MEDIUM);
        cancelButton.setPrefWidth(UIConstants.BUTTON_MEDIUM);
        confirmButton.getStyleClass().add("dialog-button-primary-bordered");
        cancelButton.getStyleClass().add("dialog-button-secondary-bordered");

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
}