package javafx.prod.client;

import app.prod.exception.ValidationException;
import app.prod.model.Client;
import app.prod.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAddController {
    private static final Logger logger = LoggerFactory.getLogger(ClientAddController.class);

    @FXML
    private TextField clientNameTextField;
    @FXML
    private TextField clientEmailTextField;
    @FXML
    private TextField companyNameTextField;

    public void addClient() {
        try {
            validateInputs();
            String name = clientNameTextField.getText().trim();
            String email = clientEmailTextField.getText().trim();
            String companyName = companyNameTextField.getText().trim();

            Client client = new Client(name, email, companyName);

            DatabaseUtils.saveClient(client);

            JavaFxUtils.clearForm(clientNameTextField, clientEmailTextField, companyNameTextField);

            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Client added successfully.");
        } catch (ValidationException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while adding client: " + ex.getMessage());
        }
    }

    private void validateInputs() throws ValidationException {
        if (clientNameTextField.getText().isEmpty() ||
            clientEmailTextField.getText().isEmpty() ||
            companyNameTextField.getText().isEmpty()) {
            throw new ValidationException("Please fill in all required fields.");
        }
        if (!clientEmailTextField.getText().contains("@")) {
            throw new ValidationException("Please provide a valid email address.");
        }
    }
}
