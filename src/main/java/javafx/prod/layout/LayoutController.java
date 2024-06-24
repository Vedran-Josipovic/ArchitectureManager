package javafx.prod.layout;

import javafx.fxml.FXMLLoader;
import javafx.prod.HelloApplication;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class LayoutController {
    private static final Logger logger = LoggerFactory.getLogger(LayoutController.class);
    public String css = Objects.requireNonNull(getClass().getResource("/javafx/prod/styles/style.css")).toExternalForm();

    public void showLocationAddScreen() {
        logger.info("Showing location add screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/location/_locationAdd.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Add a location");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
