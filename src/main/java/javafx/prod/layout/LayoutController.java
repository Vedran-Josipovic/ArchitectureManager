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
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class LayoutController {
    @FXML
    private Label AccountBalance;
    @FXML
    private MenuItem addLocationMenuItem;
    @FXML
    private MenuItem searchLocationMenuItem;
    @FXML
    private MenuItem addTransactionMenuItem;
    @FXML
    private MenuItem searchTransactionMenuItem;
    @FXML
    private MenuItem addClientMenuItem;
    @FXML
    private MenuItem searchClientMenuItem;
    @FXML
    private MenuItem addProjectMenuItem;
    @FXML
    private MenuItem searchProjectMenuItem;
    @FXML
    private MenuItem addEmployeeMenuItem;
    @FXML
    private MenuItem searchEmployeeMenuItem;
    @FXML
    private MenuItem addMeetingMenuItem;
    @FXML
    private MenuItem searchMeetingMenuItem;
    @FXML
    private MenuItem changeLogMenuItem;


    private static final Logger logger = LoggerFactory.getLogger(LayoutController.class);
    public String css = Objects.requireNonNull(getClass().getResource("/javafx/prod/styles/style.css")).toExternalForm();

    public void initialize() {
        BankAccountThread.startBalanceRefresher();

        String userRole = null;
        try {
            userRole = HelloApplication.getUser().getRole();
        } catch (NullPointerException e) {
            logger.warn("User isn't logged in: [currently null]");
        }
        finally {
            configureMenuItemsForRole(userRole);
            logger.debug("User role: " + userRole);
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            Platform.runLater(() -> {
                logger.debug("Refreshing account balance");
                AccountBalance.setText("Account balance: " + BankAccountThread.getAccountBalance() + " €");
            });
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void configureMenuItemsForRole(String role) {
        if ("USER".equals(role)) {
            addLocationMenuItem.setVisible(true);
            searchLocationMenuItem.setVisible(true);
            addTransactionMenuItem.setVisible(false);
            searchTransactionMenuItem.setVisible(false);
            addClientMenuItem.setVisible(false);
            searchClientMenuItem.setVisible(true);
            addProjectMenuItem.setVisible(false);
            searchProjectMenuItem.setVisible(true);
            addEmployeeMenuItem.setVisible(false);
            searchEmployeeMenuItem.setVisible(true);
            addMeetingMenuItem.setVisible(true);
            searchMeetingMenuItem.setVisible(true);
            changeLogMenuItem.setVisible(false);
        }
        else if ("ADMIN".equals(role)) {
            addLocationMenuItem.setVisible(true);
            searchLocationMenuItem.setVisible(true);
            addTransactionMenuItem.setVisible(true);
            searchTransactionMenuItem.setVisible(true);
            addClientMenuItem.setVisible(true);
            searchClientMenuItem.setVisible(true);
            addProjectMenuItem.setVisible(true);
            searchProjectMenuItem.setVisible(true);
            addEmployeeMenuItem.setVisible(true);
            searchEmployeeMenuItem.setVisible(true);
            addMeetingMenuItem.setVisible(true);
            searchMeetingMenuItem.setVisible(true);
            changeLogMenuItem.setVisible(true);
        }
        else {
            addLocationMenuItem.setVisible(false);
            searchLocationMenuItem.setVisible(false);
            addTransactionMenuItem.setVisible(false);
            searchTransactionMenuItem.setVisible(false);
            addClientMenuItem.setVisible(false);
            searchClientMenuItem.setVisible(false);
            addProjectMenuItem.setVisible(false);
            searchProjectMenuItem.setVisible(false);
            addEmployeeMenuItem.setVisible(false);
            searchEmployeeMenuItem.setVisible(false);
            addMeetingMenuItem.setVisible(false);
            searchMeetingMenuItem.setVisible(false);
            changeLogMenuItem.setVisible(false);
        }
    }

    public void showHomeScreen() {
        logger.info("Showing home screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/hello-view.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Home");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showChangeLogScreen() {
        logger.info("Showing change log screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/changes/changeLog.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Change Log");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void showLocationSearchScreen() {
        logger.info("Showing location search screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/location/_locationSearch.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Search locations");
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
    public void showProjectSearchScreen() {
        logger.info("Showing project search screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/project/projectSearch.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Search projects");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void showProjectAddScreen() {
        logger.info("Showing project add screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/project/projectAdd.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Add a project");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showEmployeeSearchScreen() {
        logger.info("Showing employee search screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/employee/employeeSearch.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Search employees");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showEmployeeAddScreen() {
        logger.info("Showing employee add screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/employee/employeeAdd.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Add an employee");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMeetingAddScreen() {
        logger.info("Showing meeting add screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/meeting/meetingAdd.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Add a meeting");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMeetingSearchScreen() {
        logger.info("Showing meeting search screen");
        FXMLLoader fxmlLoader = new FXMLLoader(javafx.prod.HelloApplication.class.getResource("/javafx/prod/meeting/meetingSearch.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), HelloApplication.width, HelloApplication.height);
            scene.getStylesheets().add(css);
            HelloApplication.getMainStage().setTitle("Search meetings");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
