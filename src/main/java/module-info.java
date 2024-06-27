module javafx.prod.architecturemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires org.slf4j;
    requires java.sql;

    opens javafx.prod to javafx.fxml;
    exports javafx.prod;

    exports javafx.prod.layout to javafx.fxml;
    opens javafx.prod.layout to javafx.fxml;

    exports javafx.prod.location to javafx.fxml;
    opens javafx.prod.location to javafx.fxml;

    exports javafx.prod.transaction to javafx.fxml;
    opens javafx.prod.transaction to javafx.fxml;

    exports javafx.prod.task to javafx.fxml;
    opens javafx.prod.task to javafx.fxml;

    exports javafx.prod.client to javafx.fxml;
    opens javafx.prod.client to javafx.fxml;

    exports javafx.prod.project to javafx.fxml;
    opens javafx.prod.project to javafx.fxml;


}
