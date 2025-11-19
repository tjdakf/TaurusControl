package tauruscontrol.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.domain.terminal.TerminalManager;

import java.util.List;
import java.util.function.Consumer;

public class LoginController {

    private final TerminalManager terminalManager;

    public LoginController() {
        this.terminalManager = new TerminalManager();
    }

    public void searchTerminals(Consumer<List<Terminal>> onSuccess, Consumer<Exception> onError) {
        Task<List<Terminal>> task = new Task<>() {
            @Override
            protected List<Terminal> call() throws Exception {
                terminalManager.searchTerminal();
                return terminalManager.getTerminals();
            }
        };

        task.setOnSucceeded(event -> {
            Platform.runLater(() -> onSuccess.accept(task.getValue()));
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> onError.accept((Exception) task.getException()));
        });

        new Thread(task).start();
    }

    public void loginTerminal(String sn, String password, Runnable onSuccess, Consumer<Exception> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                terminalManager.loginTerminal(sn, password);
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

    public List<Terminal> getTerminals() {
        return terminalManager.getTerminals();
    }

    public void loginTerminalSync(String sn, String password) throws Exception {
        terminalManager.loginTerminal(sn, password);
    }
}
