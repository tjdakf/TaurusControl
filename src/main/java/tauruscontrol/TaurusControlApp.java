package tauruscontrol;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tauruscontrol.view.MainView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TaurusControlApp extends Application {

    private static final int LOCK_PORT = 13579;
    private static ServerSocket lockSocket;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        MainView mainView = new MainView(primaryStage);

        Scene scene = new Scene(mainView, 1200, 700);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            releaseLock();
            Platform.exit();
        });

        startLockServer();
    }

    public static void main(String[] args) {
        if (!acquireLock()) {
            notifyRunningInstance();
            System.exit(0);
        }

        launch(args);
    }

    private static boolean acquireLock() {
        try {
            lockSocket = new ServerSocket(LOCK_PORT);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void startLockServer() {
        Thread serverThread = new Thread(() -> {
            while (!lockSocket.isClosed()) {
                try {
                    Socket client = lockSocket.accept();
                    client.close();

                    Platform.runLater(() -> {
                        primaryStage.setIconified(false);
                        primaryStage.toFront();
                        primaryStage.requestFocus();
                    });
                } catch (IOException e) {
                    break;
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void notifyRunningInstance() {
        try (Socket socket = new Socket("localhost", LOCK_PORT)) {
            // 연결만 해도 신호 전달됨
        } catch (IOException e) {
            // 무시
        }
    }

    private static void releaseLock() {
        if (lockSocket != null) {
            try {
                lockSocket.close();
            } catch (IOException e) {
                // 무시
            }
        }
    }
}