package javafx.prod;

import app.prod.utils.DatabaseUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HelloApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        logger.info("Launching JavaFX application");


        logger.info("Connecting to database");
        try(Connection connection = DatabaseUtils.connectToDatabase()) {
            logger.info("Connected to database");
            logger.info(connection.getMetaData().getURL());
        } catch (SQLException | IOException e) {
            logger.error("Failed to connect to database", e);
        }








        launch();
    }
}
