module tauruscontrol {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires org.json;
    requires com.sun.jna;
    requires com.sun.jna.platform;

    opens tauruscontrol to javafx.fxml, javafx.graphics;
    opens tauruscontrol.view to javafx.fxml, javafx.graphics;
    opens tauruscontrol.view.components to javafx.fxml, javafx.graphics;

    opens tauruscontrol.domain.terminal to org.json;
    opens tauruscontrol.domain.media to org.json;

    opens tauruscontrol.sdk to com.sun.jna;

    exports tauruscontrol;
}