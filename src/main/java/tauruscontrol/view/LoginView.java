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
import tauruscontrol.util.UIConstants;
import tauruscontrol.view.components.ErrorDialog;
import tauruscontrol.view.components.PasswordDialog;
import tauruscontrol.view.components.ProgressDialog;

import java.util.List;

public class LoginView extends StackPane {

    private final LoginController controller;
    private final VBox terminalListContainer;
    private final VBox loadingBox;
    private final VBox contentBox;
    private StackPane currentDialog;

    public LoginView(LoginController controller) {
        this.controller = controller;

        // CSS 로드
        getStylesheets().add(getClass().getResource("/styles/common-styles.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/styles/login-view.css").toExternalForm());

        // 로딩 박스
        loadingBox = createLoadingBox();

        // 메인 컨텐츠
        contentBox = new VBox();
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setFillWidth(true);

        // 검색 버튼
        Button searchButton = new Button("터미널 검색");
        searchButton.getStyleClass().add("search-button");
        searchButton.setOnAction(event -> searchTerminals());
        searchButton.setFocusTraversable(false);
        VBox.setMargin(searchButton, new Insets(UIConstants.PANEL_PADDING_SMALL, 0,
                UIConstants.PANEL_GAP, 50));

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
        currentDialog = new PasswordDialog(
                terminal,
                this::handleLogin,
                this::hideDialog
        );
        getChildren().add(currentDialog);
    }

    private void hideDialog() {
        if (currentDialog != null) {
            getChildren().remove(currentDialog);
            currentDialog = null;
        }
    }

    private void showLoginProgress() {
        currentDialog = new ProgressDialog("로그인 중...");
        getChildren().add(currentDialog);
    }

    private void handleLogin(Terminal terminal, String password) {
        hideDialog();
        showLoginProgress();

        controller.loginTerminal(
                terminal.getSn(),
                password,
                () -> {
                    hideDialog();
                    renderTerminalList();
                },
                error -> {
                    hideDialog();
                    showLoginErrorDialog(error.getMessage());
                    System.err.println("로그인 실패: " + error.getMessage());
                }
        );
    }

    private void showLoginErrorDialog(String errorMessage) {
        ErrorDialog errorDialog = new ErrorDialog("로그인 실패", errorMessage);
        getChildren().add(errorDialog);
        errorDialog.showTemporary(UIConstants.DIALOG_TIMEOUT_SHORT);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #323232; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(UIConstants.ROW_HEIGHT);
        header.setPrefHeight(UIConstants.ROW_HEIGHT);
        header.setMaxHeight(UIConstants.ROW_HEIGHT);

        // 상태 - 고정 60px
        Label statusLabel = new Label("");
        statusLabel.setMinWidth(UIConstants.STATUS_WIDTH);
        statusLabel.setPrefWidth(UIConstants.STATUS_WIDTH);
        statusLabel.setMaxWidth(UIConstants.STATUS_WIDTH);
        statusLabel.setAlignment(Pos.CENTER);

        // 터미널 이름 - 늘어남
        Label nameLabel = new Label("터미널 이름");
        nameLabel.getStyleClass().add("header-label");
        nameLabel.setMinWidth(300);
        nameLabel.setPrefWidth(300);
        nameLabel.setMaxWidth(300);

        // 해상도 - 고정 150px (20px 패딩 포함)
        Label resolutionLabel = new Label("해상도");
        resolutionLabel.getStyleClass().add("header-label");
        resolutionLabel.setStyle("-fx-padding: 0 0 0 20;");
        resolutionLabel.setMinWidth(250);
        resolutionLabel.setPrefWidth(250);
        resolutionLabel.setMaxWidth(250);

        // 액션 - 고정 100px
        Label actionLabel = new Label("");
        actionLabel.setMinWidth(UIConstants.ACTION_WIDTH);
        actionLabel.setPrefWidth(UIConstants.ACTION_WIDTH);
        actionLabel.setMaxWidth(UIConstants.ACTION_WIDTH);

        header.getChildren().addAll(statusLabel, nameLabel, resolutionLabel, actionLabel);
        return header;
    }

    private HBox createTerminalRow(Terminal terminal, int index) {
        HBox row = createRowContainer(index);
        HBox statusBox = createStatusIndicator(terminal);
        Label nameLabel = createNameLabel(terminal);
        Label resolutionLabel = createResolutionLabel(terminal);
        HBox actionBox = createActionBox(terminal);

        row.getChildren().addAll(statusBox, nameLabel, resolutionLabel, actionBox);
        return row;
    }

    private HBox createRowContainer(int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(UIConstants.SPACING_MEDIUM));
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

    private HBox createStatusIndicator(Terminal terminal) {
        Circle statusCircle = new Circle(8);
        if (terminal.isLoginedByThisApp()) {
            statusCircle.setFill(Color.LIMEGREEN);
        } else if (terminal.isLoginedByOtherDevice()) {
            statusCircle.setFill(Color.YELLOW);
        } else {
            statusCircle.setFill(Color.GRAY);
        }

        HBox statusBox = new HBox(statusCircle);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setMinWidth(UIConstants.STATUS_WIDTH);
        statusBox.setPrefWidth(UIConstants.STATUS_WIDTH);
        statusBox.setMaxWidth(UIConstants.STATUS_WIDTH);
        statusBox.setMinHeight(0);
        statusBox.setMaxHeight(Double.MAX_VALUE);
        return statusBox;
    }

    private Label createNameLabel(Terminal terminal) {
        Label nameLabel = new Label(terminal.getAliasName());
        nameLabel.getStyleClass().add("content-label");
        nameLabel.setMinWidth(300);
        nameLabel.setPrefWidth(300);
        nameLabel.setMaxWidth(300);
        nameLabel.setMinHeight(0);
        nameLabel.setMaxHeight(Double.MAX_VALUE);
        return nameLabel;
    }

    private Label createResolutionLabel(Terminal terminal) {
        Label resolutionLabel = new Label(terminal.getWidth() + " x " + terminal.getHeight());
        resolutionLabel.getStyleClass().add("content-label");
        resolutionLabel.setStyle("-fx-padding: 0 0 0 20;");
        resolutionLabel.setMinWidth(250);
        resolutionLabel.setPrefWidth(250);
        resolutionLabel.setMaxWidth(250);
        resolutionLabel.setMinHeight(0);
        resolutionLabel.setMaxHeight(Double.MAX_VALUE);
        return resolutionLabel;
    }

    private HBox createActionBox(Terminal terminal) {
        HBox actionBox = new HBox();
        actionBox.setMinWidth(UIConstants.ACTION_WIDTH);
        actionBox.setPrefWidth(UIConstants.ACTION_WIDTH);
        actionBox.setMaxWidth(UIConstants.ACTION_WIDTH);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setMinHeight(0);
        actionBox.setMaxHeight(Double.MAX_VALUE);

        if (!terminal.isLoginedByThisApp()) {
            Button loginButton = createLoginButton(terminal);
            actionBox.getChildren().add(loginButton);
        }
        return actionBox;
    }

    private Button createLoginButton(Terminal terminal) {
        Button loginButton = new Button("로그인");
        loginButton.setPrefWidth(UIConstants.BUTTON_SMALL);
        loginButton.getStyleClass().add("login-button");
        loginButton.setFocusTraversable(false);
        loginButton.setOnAction(event -> {
            if (terminal.hasPassword()) {
                handleLogin(terminal, terminal.getPassword());
            } else {
                showPasswordDialog(terminal);
            }
        });
        return loginButton;
    }

    private HBox createEmptyRow(int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(UIConstants.SPACING_MEDIUM));
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

    private HBox createNoTerminalMessageRow() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(50));
        row.setFillHeight(true);
        VBox.setVgrow(row, Priority.ALWAYS);
        row.setMinHeight(0);
        row.setPrefHeight(0);
        row.setMaxHeight(Double.MAX_VALUE);
        row.setStyle("-fx-background-color: #3a3a3a;");

        Label messageLabel = new Label("검색된 터미널이 없습니다.");
        messageLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px; -fx-font-family: 'System'; -fx-padding: 0 0 0 20;");

        row.getChildren().add(messageLabel);
        return row;
    }

    private VBox createLoadingBox() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(UIConstants.DIALOG_SMALL_WIDTH, UIConstants.DIALOG_SMALL_HEIGHT);
        box.setMaxSize(UIConstants.DIALOG_SMALL_WIDTH, UIConstants.DIALOG_SMALL_HEIGHT);
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

                    // 터미널이 없는 경우 메시지 표시
                    if (terminals.isEmpty()) {
                        HBox messageRow = createNoTerminalMessageRow();
                        terminalListContainer.getChildren().add(messageRow);

                        // 나머지 빈 줄 채우기 (9개)
                        for (int i = 1; i < 10; i++) {
                            HBox emptyRow = createEmptyRow(i);
                            terminalListContainer.getChildren().add(emptyRow);
                        }
                    } else {
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

                        // 자동 로그인 시작
                        autoLogin();
                    }

                    loadingBox.setVisible(false);
                    contentBox.setVisible(true);
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
                .filter(terminal -> !terminal.isLoginedByThisApp())
                .filter(terminal -> !terminal.isLoginedByOtherDevice())  // 다른 장치에서 로그인된 터미널 제외
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
}
