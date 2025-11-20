package tauruscontrol.view.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tauruscontrol.controller.TimeSyncController;
import tauruscontrol.domain.terminal.Terminal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeSyncDialog extends BaseSettingDialog {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DEFAULT_ZONEID = "Asia/Seoul";

    private final TimeSyncController controller = new TimeSyncController();
    private final Label localTimeLabel = new Label();
    private final Label terminalTimeLabel = new Label();
    private final Button syncButton = new Button("동기화");
    private Timeline localTimeUpdater;
    private Timeline terminalTimeUpdater;
    private long terminalTimeOffset = 0;
    private ProgressDialog loadingDialog;
    private SuccessDialog successDialog;

    public TimeSyncDialog(Terminal terminal, Runnable onClose) {
        super(terminal, "시간 동기화", onClose);

        initializeContent();
        startLocalTimeClock();
        loadTerminalTime();
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
        Label titleLabel = new Label("시간 동기화");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titleBox.getChildren().add(titleLabel);

        // 중앙 컨텐츠
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 20, 60, 20));
        VBox.setVgrow(content, Priority.ALWAYS);

        localTimeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;");
        terminalTimeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace;");

        syncButton.setPrefWidth(120);
        syncButton.setStyle(
                "-fx-background-color: #1E88E5;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #6a6a6a;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );

        syncButton.setOnMouseEntered(e ->
                syncButton.setStyle(
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

        syncButton.setOnMouseExited(e ->
                syncButton.setStyle(
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

        syncButton.setOnAction(e -> handleSync());

        VBox buttonContainer = new VBox(syncButton);
        buttonContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonContainer, new Insets(35, 0, 0, 0));

        content.getChildren().addAll(localTimeLabel, terminalTimeLabel, buttonContainer);

        mainContainer.getChildren().addAll(titleBox, content);
        return mainContainer;
    }

    private void startLocalTimeClock() {
        localTimeUpdater = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = LocalDateTime.now().format(TIME_FORMATTER);
            localTimeLabel.setText("로컬PC 시간: " + currentTime);
        }));
        localTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        localTimeUpdater.play();

        String currentTime = LocalDateTime.now().format(TIME_FORMATTER);
        localTimeLabel.setText("로컬PC 시간: " + currentTime);
    }

    private void loadTerminalTime() {
        showLoadingDialog();

        controller.getCurrentTime(
                terminal,
                epochTime -> {
                    terminalTimeOffset = epochTime - System.currentTimeMillis();
                    startTerminalTimeClock();
                    hideLoadingDialog();
                },
                error -> {
                    terminalTimeLabel.setText("터미널 시간: 조회 실패");
                    hideLoadingDialog();
                    System.err.println("시간 조회 실패: " + error);
                }
        );
    }

    private void startTerminalTimeClock() {
        if (terminalTimeUpdater != null) {
            terminalTimeUpdater.stop();
        }

        terminalTimeUpdater = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            long currentTerminalTime = System.currentTimeMillis() + terminalTimeOffset;
            LocalDateTime terminalTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(currentTerminalTime),
                    ZoneId.of(DEFAULT_ZONEID)
            );
            String formattedTime = terminalTime.format(TIME_FORMATTER);
            terminalTimeLabel.setText("터미널 시간: " + formattedTime);
        }));
        terminalTimeUpdater.setCycleCount(Timeline.INDEFINITE);
        terminalTimeUpdater.play();

        long currentTerminalTime = System.currentTimeMillis() + terminalTimeOffset;
        LocalDateTime terminalTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(currentTerminalTime),
                ZoneId.of(DEFAULT_ZONEID)
        );
        String formattedTime = terminalTime.format(TIME_FORMATTER);
        terminalTimeLabel.setText("터미널 시간: " + formattedTime);
    }

    private void handleSync() {
        showLoadingDialog();

        controller.syncTime(
                terminal,
                () -> {
                    controller.getCurrentTime(
                            terminal,
                            epochTime -> {
                                terminalTimeOffset = epochTime - System.currentTimeMillis();
                                startTerminalTimeClock();
                                hideLoadingDialog();
                                showSuccessDialog();
                            },
                            error -> {
                                hideLoadingDialog();
                                System.err.println("시간 재조회 실패: " + error);
                            }
                    );
                },
                error -> {
                    hideLoadingDialog();
                    System.err.println("동기화 실패: " + error);
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
        successDialog = new SuccessDialog("동기화 완료", "시간 동기화가 완료되었습니다.");
        getChildren().add(successDialog);
        successDialog.showTemporary(2000);
    }

    public void cleanup() {
        if (localTimeUpdater != null) {
            localTimeUpdater.stop();
        }
        if (terminalTimeUpdater != null) {
            terminalTimeUpdater.stop();
        }
    }
}
