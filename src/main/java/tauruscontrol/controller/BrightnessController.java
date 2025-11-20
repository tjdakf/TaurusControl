package tauruscontrol.controller;

import javafx.concurrent.Task;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.service.BrightnessManager;

import java.util.function.Consumer;

public class BrightnessController {

    private final BrightnessManager brightnessManager;

    public BrightnessController() {
        this.brightnessManager = new BrightnessManager();
    }

    public void loadBrightness(Terminal terminal,
                               Consumer<Float> onSuccess,
                               Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                final Exception[] error = {null};

                brightnessManager.readLedBrightness(
                        terminal,
                        brightness -> javafx.application.Platform.runLater(() -> onSuccess.accept(brightness)),
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

    public void setBrightness(Terminal terminal,
                              float brightness,
                              Runnable onSuccess,
                              Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                final Exception[] error = {null};

                brightnessManager.setLedBrightness(
                        terminal,
                        brightness,
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
