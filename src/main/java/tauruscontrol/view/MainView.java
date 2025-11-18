package tauruscontrol.view;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainView extends BorderPane {

    private ContentArea contentArea;

    public MainView(Stage stage) {
        setPrefSize(1200, 700);
        setStyle(
                "-fx-background-color: #5a5a5a;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );

        TitleBar titleBar = new TitleBar(stage);
        SideBar sideBar = new SideBar(this::onMenuSelected);
        contentArea = new ContentArea();

        setTop(titleBar);
        setLeft(sideBar);
        setCenter(contentArea);

        BorderPane.setMargin(contentArea, new Insets(40, 40, 40, 40));
    }

    private void onMenuSelected(String menuName) {
        // 나중에 메뉴별로 다른 View 표시
        System.out.println("선택된 메뉴: " + menuName);
    }
}