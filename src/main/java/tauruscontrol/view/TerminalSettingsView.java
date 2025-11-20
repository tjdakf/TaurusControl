package tauruscontrol.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;
import tauruscontrol.view.components.BrightnessDialog;
import tauruscontrol.view.components.RebootScheduleDialog;
import tauruscontrol.view.components.TimeSyncDialog;

import java.util.List;

public class TerminalSettingsView extends StackPane {

    private final TerminalManager terminalManager;
    private final VBox terminalListContainer;
    private Terminal selectedTerminal;
    private StackPane currentDialog;

    public TerminalSettingsView(TerminalManager terminalManager) {
        this.terminalManager = terminalManager;
        this.terminalListContainer = new VBox();

        getStylesheets().add(getClass().getResource("/styles/common-styles.css").toExternalForm());

        HBox mainLayout = new HBox(30);
        mainLayout.setPadding(new Insets(20, 40, 20, 40));
        mainLayout.setAlignment(Pos.CENTER);

        VBox leftPanel = createTerminalPanel();
        VBox rightPanel = createIconPanel();

        mainLayout.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        getChildren().add(mainLayout);

        renderTerminalList();
    }

    public void refresh() {
        renderTerminalList();
        requestFocus();
    }

    private VBox createTerminalPanel() {
        VBox panel = new VBox(5);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPrefWidth(500);
        panel.setMaxWidth(500);

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setMinHeight(35);
        titleRow.setMaxHeight(35);

        Label title = new Label("터미널 목록");
        title.getStyleClass().add("title-label");

        titleRow.getChildren().add(title);

        HBox header = createTerminalHeader();

        StackPane containerWrapper = new StackPane(terminalListContainer);
        containerWrapper.setStyle("-fx-border-color: #6a6a6a; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #3e3e3e; -fx-background-radius: 5;");
        containerWrapper.setMinHeight(402);
        containerWrapper.setMaxHeight(402);

        terminalListContainer.setStyle("-fx-background-color: #3e3e3e; -fx-spacing: 0;");
        terminalListContainer.setFillWidth(true);

        panel.getChildren().addAll(titleRow, header, containerWrapper);
        return panel;
    }

    private HBox createTerminalHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #323232; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(40);
        header.setPrefHeight(40);

        Label nameLabel = new Label("터미널 이름");
        nameLabel.getStyleClass().add("header-label");
        nameLabel.setPrefWidth(200);

        Label resolutionLabel = new Label("해상도");
        resolutionLabel.getStyleClass().add("header-label");
        resolutionLabel.setPrefWidth(150);

        header.getChildren().addAll(nameLabel, resolutionLabel);
        return header;
    }

    private VBox createIconPanel() {
        VBox panel = new VBox(30);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(70, 0, 0, 0));
        panel.setPrefWidth(500);
        panel.setMaxWidth(500);

        // 첫 번째 줄: 시간 동기화, 재부팅 설정
        HBox firstRow = new HBox(100);
        firstRow.setAlignment(Pos.CENTER);

        VBox timeSyncIcon = createIconButton(
                "/icons/time-sync.png",
                "시간 동기화",
                100,
                this::handleTimeSync
        );

        VBox rebootIcon = createIconButton(
                "/icons/reboot.png",
                "재부팅 설정",
                80,
                this::handleRebootSchedule
        );

        firstRow.getChildren().addAll(timeSyncIcon, rebootIcon);

        // 두 번째 줄: LED밝기 설정
        HBox secondRow = new HBox();
        secondRow.setAlignment(Pos.CENTER_LEFT);
        secondRow.setPadding(new Insets(0, 0, 0, 62));

        VBox brightnessIcon = createIconButton(
                "/icons/brightness.png",
                "LED밝기 설정",
                90,
                this::handleBrightness
        );

        secondRow.getChildren().add(brightnessIcon);

        panel.getChildren().addAll(firstRow, secondRow);

        return panel;
    }

    private VBox createIconButton(String iconPath, String labelText, int iconSize, Runnable action) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-cursor: hand;");

        try {
            Image image = new Image(getClass().getResourceAsStream(iconPath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(iconSize);
            imageView.setFitHeight(iconSize);
            imageView.setPreserveRatio(true);

            StackPane iconWrapper = new StackPane(imageView);
            iconWrapper.setMinSize(iconSize, iconSize);
            iconWrapper.setMaxSize(iconSize, iconSize);
            iconWrapper.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-radius: 50;"
            );

            Label label = new Label(labelText);
            label.getStyleClass().add("title-label");

            container.getChildren().addAll(iconWrapper, label);

            container.setOnMouseEntered(e -> {
                iconWrapper.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                                "-fx-background-radius: 50;"
                );
            });

            container.setOnMouseExited(e -> {
                iconWrapper.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-background-radius: 50;"
                );
            });

            container.setOnMouseClicked(e -> action.run());

        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconPath);
            Label errorLabel = new Label("?");
            errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 40px;");
            container.getChildren().add(errorLabel);
        }

        return container;
    }

    private void renderTerminalList() {
        terminalListContainer.getChildren().clear();

        List<Terminal> terminals = terminalManager.getLoggedInTerminals();

        if (terminals.isEmpty()) {
            selectedTerminal = null;
            HBox messageRow = createNoTerminalMessageRow();
            terminalListContainer.getChildren().add(messageRow);

            for (int i = 1; i < 10; i++) {
                HBox emptyRow = createEmptyTerminalRow(i);
                terminalListContainer.getChildren().add(emptyRow);
            }
            return;
        }

        boolean isSelectedTerminalInList = terminals.contains(selectedTerminal);
        if (!isSelectedTerminalInList) {
            selectedTerminal = terminals.get(0);
        }

        int terminalCount = Math.min(terminals.size(), 10);
        for (int i = 0; i < terminalCount; i++) {
            Terminal terminal = terminals.get(i);
            HBox row = createTerminalRow(terminal, i);
            terminalListContainer.getChildren().add(row);
        }

        for (int i = terminalCount; i < 10; i++) {
            HBox emptyRow = createEmptyTerminalRow(i);
            terminalListContainer.getChildren().add(emptyRow);
        }
    }

    private HBox createTerminalRow(Terminal terminal, int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setMinHeight(40);
        row.setPrefHeight(40);

        String bgColor = "#3a3a3a";
        if (index % 2 == 1) {
            bgColor = "#323232";
        }
        if (terminal.equals(selectedTerminal)) {
            bgColor = "#1E88E5";
        }

        row.setStyle("-fx-background-color: " + bgColor + "; -fx-cursor: hand;");

        row.setOnMouseClicked(event -> {
            selectedTerminal = terminal;
            renderTerminalList();
        });

        Label nameLabel = new Label(terminal.getAliasName());
        nameLabel.getStyleClass().add("content-label");
        nameLabel.setPrefWidth(200);

        Label resolutionLabel = new Label(terminal.getWidth() + " x " + terminal.getHeight());
        resolutionLabel.getStyleClass().add("content-label");
        resolutionLabel.setPrefWidth(150);

        row.getChildren().addAll(nameLabel, resolutionLabel);
        return row;
    }

    private HBox createNoTerminalMessageRow() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 10, 10, 20));
        row.setMinHeight(40);
        row.setPrefHeight(40);
        row.setStyle("-fx-background-color: #3a3a3a;");

        Label messageLabel = new Label("로그인 된 터미널이 없습니다.");
        messageLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");

        row.getChildren().add(messageLabel);
        return row;
    }

    private HBox createEmptyTerminalRow(int index) {
        HBox row = new HBox();
        row.setMinHeight(40);
        row.setPrefHeight(40);

        String bgColor = "#3a3a3a";
        if (index % 2 == 1) {
            bgColor = "#323232";
        }
        row.setStyle("-fx-background-color: " + bgColor + ";");

        return row;
    }

    private void handleTimeSync() {
        if (selectedTerminal == null) {
            System.out.println("시간 동기화: 터미널을 선택해주세요.");
            return;
        }
        TimeSyncDialog dialog = new TimeSyncDialog(selectedTerminal, () -> {
            if (currentDialog instanceof TimeSyncDialog) {
                ((TimeSyncDialog) currentDialog).cleanup();
            }
            closeDialog();
        });
        showDialog(dialog);
    }

    private void handleRebootSchedule() {
        if (selectedTerminal == null) {
            System.out.println("재부팅 설정: 터미널을 선택해주세요.");
            return;
        }
        RebootScheduleDialog dialog = new RebootScheduleDialog(selectedTerminal, this::closeDialog);
        showDialog(dialog);
    }

    private void handleBrightness() {
        if (selectedTerminal == null) {
            System.out.println("LED밝기 설정: 터미널을 선택해주세요.");
            return;
        }
        BrightnessDialog dialog = new BrightnessDialog(selectedTerminal, this::closeDialog);
        showDialog(dialog);
    }

    private void showDialog(StackPane dialog) {
        closeDialog();
        currentDialog = dialog;
        getChildren().add(dialog);
    }

    private void closeDialog() {
        if (currentDialog != null) {
            getChildren().remove(currentDialog);
            currentDialog = null;
        }
    }
}
