package tauruscontrol.view;

import javafx.scene.layout.StackPane;

public class ContentArea extends StackPane {

    public ContentArea() {
        setStyle("-fx-background-color: #323232;");
    }

    public void setContent(javafx.scene.Node content) {
        getChildren().clear();
        if (content != null) {
            getChildren().add(content);
        }
    }
}
