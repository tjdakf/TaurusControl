package tauruscontrol.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import tauruscontrol.controller.ScheduleController;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;
import tauruscontrol.service.PowerManager;
import tauruscontrol.util.ScheduleHelper;
import tauruscontrol.view.components.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleView extends StackPane {

    private final TerminalManager terminalManager;
    private final Map<Terminal, PowerManager> powerManagers;
    private final Map<Terminal, ScheduleController> controllers;
    private final VBox terminalListContainer;
    private final VBox scheduleListContainer;
    private Terminal selectedTerminal;
    private int selectedScheduleIndex = -1;
    private int selectedPairIndex = -1;
    private List<PowerManager.ScheduleEntry> currentSchedules = new ArrayList<>();
    private StackPane currentDialog;
    private ScrollPane scheduleScrollPane;

    private RadioButton manualModeRadio;
    private RadioButton autoModeRadio;
    private Label currentStateLabel;

    public ScheduleView(TerminalManager terminalManager) {
        this.terminalManager = terminalManager;
        this.powerManagers = new HashMap<>();
        this.controllers = new HashMap<>();
        this.terminalListContainer = new VBox();
        this.scheduleListContainer = new VBox();

        getStylesheets().add(getClass().getResource("/styles/common-styles.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/styles/schedule-view.css").toExternalForm());

        HBox mainLayout = new HBox(30);
        mainLayout.setPadding(new Insets(20, 40, 20, 40));
        mainLayout.setAlignment(Pos.CENTER);

        VBox leftPanel = createTerminalPanel();
        VBox rightPanel = createSchedulePanel();

        mainLayout.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        getChildren().add(mainLayout);

        renderTerminalList();
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (currentDialog != null) {
                return;
            }

            if (event.getCode() == KeyCode.DELETE) {
                handleDeleteSchedule();
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                handleEditSchedule();
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                navigateScheduleUp();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                navigateScheduleDown();
                event.consume();
            }
        });

        setFocusTraversable(true);
        requestFocus();
    }

    private void navigateScheduleUp() {
        if (selectedScheduleIndex > 1) {
            selectedScheduleIndex -= 2;
            updatePairSelection();
            renderScheduleList();
        } else if (selectedScheduleIndex == 1) {
            selectedScheduleIndex = 0;
            updatePairSelection();
            renderScheduleList();
        }
    }

    private void navigateScheduleDown() {
        if (selectedScheduleIndex < currentSchedules.size() - 2) {
            selectedScheduleIndex += 2;
            updatePairSelection();
            renderScheduleList();
        }
    }

    private void updatePairSelection() {
        if (selectedScheduleIndex < 0 || selectedScheduleIndex >= currentSchedules.size()) {
            selectedPairIndex = -1;
            return;
        }

        PowerManager.ScheduleEntry selected = currentSchedules.get(selectedScheduleIndex);

        if (selected.getAction().equals("OPEN")) {
            if (selectedScheduleIndex + 1 < currentSchedules.size() &&
                currentSchedules.get(selectedScheduleIndex + 1).getAction().equals("CLOSE")) {
                selectedPairIndex = selectedScheduleIndex + 1;
            } else {
                selectedPairIndex = -1;
            }
        } else {
            if (selectedScheduleIndex - 1 >= 0 &&
                currentSchedules.get(selectedScheduleIndex - 1).getAction().equals("OPEN")) {
                selectedPairIndex = selectedScheduleIndex - 1;
            } else {
                selectedPairIndex = -1;
            }
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

    private VBox createSchedulePanel() {
        VBox panel = new VBox(5);
        panel.setAlignment(Pos.TOP_LEFT);
        panel.setPrefWidth(500);
        panel.setMaxWidth(500);

        VBox powerModeSection = createPowerModeSection();
        VBox currentStateSection = createCurrentStateSection();
        VBox scheduleSection = createScheduleSection();

        panel.getChildren().addAll(powerModeSection, currentStateSection, scheduleSection);
        return panel;
    }

    private VBox createPowerModeSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10, 0, 0, 0));

        Label label = new Label("전원 모드");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER_LEFT);

        ToggleGroup group = new ToggleGroup();

        manualModeRadio = new RadioButton("수동 모드");
        manualModeRadio.setToggleGroup(group);
        manualModeRadio.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        manualModeRadio.setFocusTraversable(false);
        manualModeRadio.setOnAction(e -> handlePowerModeChange("MANUALLY"));

        autoModeRadio = new RadioButton("자동 모드");
        autoModeRadio.setToggleGroup(group);
        autoModeRadio.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        autoModeRadio.setFocusTraversable(false);
        autoModeRadio.setOnAction(e -> handlePowerModeChange("AUTO"));

        radioBox.getChildren().addAll(manualModeRadio, autoModeRadio);
        section.getChildren().addAll(label, radioBox);

        return section;
    }

    private VBox createCurrentStateSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10, 0, 0, 0));

        HBox stateRow = new HBox(10);
        stateRow.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("현재 상태:");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        currentStateLabel = new Label("--");
        currentStateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        stateRow.getChildren().addAll(label, currentStateLabel);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button onButton = new Button("ON");
        onButton.setPrefWidth(80);
        onButton.setPrefHeight(35);
        onButton.setFocusTraversable(false);
        onButton.getStyleClass().add("power-button");
        onButton.setOnAction(e -> handleSetPowerState("OPEN"));

        Button offButton = new Button("OFF");
        offButton.setPrefWidth(80);
        offButton.setPrefHeight(35);
        offButton.setFocusTraversable(false);
        offButton.getStyleClass().add("power-button");
        offButton.setOnAction(e -> handleSetPowerState("CLOSE"));

        buttonBox.getChildren().addAll(onButton, offButton);
        section.getChildren().addAll(stateRow, buttonBox);

        return section;
    }

    private VBox createScheduleSection() {
        VBox section = new VBox(5);
        section.setPadding(new Insets(10, 0, 0, 0));
        VBox.setVgrow(section, Priority.ALWAYS);

        HBox headerBox = createScheduleHeader();

        scheduleListContainer.setStyle("-fx-background-color: #3e3e3e; -fx-spacing: 0;");
        scheduleListContainer.setFillWidth(true);

        scheduleScrollPane = new ScrollPane(scheduleListContainer);
        scheduleScrollPane.setFitToWidth(true);
        scheduleScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scheduleScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scheduleScrollPane.setStyle("-fx-background-color: #3e3e3e; -fx-background: #3e3e3e; -fx-padding: 0;");
        scheduleScrollPane.setPrefHeight(240);
        scheduleScrollPane.setMaxHeight(240);

        StackPane scrollWrapper = new StackPane(scheduleScrollPane);
        scrollWrapper.setStyle("-fx-border-color: #6a6a6a; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #3e3e3e; -fx-background-radius: 5;");
        scrollWrapper.setMinHeight(242);
        scrollWrapper.setMaxHeight(242);

        Button saveButton = new Button("저장하기");
        saveButton.setPrefWidth(130);
        saveButton.setMinHeight(40);
        saveButton.setMaxHeight(40);
        saveButton.setFocusTraversable(false);
        saveButton.getStyleClass().add("save-button");
        saveButton.setOnAction(e -> handleSaveSchedule());

        HBox saveButtonContainer = new HBox(saveButton);
        saveButtonContainer.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(saveButtonContainer, new Insets(5, 0, 0, 0));

        section.getChildren().addAll(headerBox, scrollWrapper, saveButtonContainer);
        return section;
    }

    private HBox createScheduleHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #323232; -fx-padding: 10;");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMinHeight(40);
        header.setPrefHeight(40);

        Label label = new Label("스케쥴");
        label.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px; -fx-font-weight: bold;");
        label.setPrefWidth(300);

        HBox buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button addButton = new Button("+");
        addButton.setFocusTraversable(false);
        addButton.getStyleClass().add("icon-button");
        addButton.setOnAction(e -> handleAddSchedule());

        Button removeButton = new Button("-");
        removeButton.setFocusTraversable(false);
        removeButton.getStyleClass().add("icon-button");
        removeButton.setOnAction(e -> handleDeleteSchedule());

        Button editButton = new Button("편집");
        editButton.setFocusTraversable(false);
        editButton.setPrefWidth(60);
        editButton.setPrefHeight(35);
        editButton.getStyleClass().add("secondary-button");
        editButton.setOnAction(e -> handleEditSchedule());

        buttonBox.getChildren().addAll(addButton, removeButton, editButton);
        header.getChildren().addAll(label, buttonBox);

        return header;
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

        if (selectedTerminal != null) {
            boolean isFirstLoad = !controllers.containsKey(selectedTerminal);
            if (isFirstLoad) {
                loadTerminalData(selectedTerminal);
            }
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
        row.setFocusTraversable(false);

        String bgColor = "#3a3a3a";
        if (index % 2 == 1) {
            bgColor = "#323232";
        }
        if (terminal.equals(selectedTerminal)) {
            bgColor = "#1E88E5";
        }

        row.setStyle("-fx-background-color: " + bgColor + "; -fx-cursor: hand;");

        row.setOnMouseClicked(event -> {
            boolean wasSelected = terminal.equals(selectedTerminal);
            selectedTerminal = terminal;
            selectedScheduleIndex = -1;

            if (!wasSelected) {
                renderTerminalList();
            }

            boolean isFirstLoad = !controllers.containsKey(terminal);
            if (isFirstLoad) {
                loadTerminalData(terminal);
            }
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

    private void renderScheduleList() {
        scheduleListContainer.getChildren().clear();

        int scheduleCount = currentSchedules.size();
        for (int i = 0; i < scheduleCount; i++) {
            PowerManager.ScheduleEntry entry = currentSchedules.get(i);
            HBox row = createScheduleRow(entry, i);
            scheduleListContainer.getChildren().add(row);
        }

        int minRows = 6;
        if (scheduleCount < minRows) {
            for (int i = scheduleCount; i < minRows; i++) {
                HBox emptyRow = createEmptyScheduleRow(i);
                scheduleListContainer.getChildren().add(emptyRow);
            }
        }

        if (selectedScheduleIndex < 0 || scheduleCount == 0) {
            return;
        }

        javafx.application.Platform.runLater(() -> {
            double visibleRows = 6.0;
            double currentScroll = scheduleScrollPane.getVvalue();
            double maxScroll = scheduleCount - visibleRows;

            if (maxScroll <= 0) {
                return;
            }

            int topIndex = selectedScheduleIndex;
            int bottomIndex = selectedPairIndex >= 0 ? Math.max(selectedScheduleIndex, selectedPairIndex) : selectedScheduleIndex;

            double currentTopRow = currentScroll * maxScroll;
            double currentBottomRow = currentTopRow + visibleRows - 1;

            if (topIndex < currentTopRow) {
                double newScroll = topIndex / maxScroll;
                scheduleScrollPane.setVvalue(Math.max(0.0, newScroll));
            } else if (bottomIndex > currentBottomRow) {
                double newScroll = (bottomIndex - visibleRows + 1) / maxScroll;
                scheduleScrollPane.setVvalue(Math.min(1.0, newScroll));
            }
        });
    }

    private HBox createScheduleRow(PowerManager.ScheduleEntry entry, int index) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setMinHeight(40);
        row.setPrefHeight(40);
        row.setFocusTraversable(false);

        String bgColor = "#3a3a3a";
        if (index % 2 == 1) {
            bgColor = "#323232";
        }
        if (index == selectedScheduleIndex || index == selectedPairIndex) {
            bgColor = "#1E88E5";
        }

        row.setStyle("-fx-background-color: " + bgColor + "; -fx-cursor: hand;");

        row.setOnMouseClicked(event -> {
            selectedScheduleIndex = index;

            if (index < currentSchedules.size()) {
                PowerManager.ScheduleEntry clicked = currentSchedules.get(index);

                if (clicked.getAction().equals("OPEN")) {
                    if (index + 1 < currentSchedules.size() && currentSchedules.get(index + 1).getAction().equals("CLOSE")) {
                        selectedPairIndex = index + 1;
                    } else {
                        selectedPairIndex = -1;
                    }
                } else {
                    if (index - 1 >= 0 && currentSchedules.get(index - 1).getAction().equals("OPEN")) {
                        selectedPairIndex = index - 1;
                    } else {
                        selectedPairIndex = -1;
                    }
                }
            }

            renderScheduleList();
        });

        Label scheduleLabel = new Label(ScheduleHelper.formatScheduleDisplay(entry));
        scheduleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        row.getChildren().add(scheduleLabel);
        return row;
    }

    private HBox createEmptyScheduleRow(int index) {
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

    private void loadTerminalData(Terminal terminal) {
        if (currentDialog != null) {
            return;
        }

        showDialog(new ProgressDialog("데이터 로드 중..."));

        PowerManager powerManager = powerManagers.computeIfAbsent(
                terminal, t -> new PowerManager(t));

        ScheduleController controller = controllers.computeIfAbsent(
                terminal, t -> new ScheduleController(powerManager));

        controller.loadInitialData(
                this::updatePowerMode,
                this::updatePowerState,
                (schedules) -> {
                    updateScheduleList(schedules);
                    closeDialog();
                },
                (error) -> {
                    closeDialog();
                    showError(error);
                    controllers.remove(terminal);
                    powerManagers.remove(terminal);
                }
        );
    }

    private void updatePowerMode(String mode) {
        if (mode == null || mode.isEmpty()) {
            manualModeRadio.setSelected(true);
        } else if (mode.equals("MANUALLY")) {
            manualModeRadio.setSelected(true);
        } else if (mode.equals("AUTO")) {
            autoModeRadio.setSelected(true);
        } else {
            manualModeRadio.setSelected(true);
        }
    }

    private void updatePowerState(String state) {
        if (state == null || state.isEmpty()) {
            currentStateLabel.setText("--");
            currentStateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else if (state.equals("OPEN")) {
            currentStateLabel.setText("ON");
            currentStateLabel.setStyle("-fx-text-fill: limegreen; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else if (state.equals("CLOSE")) {
            currentStateLabel.setText("OFF");
            currentStateLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            currentStateLabel.setText("--");
            currentStateLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        }
    }

    private void updateScheduleList(List<PowerManager.ScheduleEntry> schedules) {
        currentSchedules = ScheduleHelper.normalizeAndOptimize(schedules);
        renderScheduleList();
    }

    private void handlePowerModeChange(String mode) {
        if (selectedTerminal == null) {
            return;
        }

        ScheduleController controller = controllers.get(selectedTerminal);
        if (controller != null) {
            showDialog(new ProgressDialog("모드 변경 중..."));
            controller.setPowerMode(
                    mode,
                    this::closeDialog,
                    this::showError
            );
        }
    }

    private void handleSetPowerState(String state) {
        if (selectedTerminal == null) {
            return;
        }

        showDialog(new ProgressDialog("설정 중입니다..."));

        ScheduleController controller = controllers.get(selectedTerminal);
        if (controller != null) {
            controller.setPowerState(
                    state,
                    (newState) -> {
                        updatePowerState(newState);
                        closeDialog();
                    },
                    this::showError
            );
        }
    }

    private void handleAddSchedule() {
        if (selectedTerminal == null) {
            return;
        }

        TimeSettingDialog dialog = new TimeSettingDialog(
                (onTime, offTime) -> {
                    List<PowerManager.ScheduleEntry> merged = ScheduleHelper.mergeSchedules(
                            currentSchedules, onTime, offTime);
                    updateScheduleList(merged);
                    closeDialog();
                    selectedScheduleIndex = -1;
                    selectedPairIndex = -1;
                },
                () -> {
                    closeDialog();
                    selectedScheduleIndex = -1;
                    selectedPairIndex = -1;
                }
        );

        showDialog(dialog);
    }

    private void handleDeleteSchedule() {
        if (selectedTerminal == null || selectedScheduleIndex == -1) {
            return;
        }

        int previousIndex = selectedScheduleIndex;
        List<PowerManager.ScheduleEntry> updated = ScheduleHelper.removeSchedulePair(
                currentSchedules, selectedScheduleIndex);
        updateScheduleList(updated);

        if (currentSchedules.isEmpty()) {
            selectedScheduleIndex = -1;
            selectedPairIndex = -1;
        } else {
            if (previousIndex >= currentSchedules.size()) {
                selectedScheduleIndex = Math.max(0, currentSchedules.size() - 2);
            } else {
                selectedScheduleIndex = previousIndex;
            }
            if (selectedScheduleIndex >= currentSchedules.size()) {
                selectedScheduleIndex = currentSchedules.size() - 1;
            }
            updatePairSelection();
            renderScheduleList();
        }
    }

    private void handleEditSchedule() {
        if (selectedTerminal == null || selectedScheduleIndex == -1) {
            return;
        }

        ScheduleHelper.SchedulePair pair = ScheduleHelper.getSchedulePair(
                currentSchedules, selectedScheduleIndex);

        if (pair == null) {
            showError("쌍을 찾을 수 없습니다.");
            return;
        }

        int editingIndex = selectedScheduleIndex;

        TimeSettingDialog dialog = new TimeSettingDialog(
                pair.getOnTime(),
                pair.getOffTime(),
                (onTime, offTime) -> {
                    List<PowerManager.ScheduleEntry> updated = ScheduleHelper.updateSchedulePair(
                            currentSchedules, editingIndex, onTime, offTime);
                    updateScheduleList(updated);

                    if (selectedScheduleIndex >= currentSchedules.size()) {
                        selectedScheduleIndex = currentSchedules.size() - 1;
                    }
                    if (selectedScheduleIndex < 0) {
                        selectedScheduleIndex = -1;
                        selectedPairIndex = -1;
                    } else {
                        renderScheduleList();
                    }

                    closeDialog();
                    javafx.application.Platform.runLater(this::requestFocus);
                },
                () -> {
                    closeDialog();
                    javafx.application.Platform.runLater(this::requestFocus);
                }
        );

        showDialog(dialog);
    }

    private void handleSaveSchedule() {
        if (selectedTerminal == null) {
            return;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog(
                "스케쥴을 저장하시겠습니까?",
                () -> {
                    closeDialog();
                    executeSaveSchedule();
                },
                this::closeDialog
        );

        showDialog(confirmDialog);
    }

    private void executeSaveSchedule() {
        showDialog(new ProgressDialog("저장 중입니다..."));

        ScheduleController controller = controllers.get(selectedTerminal);
        if (controller != null) {
            controller.saveSchedule(
                    currentSchedules,
                    (state) -> {
                        closeDialog();
                        showSuccessDialog("성공", "저장되었습니다.");
                        autoModeRadio.setSelected(true);
                        updatePowerState(state);
                    },
                    this::showError
            );
        }
    }

    private void showError(String message) {
        closeDialog();
        showErrorDialog("오류", message);
    }

    private void showSuccessDialog(String title, String message) {
        SuccessDialog dialog = new SuccessDialog(title, message);
        getChildren().add(dialog);
        dialog.showTemporary(2000);
    }

    private void showErrorDialog(String title, String message) {
        ErrorDialog dialog = new ErrorDialog(title, message);
        getChildren().add(dialog);
        dialog.showTemporary(2000);
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