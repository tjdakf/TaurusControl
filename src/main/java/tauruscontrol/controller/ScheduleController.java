package tauruscontrol.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tauruscontrol.service.PowerManager;

import java.util.List;
import java.util.function.Consumer;

public class ScheduleController {

    private final PowerManager powerManager;

    public ScheduleController(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    public void loadInitialData(
            Consumer<String> onModeLoaded,
            Consumer<String> onStateLoaded,
            Consumer<List<PowerManager.ScheduleEntry>> onScheduleLoaded,
            Consumer<String> onError
    ) {
        Task<Void> task = new Task<>() {
            protected Void call() throws Exception {
                try {
                    String mode = powerManager.readPowerMode();
                    String state = powerManager.readPowerState();
                    List<PowerManager.ScheduleEntry> schedules = powerManager.readPowerSchedule();

                    Platform.runLater(() -> {
                        if (mode != null) {
                            onModeLoaded.accept(mode);
                        }
                        if (state != null) {
                            onStateLoaded.accept(state);
                        }
                        onScheduleLoaded.accept(schedules != null ? schedules : new java.util.ArrayList<>());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> onError.accept(e.getMessage()));
                }

                return null;
            }
        };

        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            if (exception != null) {
                Platform.runLater(() -> onError.accept(exception.getMessage()));
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        java.util.Timer timer = new java.util.Timer(true);
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (task.isRunning()) {
                        task.cancel();
                        onError.accept("데이터 로드 시간 초과 (5초)");
                    }
                });
            }
        }, 5000);
    }

    public void setPowerMode(String mode, Runnable onSuccess, Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            protected Void call() throws Exception {
                powerManager.setPowerMode(mode);
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

    public void setPowerState(String state, Consumer<String> onStateUpdated, Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            protected Void call() throws Exception {
                powerManager.setPowerState(state);
                String newState = powerManager.readPowerState();

                Platform.runLater(() -> onStateUpdated.accept(newState));

                return null;
            }
        };

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

    public void saveSchedule(
            List<PowerManager.ScheduleEntry> schedules,
            Consumer<String> onSuccess,
            Consumer<String> onError
    ) {
        Task<String> task = new Task<>() {
            protected String call() throws Exception {
                powerManager.setPowerSchedule(schedules);
                powerManager.setPowerMode("AUTO");
                String state = powerManager.readPowerState();
                return state;
            }
        };

        task.setOnSucceeded(event -> {
            String state = task.getValue();
            Platform.runLater(() -> onSuccess.accept(state));
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
}