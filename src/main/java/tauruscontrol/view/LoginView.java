package tauruscontrol.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import tauruscontrol.controller.LoginController;
import tauruscontrol.domain.terminal.Terminal;

import java.util.List;

public class LoginView extends StackPane {

    private final LoginController controller;
    private final VBox terminalListContainer;
    private final VBox loadingBox;
    private final VBox contentBox;
    private StackPane passwordDialog;
    private StackPane loginProgressBox;
    private javafx.scene.control.PasswordField currentPasswordField;

    public LoginView() {
        this.controller = new LoginController();

        // 로딩 박스
        loadingBox = createLoadingBox();

        // 메인 컨텐츠
        contentBox = new VBox();
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setFillWidth(true);

        // 검색 버튼
        Button searchButton = new Button("터미널 검색");
        styleSearchButton(searchButton);
        searchButton.setOnAction(event -> searchTerminals());
        VBox.setMargin(searchButton, new Insets(20,0, 30, 50));

        // 헤더
        HBox header = createHeader();

        // 터미널 리스트 컨테이너
        terminalListContainer = new VBox();
        terminalListContainer.setStyle("-fx-background-color: #3e3e3e; -fx-border-width: 0; -fx-spacing: 0;");
        terminalListContainer.setFillWidth(true);
        terminalListContainer.setMinHeight(0);
        terminalListContainer.setPrefHeight(0);
        VBox.setVgrow(terminalListContainer, Priority.ALWAYS);
        terminalListContainer.setMaxHeight(Double.MAX_VALUE);
        terminalListContainer.setSnapToPixel(true);

        // 오버플로우 완전히 숨기기
        terminalListContainer.setClip(new javafx.scene.shape.Rectangle());
        terminalListContainer.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            javafx.scene.shape.Rectangle clip = (javafx.scene.shape.Rectangle) terminalListContainer.getClip();
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });

        contentBox.getChildren().addAll(searchButton, header, terminalListContainer);
        contentBox.setVisible(false);

        getChildren().addAll(contentBox, loadingBox);
    }

    public void refresh() {
        searchTerminals();
    }

    private void renderTerminalList() {
        List<Terminal> terminals = controller.getTerminals();
        terminalListContainer.getChildren().clear();

        int terminalCount = Math.min(terminals.size(), 10);
        for (int i = 0; i < terminalCount; i++) {
            HBox row = createTerminalRow(terminals.get(i), i);
            terminalListContainer.getChildren().add(row);
        }

        for (int i = terminalCount; i < 10; i++) {
            HBox emptyRow = createEmptyRow(i);
            terminalListContainer.getChildren().add(emptyRow);
        }
    }

    private void showPasswordDialog(Terminal terminal) {
        passwordDialog = createPasswordDialog(terminal);
        getChildren().add(passwordDialog);

        // 다이얼로그가 렌더링된 후 포커스 설정
        javafx.application.Platform.runLater(() -> {
            if (currentPasswordField != null) {
                currentPasswordField.requestFocus();
            }
        });
    }

    private void hidePasswordDialog() {
        if (passwordDialog != null) {
            getChildren().remove(passwordDialog);
            passwordDialog = null;
        }
    }

    private void showLoginProgress() {
        loginProgressBox = createLoginProgressBox();
        getChildren().add(loginProgressBox);
    }

    private void hideLoginProgress() {
        if (loginProgressBox != null) {
            getChildren().remove(loginProgressBox);
            loginProgressBox = null;
        }
    }

    private StackPane createPasswordDialog(Terminal terminal) {
        StackPane dialog = new StackPane();
        dialog.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

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

        currentPasswordField = new javafx.scene.control.PasswordField();
        currentPasswordField.setPromptText("비밀번호");
        currentPasswordField.setPrefWidth(290);
        currentPasswordField.setStyle(
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

        javafx.scene.control.TextField textField = new javafx.scene.control.TextField();
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
        currentPasswordField.textProperty().bindBidirectional(textField.textProperty());

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
            currentPasswordField.setVisible(false);
            currentPasswordField.setManaged(false);
            textField.setVisible(true);
            textField.setManaged(true);
        });

        toggleButton.setOnMouseReleased(event -> {
            textField.setVisible(false);
            textField.setManaged(false);
            currentPasswordField.setVisible(true);
            currentPasswordField.setManaged(true);
        });

        passwordContainer.getChildren().addAll(currentPasswordField, textField, toggleButton);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("확인");
        Button cancelButton = new Button("취소");

        styleDialogButton(confirmButton, true);
        styleDialogButton(cancelButton, false);

        confirmButton.setOnAction(event -> {
            String password = currentPasswordField.getText();
            if (!password.isEmpty()) {
                hidePasswordDialog();
                handleLogin(terminal, password);
            }
        });

        cancelButton.setOnAction(event -> hidePasswordDialog());

        currentPasswordField.setOnAction(event -> confirmButton.fire());
        textField.setOnAction(event -> confirmButton.fire());

        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        card.getChildren().addAll(titleLabel, passwordContainer, buttonBox);
        dialog.getChildren().add(card);

        return dialog;
    }

    private StackPane createLoginProgressBox() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(180, 120);
        box.setMaxSize(180, 120);
        box.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );

        Label label = new Label("로그인 중...");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        box.getChildren().add(label);
        overlay.getChildren().add(box);

        return overlay;
    }

    private void handleLogin(Terminal terminal, String password) {
        showLoginProgress();

        controller.loginTerminal(
                terminal.getSn(),
                password,
                () -> {
                    hideLoginProgress();
                    renderTerminalList();
                },
                error -> {
                    hideLoginProgress();
                    showLoginErrorDialog(error.getMessage());
                    System.err.println("로그인 실패: " + error.getMessage());
                }
        );
    }

    private void showLoginErrorDialog(String errorMessage) {
        StackPane errorDialog = createLoginErrorDialog(errorMessage);
        getChildren().add(errorDialog);

        // 2초 후 자동으로 닫기
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> {
                    getChildren().remove(errorDialog);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private StackPane createLoginErrorDialog(String errorMessage) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(250, 150);
        box.setMaxSize(250, 150);
        box.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );
        box.setPadding(new Insets(20));

        Label titleLabel = new Label("로그인 실패");
        titleLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label messageLabel = new Label(errorMessage != null ? errorMessage : "알 수 없는 오류");
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(210);
        messageLabel.setAlignment(Pos.CENTER);

        box.getChildren().addAll(titleLabel, messageLabel);
        overlay.getChildren().add(box);

        return overlay;
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

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #323232; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(40);
        header.setPrefHeight(40);
        header.setMaxHeight(40);

        // 상태 - 고정 60px
        Label statusLabel = new Label("");
        statusLabel.setMinWidth(60);
        statusLabel.setPrefWidth(60);
        statusLabel.setMaxWidth(60);
        statusLabel.setAlignment(Pos.CENTER);

        // 터미널 이름 - 늘어남
        Label nameLabel = new Label("터미널 이름");
        nameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-font-family: 'System';");
        nameLabel.setMinWidth(300);
        nameLabel.setPrefWidth(300);
        nameLabel.setMaxWidth(300);

        // 해상도 - 고정 150px (20px 패딩 포함)
        Label resolutionLabel = new Label("해상도");
        resolutionLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px; -fx-font-family: 'System'; -fx-padding: 0 0 0 20;");
        resolutionLabel.setMinWidth(250);
        resolutionLabel.setPrefWidth(250);
        resolutionLabel.setMaxWidth(250);

        // 액션 - 고정 100px
        Label actionLabel = new Label("");
        actionLabel.setMinWidth(100);
        actionLabel.setPrefWidth(100);
        actionLabel.setMaxWidth(100);

        header.getChildren().addAll(statusLabel, nameLabel, resolutionLabel, actionLabel);
        return header;
    }

    private HBox createTerminalRow(Terminal terminal, int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setFillHeight(true);
        VBox.setVgrow(row, Priority.ALWAYS);
        row.setMinHeight(0);
        row.setPrefHeight(0);
        row.setMaxHeight(Double.MAX_VALUE);

        if (index % 2 == 0) {
            row.setStyle("-fx-background-color: #3a3a3a;");
        } else {
            row.setStyle("-fx-background-color: #323232;");
        }

        // 상태 표시등 - 60px
        Circle statusCircle = new Circle(8);
        statusCircle.setFill(terminal.isLogined() ? Color.LIMEGREEN : Color.GRAY);
        HBox statusBox = new HBox(statusCircle);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setMinWidth(60);
        statusBox.setPrefWidth(60);
        statusBox.setMaxWidth(60);
        statusBox.setMinHeight(0);
        statusBox.setMaxHeight(Double.MAX_VALUE);

        // 터미널 이름 - 늘어남
        Label nameLabel = new Label(terminal.getAliasName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'System';");
        nameLabel.setMinWidth(300);
        nameLabel.setPrefWidth(300);
        nameLabel.setMaxWidth(300);
        nameLabel.setMinHeight(0);
        nameLabel.setMaxHeight(Double.MAX_VALUE);


        // 해상도 - 150px (20px 패딩 포함)
        Label resolutionLabel = new Label(terminal.getWidth() + " x " + terminal.getHeight());
        resolutionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-family: 'System'; -fx-padding: 0 0 0 20;");
        resolutionLabel.setMinWidth(250);
        resolutionLabel.setPrefWidth(250);
        resolutionLabel.setMaxWidth(250);
        resolutionLabel.setMinHeight(0);
        resolutionLabel.setMaxHeight(Double.MAX_VALUE);

        // 로그인 버튼 - 100px
        HBox actionBox = new HBox();
        actionBox.setMinWidth(100);
        actionBox.setPrefWidth(100);
        actionBox.setMaxWidth(100);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setMinHeight(0);
        actionBox.setMaxHeight(Double.MAX_VALUE);

        if (!terminal.isLogined()) {
            Button loginButton = new Button("로그인");
            styleLoginButton(loginButton);
            loginButton.setOnAction(event -> showPasswordDialog(terminal));
            actionBox.getChildren().add(loginButton);
        }

        row.getChildren().addAll(statusBox, nameLabel, resolutionLabel, actionBox);
        return row;
    }

    private HBox createEmptyRow(int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setFillHeight(true);
        VBox.setVgrow(row, Priority.ALWAYS);
        row.setMinHeight(0);
        row.setPrefHeight(0);
        row.setMaxHeight(Double.MAX_VALUE);

        if (index % 2 == 0) {
            row.setStyle("-fx-background-color: #3a3a3a;");
        } else {
            row.setStyle("-fx-background-color: #323232;");
        }

        return row;
    }

    private VBox createLoadingBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(180, 120);
        box.setMaxSize(180, 120);
        box.setStyle(
                "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );

        Label label = new Label("터미널 검색 중...");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        box.getChildren().add(label);
        return box;
    }

    private void searchTerminals() {
        loadingBox.setVisible(true);
        contentBox.setVisible(false);

        controller.searchTerminals(
                terminals -> {
                    terminalListContainer.getChildren().clear();

                    // 실제 터미널 표시 (최대 10개까지만)
                    int terminalCount = Math.min(terminals.size(), 10);
                    for (int i = 0; i < terminalCount; i++) {
                        HBox row = createTerminalRow(terminals.get(i), i);
                        terminalListContainer.getChildren().add(row);
                    }

                    // 빈 줄 채우기 (10개 고정)
                    for (int i = terminalCount; i < 10; i++) {
                        HBox emptyRow = createEmptyRow(i);
                        terminalListContainer.getChildren().add(emptyRow);
                    }

                    loadingBox.setVisible(false);
                    contentBox.setVisible(true);

                    // 자동 로그인 시작
                    autoLogin();
                },
                error -> {
                    System.err.println("검색 실패: " + error.getMessage());
                    loadingBox.setVisible(false);
                    contentBox.setVisible(true);
                }
        );
    }

    private void autoLogin() {
        List<Terminal> terminalsToLogin = controller.getTerminals().stream()
                .filter(Terminal::hasPassword)
                .filter(terminal -> !terminal.isLogined())
                .toList();

        if (terminalsToLogin.isEmpty()) {
            return;
        }

        // 백그라운드 쓰레드에서 동기 방식으로 순차 로그인
        new Thread(() -> {
            for (Terminal terminal : terminalsToLogin) {
                try {
                    controller.loginTerminalSync(terminal.getSn(), terminal.getPassword());
                } catch (Exception e) {
                    // 실패는 무시하고 다음 터미널로
                }
            }
            // 모든 자동 로그인 완료 후 화면 리로드
            javafx.application.Platform.runLater(this::renderTerminalList);
        }).start();
    }

    private void styleSearchButton(Button button) {
        button.setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-padding: 10 20;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #323232;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-padding: 10 20;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-padding: 10 20;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
    }

    private void styleLoginButton(Button button) {
        button.setPrefWidth(80);
        button.setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 3;" +
                        "-fx-background-radius: 3;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #323232;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 3;" +
                        "-fx-background-radius: 3;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-family: 'System';" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 3;" +
                        "-fx-background-radius: 3;" +
                        "-fx-cursor: hand;"
        ));
    }
}
