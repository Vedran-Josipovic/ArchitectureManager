package javafx.prod;

import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Label;
import app.prod.model.User;
import app.prod.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button actionButton;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private Label roleLabel;

    private boolean isLoginMode = true;

    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("USER", "ADMIN"));
    }

    @FXML
    private void switchMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            actionButton.setText("Login");
            confirmPasswordField.setVisible(false);
            confirmPasswordLabel.setVisible(false);
            roleComboBox.setVisible(false);
            roleLabel.setVisible(false);
        } else {
            actionButton.setText("Register");
            confirmPasswordField.setVisible(true);
            confirmPasswordLabel.setVisible(true);
            roleComboBox.setVisible(true);
            roleLabel.setVisible(true);
        }
    }

    @FXML
    private void handleAction() {
        if (isLoginMode) {
            handleLogin();
        } else {
            handleRegister();
        }
    }

    private void handleLogin() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String hashedPassword = FileUtils.hashPassword(password);

        List<User> users = FileUtils.getUsersFromFile();
        boolean isAuthenticated = users.stream()
                .anyMatch(user -> user.getUsername().equals(username) && user.getPassword().equals(hashedPassword));

        if (isAuthenticated) {
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + username + "!");
            logger.info("User {} logged in.", username);
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            logger.error("Failed login attempt for user {}.", username);
        }
    }

    private void handleRegister() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        if (!password.equals(confirmPassword)) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Registration Failed", "Passwords do not match.");
            return;
        }

        String hashedPassword = FileUtils.hashPassword(password);
        FileUtils.addUserToFile(username, hashedPassword, role);
        JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User " + username + " registered successfully.");
        switchMode();
    }
}
