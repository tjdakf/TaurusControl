package tauruscontrol.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tauruscontrol.controller.LoginController;

public class MainView extends BorderPane {

    private final LoginController loginController;
    private ContentArea contentArea;
    private LoginView loginView;
    private PlaybackView playbackView;

    public MainView(Stage stage) {
        this.loginController = new LoginController();

        setPrefSize(1200, 700);
        setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );

        TitleBar titleBar = new TitleBar(stage);
        contentArea = new ContentArea();
        SideBar sideBar = new SideBar(this::onMenuSelected);

        setTop(titleBar);
        setLeft(sideBar);
        setCenter(contentArea);

        BorderPane.setMargin(contentArea, new Insets(40, 40, 40, 40));
    }

    private void onMenuSelected(String menuName) {
        switch (menuName) {
            case "로그인 관리" -> {
                if (loginView == null) {
                    loginView = new LoginView(loginController);
                    loginView.refresh();
                }
                contentArea.setContent(loginView);
            }
            case "재생 관리" -> {
                if (playbackView == null) {
                    playbackView = new PlaybackView(loginController.getTerminalManager());
                }
                playbackView.refresh();
                contentArea.setContent(playbackView);
            }
            case "스케줄 관리" -> contentArea.setContent(new Label("스케줄 관리 화면"));
            case "터미널 설정" -> contentArea.setContent(new Label("터미널 설정 화면"));
        }
    }
}
