package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tauruscontrol.controller.RebootScheduleController;
import tauruscontrol.domain.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;

public class RebootScheduleDialog extends BaseSettingDialog {

    private final RebootScheduleController controller = new RebootScheduleController();
    private final Label rebootTimeLabel = new Label();
    private final List<Button> dayButtons = new ArrayList<>();
    private final TextField hourField;
    private final TextField minuteField;
    private final Button setButton = new Button("재부팅 설정");
    private int selectedDayIndex = 0;
    private ProgressDialog loadingDialog;
    private SuccessDialog successDialog;
    private ErrorDialog errorDialog;

    public RebootScheduleDialog(Terminal terminal, Runnable onClose) {
        super(terminal, "재부팅 설정", onClose);

        hourField = createTimeFieldWithMax(23);
        minuteField = createTimeFieldWithMax(59);

        setupArrowKeyHandlers(hourField, 23);
        setupArrowKeyHandlers(minuteField, 59);

        initializeContent();
        loadRebootTime();
    }

    @Override
    protected VBox createContent() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        // 타이틀
        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 0, 0));
        Label titleLabel = new Label("재부팅 설정");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titleBox.getChildren().add(titleLabel);

        // 중앙 컨텐츠
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30, 20, 30, 20));
        VBox.setVgrow(content, Priority.ALWAYS);

        // 재부팅 시간 표시
        rebootTimeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;");

        // 요일/시간 선택 UI
        HBox selectionBox = createSelectionBox();

        // 재부팅 설정 버튼
        setButton.setPrefWidth(120);
        setButton.setStyle(
                "-fx-background-color: #1E88E5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );

        setButton.setOnMouseEntered(e ->
                setButton.setStyle(
                        "-fx-background-color: #1976D2;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-border-color: #6a6a6a;" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                )
        );

        setButton.setOnMouseExited(e ->
                setButton.setStyle(
                        "-fx-background-color: #1E88E5;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-border-color: #6a6a6a;" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 5;" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                )
        );

        setButton.setOnAction(e -> handleRebootSet());

        VBox buttonContainer = new VBox(setButton);
        buttonContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonContainer, new Insets(5, 0, 25, 0));

        content.getChildren().addAll(rebootTimeLabel, selectionBox, buttonContainer);

        mainContainer.getChildren().addAll(titleBox, content);
        return mainContainer;
    }

    private HBox createSelectionBox() {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);

        // 요일 버튼
        HBox dayButtonBox = createDayButtons();

        // 시간 선택
        HBox timeBox = new HBox(5);
        timeBox.setAlignment(Pos.CENTER);

        // 시간 입력
        VBox hourBox = new VBox(2);
        hourBox.setAlignment(Pos.CENTER);

        Button hourUp = createSpinnerButton("▲", hourField, 23);
        Button hourDown = createSpinnerButton("▼", hourField, 23);
        hourUp.setFocusTraversable(false);
        hourDown.setFocusTraversable(false);

        hourBox.getChildren().addAll(hourUp, hourField, hourDown);

        Label hourLabel = new Label("시");
        hourLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        // 분 입력
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

        box.getChildren().addAll(dayButtonBox, timeBox);
        return box;
    }

    private HBox createDayButtons() {
        HBox buttonBox = new HBox(3);
        buttonBox.setAlignment(Pos.CENTER);

        String[] labels = {"일", "월", "화", "수", "목", "금", "토"};

        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            Button button = new Button(labels[i]);
            button.setPrefWidth(32);
            button.setPrefHeight(32);
            button.setStyle(buildDayButtonStyle(i == selectedDayIndex));
            button.setFocusTraversable(false);

            button.setOnAction(e -> {
                selectedDayIndex = index;
                updateDayButtonStates();
            });

            dayButtons.add(button);
            buttonBox.getChildren().add(button);
        }

        return buttonBox;
    }

    private void updateDayButtonStates() {
        for (int i = 0; i < dayButtons.size(); i++) {
            dayButtons.get(i).setStyle(buildDayButtonStyle(i == selectedDayIndex));
        }
    }

    private String buildDayButtonStyle(boolean selected) {
        String bgColor = selected ? "#1E88E5" : "#3a3a3a";
        return String.format(
                "-fx-background-color: %s;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;",
                bgColor
        );
    }

    private TextField createTimeFieldWithMax(int max) {
        TextField field = new TextField("00");
        field.setPrefWidth(48);
        field.setPrefHeight(32);
        field.setAlignment(Pos.CENTER);
        field.setStyle(
                "-fx-background-color: #3a3a3a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
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

    private Button createSpinnerButton(String text, TextField field, int max) {
        Button button = new Button(text);
        button.setPrefWidth(40);
        button.setPrefHeight(16);
        button.setStyle(
                "-fx-background-color: #3a3a3a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 8px;" +
                        "-fx-background-radius: 2;" +
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

    private void loadRebootTime() {
        showLoadingDialog();

        controller.loadRebootTime(
                terminal,
                data -> {
                    parseCronAndUpdateUI(data.getCron());
                    rebootTimeLabel.setText("현재 설정된 재부팅 시간: " + data.getDisplayText());
                    hideLoadingDialog();
                },
                error -> {
                    hideLoadingDialog();
                    if (error.equals("재부팅 초기 설정이 되지 않았습니다.") ||
                        error.equals("재부팅 시간이 설정되지 않았습니다.")) {
                        rebootTimeLabel.setText(error);
                    } else {
                        rebootTimeLabel.setText("재부팅 시간 조회 실패");
                        showErrorDialog(error);
                    }
                }
        );
    }

    private void parseCronAndUpdateUI(String cron) {
        String[] parts = cron.split(" ");
        String minute = parts[1];
        String hour = parts[2];
        String dayOfWeek = parts[5];

        hourField.setText(String.format("%02d", Integer.parseInt(hour)));
        minuteField.setText(String.format("%02d", Integer.parseInt(minute)));

        int dayIndex = Integer.parseInt(dayOfWeek) - 1;
        if (dayIndex >= 0 && dayIndex < 7) {
            selectedDayIndex = dayIndex;
            updateDayButtonStates();
        }
    }

    private void handleRebootSet() {
        int hour = parseTime(hourField.getText());
        int minute = parseTime(minuteField.getText());
        int dayOfWeek = selectedDayIndex + 1;

        String cron = String.format("00 %02d %02d ? * %d", minute, hour, dayOfWeek);

        showLoadingDialog();

        controller.setRebootTime(
                terminal,
                cron,
                () -> {
                    hideLoadingDialog();
                    showSuccessDialog();
                    rebootTimeLabel.setText(String.format("현재 설정된 재부팅 시간: %s %02d시 %02d분",
                            getDayName(selectedDayIndex), hour, minute));
                },
                error -> {
                    hideLoadingDialog();
                    showErrorDialog(error);
                }
        );
    }

    private String getDayName(int index) {
        String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"};
        return dayNames[index];
    }

    private void showLoadingDialog() {
        if (loadingDialog != null) {
            return;
        }
        loadingDialog = new ProgressDialog("로딩 중입니다...");
        getChildren().add(loadingDialog);
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            getChildren().remove(loadingDialog);
            loadingDialog = null;
        }
    }

    private void showSuccessDialog() {
        successDialog = new SuccessDialog("설정 완료", "재부팅 시간이 설정되었습니다.");
        getChildren().add(successDialog);
        successDialog.showTemporary(2000);
    }

    private void showErrorDialog(String message) {
        errorDialog = new ErrorDialog("설정 실패", message);
        getChildren().add(errorDialog);
        errorDialog.showTemporary(3000);
    }
}