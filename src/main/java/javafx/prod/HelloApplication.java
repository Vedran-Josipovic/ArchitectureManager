package javafx.prod;

import app.prod.utils.DatabaseUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class HelloApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);
    public static Stage mainStage;
    public static final int width = 1875, height = 1250;

    public String css = Objects.requireNonNull(getClass().getResource("/javafx/prod/styles/style.css")).toExternalForm();


    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add(css);

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/javafx/prod/images/icon.png"))));

        stage.setTitle("Upravljanje projektima");
        stage.setScene(scene);

        stage.show();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        logger.info("Launching JavaFX application");

        try(Connection connection = DatabaseUtils.connectToDatabase()) {
            logger.info("Connected to database: {}", connection.getMetaData().getURL());
        } catch (SQLException | IOException e) {
            logger.error("Failed to connect to database", e);
        }

        launch();
    }
}
