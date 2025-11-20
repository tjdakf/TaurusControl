package tauruscontrol.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.service.TerminalTimeManager;

import java.util.function.Consumer;

public class TimeSyncController {

    private final TerminalTimeManager terminalTimeManager;

    public TimeSyncController() {
        this.terminalTimeManager = new TerminalTimeManager();
    }

    public void getCurrentTime(
            Terminal terminal,
            Consumer<Long> onSuccess,
            Consumer<String> onError
    ) {
        Task<Long> task = new Task<>() {
            protected Long call() throws Exception {
                return terminalTimeManager.getCurrentTime(terminal);
            }
        };

        task.setOnSucceeded(event -> {
            Long epochTime = task.getValue();
            Platform.runLater(() -> onSuccess.accept(epochTime));
        });

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                Platform.runLater(() -> onError.accept(exception.getMessage()));
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void syncTime(
            Terminal terminal,
            Runnable onSuccess,
            Consumer<String> onError
    ) {
        Task<Void> task = new Task<>() {
            protected Void call() throws Exception {
                terminalTimeManager.setCurrentTime(terminal);
                return null;
            }
        };

        task.setOnSucceeded(event -> Platform.runLater(onSuccess));

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                Platform.runLater(() -> onError.accept(exception.getMessage()));
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
