package tauruscontrol.controller;

import javafx.concurrent.Task;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.service.RebootManager;

import java.util.function.Consumer;

public class RebootScheduleController {

    private final RebootManager rebootManager;

    public RebootScheduleController() {
        this.rebootManager = new RebootManager();
    }

    public void loadRebootTime(Terminal terminal,
                               Consumer<RebootManager.RebootTimeData> onSuccess,
                               Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                final Exception[] error = {null};

                rebootManager.searchRebootTask(
                        terminal,
                        data -> javafx.application.Platform.runLater(() -> onSuccess.accept(data)),
                        errorMsg -> error[0] = new RuntimeException(errorMsg)
                );

                if (error[0] != null) {
                    throw error[0];
                }

                return null;
            }
        };

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            String message = exception.getMessage();

            if (message.equals("NOT_INITIALIZED")) {
                javafx.application.Platform.runLater(() -> onError.accept("재부팅 초기 설정이 되지 않았습니다."));
            } else if (message.equals("NO_TIME_SET")) {
                javafx.application.Platform.runLater(() -> onError.accept("재부팅 시간이 설정되지 않았습니다."));
            } else {
                javafx.application.Platform.runLater(() -> onError.accept("알 수 없는 오류가 발생했습니다."));
            }
        });

        new Thread(task).start();
    }

    public void setRebootTime(Terminal terminal,
                              String cron,
                              Runnable onSuccess,
                              Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                final Exception[] error = {null};

                rebootManager.setRebootTask(
                        terminal,
                        cron,
                        () -> javafx.application.Platform.runLater(onSuccess),
                        errorMsg -> error[0] = new RuntimeException(errorMsg)
                );

                if (error[0] != null) {
                    throw error[0];
                }

                return null;
            }
        };

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            javafx.application.Platform.runLater(() -> onError.accept(exception.getMessage()));
        });

        new Thread(task).start();
    }
}
