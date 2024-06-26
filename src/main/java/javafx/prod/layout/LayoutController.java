package javafx.prod.layout;

import app.prod.thread.BankAccountThread;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.prod.HelloApplication;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class LayoutController {
    @FXML
    private Label AccountBalance;

    private static final Logger logger = LoggerFactory.getLogger(LayoutController.class);
    public String css = Objects.requireNonNull(getClass().getResource("/javafx/prod/styles/style.css")).toExternalForm();

    public void initialize() {
        BankAccountThread.startBalanceRefresher();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            Platform.runLater(() -> {
                AccountBalance.setText("Account balance: " + BankAccountThread.getAccountBalance() + " €");
            });
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

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

    public void showTransactionAddScreen() {
        logger.info("Showing transaction add screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/transaction/transactionAdd.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Add a transaction");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showTransactionSearchScreen() {
        logger.info("Showing transaction search screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/transaction/transactionSearch.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Search transactions");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showClientSearchScreen() {
        logger.info("Showing client search screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/client/clientSearch.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Search clients");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showClientAddScreen() {
        logger.info("Showing client add screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/client/clientAdd.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Add a client");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
