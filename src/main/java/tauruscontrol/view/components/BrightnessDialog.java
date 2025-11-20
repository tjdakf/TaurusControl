package tauruscontrol.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tauruscontrol.controller.BrightnessController;
import tauruscontrol.domain.terminal.Terminal;

public class BrightnessDialog extends BaseSettingDialog {

    private final BrightnessController controller = new BrightnessController();
    private final Slider brightnessSlider = new Slider(0, 100, 50);
    private final TextField brightnessField;
    private final Button upButton;
    private final Button downButton;
    private ProgressDialog loadingDialog;
    private SuccessDialog successDialog;
    private ErrorDialog errorDialog;
    private boolean isUpdatingFromSlider = false;
    private boolean isUpdatingFromField = false;
    private int lastSentValue = 50;

    public BrightnessDialog(Terminal terminal, Runnable onClose) {
        super(terminal, "LED 밝기 설정", onClose);

        brightnessField = createBrightnessField();
        upButton = createSpinnerButton("▲");
        downButton = createSpinnerButton("▼");

        initializeContent();
        loadBrightness();
    }

    @Override
    protected VBox createContent() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        mainContainer.setOnMouseClicked(e -> {
            mainContainer.requestFocus();
            e.consume();
        });

        // 타이틀
        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 0, 0));
        Label titleLabel = new Label("LED 밝기 설정");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titleBox.getChildren().add(titleLabel);

        // 중앙 컨텐츠
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30, 20, 30, 20));
        VBox.setVgrow(content, Priority.ALWAYS);

        content.setOnMouseClicked(e -> {
            content.requestFocus();
            e.consume();
        });

        HBox controlBox = createControlBox();

        content.getChildren().add(controlBox);

        mainContainer.getChildren().addAll(titleBox, content);
        return mainContainer;
    }

    private HBox createControlBox() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(500);

        brightnessSlider.setPrefWidth(360);
        brightnessSlider.setShowTickLabels(false);
        brightnessSlider.setShowTickMarks(false);
        brightnessSlider.setMajorTickUnit(10);
        brightnessSlider.setMinorTickCount(0);
        brightnessSlider.setBlockIncrement(1);
        brightnessSlider.setSnapToTicks(true);

        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isUpdatingFromField) {
                isUpdatingFromSlider = true;
                int value = newVal.intValue();
                brightnessField.setText(String.valueOf(value));
                isUpdatingFromSlider = false;
            }
        });

        brightnessSlider.setOnMouseReleased(e -> handleBrightnessSet());

        HBox fieldBox = new HBox(5);
        fieldBox.setAlignment(Pos.CENTER);

        VBox spinnerBox = new VBox(2);
        spinnerBox.setAlignment(Pos.CENTER);
        upButton.setFocusTraversable(false);
        downButton.setFocusTraversable(false);
        spinnerBox.getChildren().addAll(upButton, downButton);

        Label percentLabel = new Label("%");
        percentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        fieldBox.getChildren().addAll(brightnessField, percentLabel, spinnerBox);

        box.getChildren().addAll(brightnessSlider, fieldBox);
        return box;
    }

    private TextField createBrightnessField() {
        TextField field = new TextField("50");
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
                    field.setText("0");
                } else {
                    int value = parseBrightness(text);
                    if (value > 100) {
                        value = 100;
                    }
                    field.setText(String.valueOf(value));
                    updateSliderFromField(value);

                    if (value != lastSentValue) {
                        handleBrightnessSet();
                    }
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

            if (newVal.length() > 3) {
                String lastThreeDigits = newVal.substring(newVal.length() - 3);
                int value = Integer.parseInt(lastThreeDigits);
                if (value <= 100) {
                    javafx.application.Platform.runLater(() -> {
                        field.setText(lastThreeDigits);
                        field.positionCaret(field.getText().length());
                    });
                } else {
                    String lastTwoDigits = newVal.substring(newVal.length() - 2);
                    javafx.application.Platform.runLater(() -> {
                        field.setText(lastTwoDigits);
                        field.positionCaret(field.getText().length());
                    });
                }
                return;
            }

            int value = Integer.parseInt(newVal);
            if (value > 100 && newVal.length() == 3) {
                String lastTwoDigits = newVal.substring(1);
                javafx.application.Platform.runLater(() -> {
                    field.setText(lastTwoDigits);
                    field.positionCaret(field.getText().length());
                });
            }

            if (!isUpdatingFromSlider) {
                updateSliderFromField(parseBrightness(newVal));
            }
        });

        field.setOnAction(e -> {
            int value = parseBrightness(field.getText());
            if (value > 100) {
                value = 100;
            }
            field.setText(String.valueOf(value));
            updateSliderFromField(value);
            handleBrightnessSet();
            this.requestFocus();
        });

        return field;
    }

    private Button createSpinnerButton(String text) {
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
            int value = parseBrightness(brightnessField.getText());
            if (text.equals("▲")) {
                value = Math.min(100, value + 1);
            } else {
                value = Math.max(0, value - 1);
            }
            brightnessField.setText(String.valueOf(value));
            updateSliderFromField(value);
            handleBrightnessSet();
            this.requestFocus();
        });

        return button;
    }

    private void updateSliderFromField(int value) {
        isUpdatingFromField = true;
        brightnessSlider.setValue(value);
        isUpdatingFromField = false;
    }

    private int parseBrightness(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            int value = Integer.parseInt(text);
            if (value < 0) {
                return 0;
            }
            if (value > 100) {
                return 100;
            }
            return value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void loadBrightness() {
        showLoadingDialog();

        controller.loadBrightness(
                terminal,
                brightness -> {
                    int value = Math.round(brightness);
                    brightnessSlider.setValue(value);
                    brightnessField.setText(String.valueOf(value));
                    lastSentValue = value;
                    hideLoadingDialog();
                },
                error -> {
                    hideLoadingDialog();
                    showErrorDialog(error);
                }
        );
    }

    private void handleBrightnessSet() {
        final int value = parseBrightness(brightnessField.getText());
        float brightness = value;

        showLoadingDialog();

        controller.setBrightness(
                terminal,
                brightness,
                () -> {
                    lastSentValue = value;
                    hideLoadingDialog();
                },
                error -> {
                    hideLoadingDialog();
                    showErrorDialog(error);
                }
        );
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
        successDialog = new SuccessDialog("설정 완료", "밝기가 설정되었습니다.");
        getChildren().add(successDialog);
        successDialog.showTemporary(2000);
    }

    private void showErrorDialog(String message) {
        errorDialog = new ErrorDialog("설정 실패", message);
        getChildren().add(errorDialog);
        errorDialog.showTemporary(2000);
    }
}
