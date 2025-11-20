package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tauruscontrol.util.ScheduleHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class TimeSettingDialog extends StackPane {

    private final Set<Integer> selectedDaysOn = new HashSet<>();
    private final Set<Integer> selectedDaysOff = new HashSet<>();
    private final TextField onHourField;
    private final TextField onMinuteField;
    private final TextField offHourField;
    private final TextField offMinuteField;
    private final List<Button> dayButtonsOn = new ArrayList<>();
    private final List<Button> dayButtonsOff = new ArrayList<>();
    private final BiConsumer<ScheduleTime, ScheduleTime> onConfirmCallback;
    private StackPane errorOverlay;
    private boolean justClosedError = false;
    private javafx.event.EventHandler<javafx.scene.input.KeyEvent> sceneKeyHandler;

    public TimeSettingDialog(BiConsumer<ScheduleTime, ScheduleTime> onConfirm, Runnable onCancel) {
        this(null, null, onConfirm, onCancel);
    }

    public TimeSettingDialog(ScheduleTime existingOn, ScheduleTime existingOff,
                            BiConsumer<ScheduleTime, ScheduleTime> onConfirm, Runnable onCancel) {
        this.onConfirmCallback = onConfirm;
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        setAlignment(Pos.CENTER);

        VBox card = new VBox(20);
        card.setMaxWidth(800);
        card.setMaxHeight(350);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
            "-fx-background-color: #2a2a2a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #999999;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );

        Label title = new Label("시간 설정");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        onHourField = createTimeFieldWithMax(23);
        onMinuteField = createTimeFieldWithMax(59);
        offHourField = createTimeFieldWithMax(23);
        offMinuteField = createTimeFieldWithMax(59);

        setupArrowKeyHandlers(onHourField, 23);
        setupArrowKeyHandlers(onMinuteField, 59);
        setupArrowKeyHandlers(offHourField, 23);
        setupArrowKeyHandlers(offMinuteField, 59);

        HBox onTimeRow = createTimeRow("ON", selectedDaysOn, dayButtonsOn, onHourField, onMinuteField, existingOn);
        HBox offTimeRow = createTimeRow("OFF", selectedDaysOff, dayButtonsOff, offHourField, offMinuteField, existingOff);

        if (existingOn == null && existingOff == null) {
            syncDays(true);
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button confirmButton = new Button("확인");
        confirmButton.setPrefWidth(100);
        confirmButton.setStyle(
            "-fx-background-color: #1E88E5;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        confirmButton.setOnMouseEntered(e -> confirmButton.setStyle(
            "-fx-background-color: #1976D2;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        ));
        confirmButton.setOnMouseExited(e -> confirmButton.setStyle(
            "-fx-background-color: #1E88E5;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        ));

        Button cancelButton = new Button("취소");
        cancelButton.setPrefWidth(100);
        cancelButton.setStyle(
            "-fx-background-color: #5a5a5a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
            "-fx-background-color: #4a4a4a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        ));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
            "-fx-background-color: #5a5a5a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        ));

        confirmButton.setOnAction(e -> handleConfirm());

        cancelButton.setOnAction(e -> onCancel.run());

        buttonBox.getChildren().addAll(confirmButton, cancelButton);

        card.getChildren().addAll(title, onTimeRow, offTimeRow, buttonBox);
        getChildren().add(card);

        card.setOnMouseClicked(e -> {
            this.requestFocus();
            e.consume();
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                onCancel.run();
                event.consume();
            }
        });

        setOnMouseClicked(e -> e.consume());
        setOnMousePressed(e -> e.consume());
        setOnMouseReleased(e -> e.consume());

        setFocusTraversable(true);
        javafx.application.Platform.runLater(this::requestFocus);

        cancelButton.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.TAB && !event.isShiftDown()) {
                onHourField.requestFocus();
                event.consume();
            }
        });

        onHourField.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.TAB && event.isShiftDown()) {
                cancelButton.requestFocus();
                event.consume();
            }
        });

        sceneKeyHandler = event -> {
            if (!isVisible()) {
                return;
            }
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                if (errorOverlay != null) {
                    getChildren().remove(errorOverlay);
                    errorOverlay = null;
                    event.consume();
                    javafx.application.Platform.runLater(() -> justClosedError = false);
                    justClosedError = true;
                    return;
                }
                if (justClosedError) {
                    event.consume();
                    return;
                }
                handleConfirm();
                event.consume();
            }
        };

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null && sceneKeyHandler != null) {
                oldScene.removeEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, sceneKeyHandler);
            }
            if (newScene != null && sceneKeyHandler != null) {
                newScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, sceneKeyHandler);
            }
        });

        visibleProperty().addListener((obs, wasVisible, isNowVisible) -> {
            if (!isNowVisible && getScene() != null && sceneKeyHandler != null) {
                getScene().removeEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, sceneKeyHandler);
            }
        });
    }

    private void handleConfirm() {
        Set<Integer> onDays = getDaysFromSelection(selectedDaysOn);
        if (onDays.isEmpty()) {
            onDays = new HashSet<>();
            for (int i = 1; i <= 7; i++) {
                onDays.add(i);
            }
        }

        Set<Integer> offDays = getDaysFromSelection(selectedDaysOff);
        if (offDays.isEmpty()) {
            offDays = new HashSet<>();
            for (int i = 1; i <= 7; i++) {
                offDays.add(i);
            }
        }

        int onHour = parseTime(onHourField.getText());
        int onMinute = parseTime(onMinuteField.getText());
        int offHour = parseTime(offHourField.getText());
        int offMinute = parseTime(offMinuteField.getText());

        int onTotalMinutes = onHour * 60 + onMinute;
        int offTotalMinutes = offHour * 60 + offMinute;

        if (onTotalMinutes >= offTotalMinutes) {
            showError("ON 시간이 OFF 시간보다\n빠르거나 같을 수 없습니다.");
            return;
        }

        ScheduleTime onTime = new ScheduleTime(onHour, onMinute, onDays);
        ScheduleTime offTime = new ScheduleTime(offHour, offMinute, offDays);

        if (ScheduleHelper.hasTimeConflict(onTime, offTime)) {
            showError("ON과 OFF 시간이 같은 요일에\n중복될 수 없습니다.");
            return;
        }

        onConfirmCallback.accept(onTime, offTime);
    }

    private void showError(String message) {
        if (errorOverlay != null) {
            return;
        }

        errorOverlay = new StackPane();
        errorOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
        errorOverlay.setAlignment(Pos.CENTER);

        VBox errorBox = new VBox(15);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPrefSize(300, 150);
        errorBox.setMaxSize(300, 150);
        errorBox.setStyle(
            "-fx-background-color: #2a2a2a;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #ff6b6b;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        );

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-text-alignment: center;");
        errorLabel.setWrapText(true);

        Button okButton = new Button("확인");
        okButton.setPrefWidth(100);
        okButton.setStyle(
            "-fx-background-color: #1E88E5;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        okButton.setOnAction(e -> {
            getChildren().remove(errorOverlay);
            errorOverlay = null;
        });

        errorBox.getChildren().addAll(errorLabel, okButton);
        errorOverlay.getChildren().add(errorBox);
        getChildren().add(errorOverlay);
    }

    private TextField createTimeFieldWithMax(int max) {
        TextField field = new TextField("00");
        field.setPrefWidth(60);
        field.setAlignment(javafx.geometry.Pos.CENTER);
        field.setStyle(
            "-fx-background-color: #3a3a3a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: #6a6a6a;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                javafx.application.Platform.runLater(field::selectAll);
            } else {
                String text = field.getText();
                if (text.isEmpty()) {
                    field.setText("00");
                } else {
                    int value = parseTime(text);
                    if (value > max) {
                        value = max;
                    }
                    field.setText(String.format("%02d", value));
                }
            }
        });

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                javafx.application.Platform.runLater(() -> field.setText(oldVal));
                return;
            }

            if (newVal.isEmpty()) {
                return;
            }

            if (newVal.length() > 2) {
                String lastTwoDigits = newVal.substring(newVal.length() - 2);
                int lastTwoValue = Integer.parseInt(lastTwoDigits);

                if (lastTwoValue <= max) {
                    javafx.application.Platform.runLater(() -> {
                        field.setText(lastTwoDigits);
                        field.positionCaret(field.getText().length());
                    });
                } else {
                    String lastChar = newVal.substring(newVal.length() - 1);
                    int lastDigit = Integer.parseInt(lastChar);
                    javafx.application.Platform.runLater(() -> {
                        field.setText(String.format("%02d", lastDigit));
                        field.positionCaret(field.getText().length());
                    });
                }
                return;
            }

            int value = Integer.parseInt(newVal);
            if (value > max) {
                String lastChar = newVal.substring(newVal.length() - 1);
                int lastDigit = Integer.parseInt(lastChar);
                javafx.application.Platform.runLater(() -> {
                    field.setText(String.format("%02d", lastDigit));
                    field.positionCaret(field.getText().length());
                });
            }
        });

        return field;
    }

    private void setupArrowKeyHandlers(TextField field, int max) {
        field.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    if (event.getTarget() == field) {
                        javafx.scene.input.KeyCode code = event.getCode();
                        if (code == javafx.scene.input.KeyCode.UP || code == javafx.scene.input.KeyCode.DOWN) {
                            int value = parseTime(field.getText());
                            if (code == javafx.scene.input.KeyCode.UP) {
                                value = (value + 1) % (max + 1);
                            } else {
                                value = (value - 1 + max + 1) % (max + 1);
                            }
                            field.setText(String.format("%02d", value));
                            field.selectAll();
                            event.consume();
                        }
                    }
                });
            }
        });
    }

    private HBox createTimeRow(String label, Set<Integer> selectedDays, List<Button> dayButtons,
                               TextField hourField, TextField minuteField, ScheduleTime existingTime) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0, 0, 0, 20));

        Label actionLabel = new Label(label);
        actionLabel.setPrefWidth(40);
        actionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        boolean isOnRow = label.equals("ON");
        HBox dayButtonBox = createDayButtons(selectedDays, dayButtons, isOnRow);

        if (existingTime != null) {
            hourField.setText(String.format("%02d", existingTime.getHour()));
            minuteField.setText(String.format("%02d", existingTime.getMinute()));
            for (int day : existingTime.getDays()) {
                selectedDays.add(day);
            }
            updateDayButtonStates(selectedDays, dayButtons);
        } else {
            for (int i = 1; i <= 7; i++) {
                selectedDays.add(i);
            }
            updateDayButtonStates(selectedDays, dayButtons);
        }

        HBox timeBox = new HBox(5);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        VBox hourBox = new VBox(2);
        hourBox.setAlignment(Pos.CENTER);
        Button hourUp = createSpinnerButton("▲", hourField, 23);
        Button hourDown = createSpinnerButton("▼", hourField, 23);
        hourUp.setFocusTraversable(false);
        hourDown.setFocusTraversable(false);
        hourBox.getChildren().addAll(hourUp, hourField, hourDown);

        Label hourLabel = new Label("시");
        hourLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        VBox minuteBox = new VBox(2);
        minuteBox.setAlignment(Pos.CENTER);
        Button minuteUp = createSpinnerButton("▲", minuteField, 59);
        Button minuteDown = createSpinnerButton("▼", minuteField, 59);
        minuteUp.setFocusTraversable(false);
        minuteDown.setFocusTraversable(false);
        minuteBox.getChildren().addAll(minuteUp, minuteField, minuteDown);

        Label minuteLabel = new Label("분");
        minuteLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        timeBox.getChildren().addAll(hourBox, hourLabel, minuteBox, minuteLabel);

        row.getChildren().addAll(actionLabel, dayButtonBox, timeBox);
        return row;
    }

    private HBox createDayButtons(Set<Integer> selectedDays, List<Button> dayButtons, boolean isOnRow) {
        HBox buttonBox = new HBox(3);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        String[] labels = {"매일", "평일", "주말", "월", "화", "수", "목", "금", "토", "일"};
        int[][] dayMaps = {
            {1,2,3,4,5,6,7},
            {2,3,4,5,6},
            {1,7},
            {2}, {3}, {4}, {5}, {6}, {7}, {1}
        };

        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            Button button = new Button(labels[i]);
            button.setPrefWidth(i < 3 ? 55 : 40);
            button.setPrefHeight(40);
            button.setStyle(buildDayButtonStyle(false));
            button.setFocusTraversable(false);

            button.setOnAction(e -> {
                toggleDay(button, dayMaps[index], selectedDays, dayButtons, dayMaps);
                syncDays(isOnRow);
            });

            dayButtons.add(button);
            buttonBox.getChildren().add(button);
        }

        return buttonBox;
    }

    private void toggleDay(Button button, int[] days, Set<Integer> selectedDays,
                          List<Button> dayButtons, int[][] dayMaps) {
        Set<Integer> buttonDays = new HashSet<>();
        for (int day : days) {
            buttonDays.add(day);
        }

        if (days.length == 1) {
            for (int day : days) {
                if (selectedDays.contains(day)) {
                    selectedDays.remove(day);
                } else {
                    selectedDays.add(day);
                }
            }
        } else {
            if (selectedDays.equals(buttonDays)) {
                selectedDays.clear();
            } else {
                selectedDays.clear();
                selectedDays.addAll(buttonDays);
            }
        }

        updateDayButtonStates(selectedDays, dayButtons);

        this.requestFocus();
    }

    private void updateDayButtonStates(Set<Integer> selectedDays, List<Button> dayButtons) {
        int[][] dayMaps = {
            {1,2,3,4,5,6,7},
            {2,3,4,5,6},
            {1,7},
            {2}, {3}, {4}, {5}, {6}, {7}, {1}
        };

        for (int i = 0; i < dayButtons.size(); i++) {
            boolean shouldHighlight = false;

            if (i < 3) {
                Set<Integer> buttonDays = new HashSet<>();
                for (int day : dayMaps[i]) {
                    buttonDays.add(day);
                }
                shouldHighlight = selectedDays.equals(buttonDays);
            } else {
                shouldHighlight = selectedDays.contains(dayMaps[i][0]);
            }

            dayButtons.get(i).setStyle(buildDayButtonStyle(shouldHighlight));
        }
    }

    private String buildDayButtonStyle(boolean selected) {
        String bgColor = selected ? "#1E88E5" : "#3a3a3a";
        return String.format(
            "-fx-background-color: %s;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #6a6a6a;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;",
            bgColor
        );
    }

    private void syncDays(boolean fromOnRow) {
        if (fromOnRow) {
            selectedDaysOff.clear();
            selectedDaysOff.addAll(selectedDaysOn);
            updateDayButtonStates(selectedDaysOff, dayButtonsOff);
        } else {
            selectedDaysOn.clear();
            selectedDaysOn.addAll(selectedDaysOff);
            updateDayButtonStates(selectedDaysOn, dayButtonsOn);
        }
    }

    private Button createSpinnerButton(String text, TextField field, int max) {
        Button button = new Button(text);
        button.setPrefWidth(50);
        button.setPrefHeight(20);
        button.setStyle(
            "-fx-background-color: #3a3a3a;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px;" +
            "-fx-background-radius: 3;" +
            "-fx-cursor: hand;"
        );

        button.setOnAction(e -> {
            int value = parseTime(field.getText());
            if (text.equals("▲")) {
                value = (value + 1) % (max + 1);
            } else {
                value = (value - 1 + max + 1) % (max + 1);
            }
            field.setText(String.format("%02d", value));
            this.requestFocus();
        });

        return button;
    }

    private int parseTime(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            int value = Integer.parseInt(text);
            if (value < 0) {
                return 0;
            }
            return value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Set<Integer> getDaysFromSelection(Set<Integer> selectedDays) {
        return new HashSet<>(selectedDays);
    }

    public static class ScheduleTime {
        private final int hour;
        private final int minute;
        private final Set<Integer> days;

        public ScheduleTime(int hour, int minute, Set<Integer> days) {
            this.hour = Math.min(23, Math.max(0, hour));
            this.minute = Math.min(59, Math.max(0, minute));
            this.days = days;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public Set<Integer> getDays() {
            return days;
        }
    }
}