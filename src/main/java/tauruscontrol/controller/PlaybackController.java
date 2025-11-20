package tauruscontrol.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tauruscontrol.domain.media.MediaManager;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;
import tauruscontrol.service.ProgramManager;

import java.util.List;
import java.util.function.Consumer;

public class PlaybackController {

    private final TerminalManager terminalManager;
    private final MediaManager mediaManager;
    private final ProgramManager programManager;

    public PlaybackController(TerminalManager terminalManager) {
        this.terminalManager = terminalManager;
        this.mediaManager = new MediaManager();
        this.programManager = new ProgramManager();
    }

    public List<Terminal> getLoggedInTerminals() {
        return terminalManager.getLoggedInTerminals();
    }

    public void addMedia(String path, Runnable onSuccess, Consumer<Exception> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                mediaManager.addMedia(path);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(onSuccess);
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> onError.accept((Exception) task.getException()));
        });

        new Thread(task).start();
    }

    public void sendProgram(Terminal terminal, Consumer<Integer> onProgress, Runnable onSuccess, Consumer<Exception> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                programManager.findOrCreateProgramId(terminal);
                programManager.editProgram(mediaManager);
                programManager.saveProgramToLocal();
                programManager.publishProgram(terminal, mediaManager, onProgress);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(onSuccess);
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> onError.accept((Exception) task.getException()));
        });

        new Thread(task).start();
    }

    public MediaManager getMediaManager() {
        return mediaManager;
    }
}