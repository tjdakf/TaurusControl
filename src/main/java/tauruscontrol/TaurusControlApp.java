package tauruscontrol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tauruscontrol.view.MainView;

public class TaurusControlApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView(primaryStage);

        Scene scene = new Scene(mainView, 1200, 700);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}