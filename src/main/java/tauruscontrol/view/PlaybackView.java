package tauruscontrol.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import tauruscontrol.controller.PlaybackController;
import tauruscontrol.domain.media.Media;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;
import tauruscontrol.view.components.ConfirmDialog;
import tauruscontrol.view.components.ErrorDialog;
import tauruscontrol.view.components.ProgressDialog;
import tauruscontrol.view.components.ProgressWithPercentDialog;
import tauruscontrol.view.components.SuccessDialog;

import java.io.File;
import java.util.List;

public class PlaybackView extends StackPane {

    private final PlaybackController controller;
    private final VBox terminalListContainer;
    private final VBox mediaListContainer;
    private ScrollPane mediaScrollPane;
    private Terminal selectedTerminal;
    private int selectedMediaIndex = -1;
    private StackPane currentDialog;
    private File lastUsedDirectory;

    public PlaybackView(TerminalManager terminalManager) {
        this.controller = new PlaybackController(terminalManager);
        this.terminalListContainer = new VBox();
        this.mediaListContainer = new VBox();

        getStylesheets().add(getClass().getResource("/styles/playback-view.css").toExternalForm());

        HBox mainLayout = new HBox(30);
        mainLayout.setPadding(new Insets(20, 40, 20, 40));
        mainLayout.setAlignment(Pos.CENTER);

        VBox leftPanel = createTerminalPanel();
        VBox rightPanel = createMediaPanel();

        mainLayout.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        getChildren().add(mainLayout);

        renderTerminalList();
        renderMediaList();

        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DELETE) {
                handleRemoveMedia();
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                navigateMediaUp();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                navigateMediaDown();
                event.consume();
            }
        });

        setFocusTraversable(true);
        requestFocus();
    }

    private void navigateMediaUp() {
        if (selectedMediaIndex > 0) {
            selectedMediaIndex--;
            renderMediaList();
        }
    }

    private void navigateMediaDown() {
        int mediaCount = controller.getMediaManager().getMedias().size();
        if (selectedMediaIndex < mediaCount - 1) {
            selectedMediaIndex++;
            renderMediaList();
        }
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
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

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
        nameLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px; -fx-font-weight: bold;");
        nameLabel.setPrefWidth(200);

        Label resolutionLabel = new Label("해상도");
        resolutionLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px; -fx-font-weight: bold;");
        resolutionLabel.setPrefWidth(150);

        header.getChildren().addAll(nameLabel, resolutionLabel);
        return header;
    }

    private VBox createMediaPanel() {
        VBox panel = new VBox(5);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPrefWidth(500);
        panel.setMaxWidth(500);

        VBox mediaSection = createMediaSection();
        HBox sendButtonContainer = createSendButton();

        panel.getChildren().addAll(mediaSection, sendButtonContainer);
        VBox.setVgrow(mediaSection, Priority.ALWAYS);

        return panel;
    }

    private VBox createMediaSection() {
        VBox section = new VBox(5);
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setMinHeight(35);
        titleRow.setMaxHeight(35);

        Label title = new Label("미디어 목록");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        titleRow.getChildren().add(title);

        HBox header = createMediaHeader();

        mediaListContainer.setStyle("-fx-background-color: #3e3e3e; -fx-spacing: 0;");
        mediaListContainer.setFillWidth(true);

        mediaScrollPane = new ScrollPane(mediaListContainer);
        mediaScrollPane.setFitToWidth(true);
        mediaScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mediaScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mediaScrollPane.setStyle("-fx-background-color: #3e3e3e; -fx-background: #3e3e3e; -fx-padding: 0;");
        mediaScrollPane.setPrefHeight(400);
        mediaScrollPane.setMaxHeight(400);

        StackPane scrollWrapper = new StackPane(mediaScrollPane);
        scrollWrapper.setStyle("-fx-border-color: #6a6a6a; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #3e3e3e; -fx-background-radius: 5;");
        scrollWrapper.setMinHeight(402);
        scrollWrapper.setMaxHeight(402);

        section.getChildren().addAll(titleRow, header, scrollWrapper);
        return section;
    }

    private HBox createMediaButtons() {
        HBox buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button addButton = new Button("+");
        Button removeButton = new Button("×");
        Button upButton = new Button("▲");
        Button downButton = new Button("▼");

        addButton.getStyleClass().add("media-button");
        removeButton.getStyleClass().add("media-button");
        upButton.getStyleClass().add("media-button");
        downButton.getStyleClass().add("media-button");

        addButton.setOnAction(event -> handleAddMedia());
        removeButton.setOnAction(event -> handleRemoveMedia());
        upButton.setOnAction(event -> handleMoveMediaUp());
        downButton.setOnAction(event -> handleMoveMediaDown());

        buttonBox.getChildren().addAll(addButton, removeButton, upButton, downButton);
        return buttonBox;
    }

    private HBox createMediaHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #323232; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(40);
        header.setPrefHeight(40);

        Label fileLabel = new Label("파일명");
        fileLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px; -fx-font-weight: bold;");
        fileLabel.setPrefWidth(300);

        HBox buttonBox = createMediaButtons();

        header.getChildren().addAll(fileLabel, buttonBox);
        return header;
    }

    private HBox createSendButton() {
        Button button = new Button("프로그램 전송");
        button.getStyleClass().add("send-button");
        button.setPrefWidth(130);
        button.setMinHeight(40);
        button.setMaxHeight(40);
        button.setOnAction(event -> handleSendProgram());

        HBox container = new HBox(button);
        container.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(container, new Insets(5, 0, 0, 0));

        return container;
    }

    private void renderTerminalList() {
        terminalListContainer.getChildren().clear();

        List<Terminal> terminals = controller.getLoggedInTerminals();

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
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        nameLabel.setPrefWidth(200);

        Label resolutionLabel = new Label(terminal.getWidth() + " x " + terminal.getHeight());
        resolutionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
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

    private void renderMediaList() {
        mediaListContainer.getChildren().clear();

        List<Media> medias = controller.getMediaManager().getMedias();

        int mediaCount = medias.size();
        for (int i = 0; i < mediaCount; i++) {
            Media media = medias.get(i);
            HBox row = createMediaRow(media, i);
            mediaListContainer.getChildren().add(row);
        }

        int minRows = 10;
        if (mediaCount < minRows) {
            for (int i = mediaCount; i < minRows; i++) {
                HBox emptyRow = createEmptyMediaRow(i);
                mediaListContainer.getChildren().add(emptyRow);
            }
        }

        scrollToSelectedMedia(mediaCount);
    }

    private void scrollToSelectedMedia(int mediaCount) {
        if (selectedMediaIndex < 0 || mediaCount <= 10) {
            return;
        }

        javafx.application.Platform.runLater(() -> {
            double visibleRows = 10.0;
            double currentScroll = mediaScrollPane.getVvalue();
            double maxScroll = mediaCount - visibleRows;

            double currentTopRow = currentScroll * maxScroll;
            double currentBottomRow = currentTopRow + visibleRows - 1;

            if (selectedMediaIndex < currentTopRow) {
                double newScroll = selectedMediaIndex / maxScroll;
                mediaScrollPane.setVvalue(Math.max(0.0, newScroll));
            } else if (selectedMediaIndex > currentBottomRow) {
                double newScroll = (selectedMediaIndex - visibleRows + 1) / maxScroll;
                mediaScrollPane.setVvalue(Math.min(1.0, newScroll));
            }
        });
    }

    private HBox createMediaRow(Media media, int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setMinHeight(40);
        row.setPrefHeight(40);

        String bgColor = "#3a3a3a";
        if (index % 2 == 1) {
            bgColor = "#323232";
        }
        if (index == selectedMediaIndex) {
            bgColor = "#1E88E5";
        }

        row.setStyle("-fx-background-color: " + bgColor + "; -fx-cursor: hand;");

        row.setOnMouseClicked(event -> {
            selectedMediaIndex = index;
            renderMediaList();
        });

        Label fileLabel = new Label(media.getFileName() + media.getExtension());
        fileLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        fileLabel.setPrefWidth(300);

        row.getChildren().add(fileLabel);
        return row;
    }

    private HBox createEmptyMediaRow(int index) {
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

    private void handleAddMedia() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("미디어 파일 선택");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("미디어 파일", "*.mp4", "*.avi", "*.jpg", "*.png", "*.gif"),
                new FileChooser.ExtensionFilter("비디오", "*.mp4", "*.avi"),
                new FileChooser.ExtensionFilter("이미지", "*.jpg", "*.png", "*.gif")
        );

        if (lastUsedDirectory != null && lastUsedDirectory.exists()) {
            fileChooser.setInitialDirectory(lastUsedDirectory);
        } else {
            String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
            File downloadsDir = new File(downloadsPath);
            if (downloadsDir.exists()) {
                fileChooser.setInitialDirectory(downloadsDir);
            }
        }

        List<File> files = fileChooser.showOpenMultipleDialog(getScene().getWindow());
        if (files == null || files.isEmpty()) {
            return;
        }

        lastUsedDirectory = files.get(0).getParentFile();

        showAddMediaProgress();

        addMediaRecursive(files, 0);
    }

    private void addMediaRecursive(List<File> files, int index) {
        if (index >= files.size()) {
            hideDialog();
            renderMediaList();
            return;
        }

        File file = files.get(index);
        controller.addMedia(
                file.getAbsolutePath(),
                () -> {
                    selectedMediaIndex = controller.getMediaManager().getMedias().size() - 1;
                    addMediaRecursive(files, index + 1);
                },
                error -> {
                    hideDialog();
                    showErrorDialog("미디어 추가 실패", error.getMessage());
                }
        );
    }

    private void handleRemoveMedia() {
        if (selectedMediaIndex < 0) {
            return;
        }

        controller.getMediaManager().removeMedia(selectedMediaIndex);

        if (selectedMediaIndex >= controller.getMediaManager().getMedias().size()) {
            selectedMediaIndex = controller.getMediaManager().getMedias().size() - 1;
        }

        renderMediaList();
    }

    private void handleMoveMediaUp() {
        if (selectedMediaIndex < 0) {
            return;
        }

        controller.getMediaManager().moveMediaUp(selectedMediaIndex);

        if (selectedMediaIndex > 0) {
            selectedMediaIndex--;
        }

        renderMediaList();
    }

    private void handleMoveMediaDown() {
        if (selectedMediaIndex < 0) {
            return;
        }

        controller.getMediaManager().moveMediaDown(selectedMediaIndex);

        if (selectedMediaIndex < controller.getMediaManager().getMedias().size() - 1) {
            selectedMediaIndex++;
        }

        renderMediaList();
    }

    private void handleSendProgram() {
        if (selectedTerminal == null) {
            showErrorDialog("전송 실패", "터미널을 선택해주세요.");
            return;
        }

        if (controller.getMediaManager().getMedias().isEmpty()) {
            showErrorDialog("전송 실패", "미디어를 추가해주세요.");
            return;
        }

        String message = selectedTerminal.getAliasName() + "으로 프로그램을 전송합니다.";
        currentDialog = new ConfirmDialog(
                message,
                this::executeSendProgram,
                this::hideDialog
        );
        getChildren().add(currentDialog);
    }

    private void executeSendProgram() {
        hideDialog();
        showProgramProgress();

        controller.sendProgram(
                selectedTerminal,
                this::updateProgramProgress,
                () -> {
                    hideDialog();
                    showSuccessDialog();
                },
                error -> {
                    hideDialog();
                    showErrorDialog("프로그램 전송 실패", error.getMessage());
                }
        );
    }

    private void showAddMediaProgress() {
        currentDialog = new ProgressDialog("미디어 추가 중...");
        getChildren().add(currentDialog);
    }

    private void showProgramProgress() {
        currentDialog = new ProgressWithPercentDialog("프로그램 전송 중...");
        getChildren().add(currentDialog);
    }

    private void updateProgramProgress(int percent) {
        if (currentDialog instanceof ProgressWithPercentDialog dialog) {
            dialog.updateProgress(percent);
        }
    }

    private void showSuccessDialog() {
        SuccessDialog dialog = new SuccessDialog("전송 완료", "프로그램이 성공적으로 전송되었습니다.");
        getChildren().add(dialog);
        dialog.showTemporary(2000);
    }

    private void showErrorDialog(String title, String message) {
        ErrorDialog dialog = new ErrorDialog(title, message);
        getChildren().add(dialog);
        dialog.showTemporary(2000);
    }

    private void hideDialog() {
        if (currentDialog != null) {
            getChildren().remove(currentDialog);
            currentDialog = null;
        }
    }
}