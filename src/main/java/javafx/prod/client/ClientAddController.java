package javafx.prod.client;

import app.prod.exception.ValidationException;
import app.prod.model.ChangeLogEntry;
import app.prod.model.Client;
import app.prod.utils.DatabaseUtils;
import app.prod.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.prod.HelloApplication;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ClientAddController {
    private static final Logger logger = LoggerFactory.getLogger(ClientAddController.class);
    private Client clientToEdit;
    @FXML
    private TextField clientNameTextField;
    @FXML
    private TextField clientEmailTextField;
    @FXML
    private TextField companyNameTextField;

    @FXML
    private void addClient() {
        try {
            validateInputs();
            if (clientToEdit == null) {
                addNewClient();
            } else {
                updateExistingClient();
            }
            JavaFxUtils.clearForm(clientNameTextField, clientEmailTextField, companyNameTextField);
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Client saved successfully.");
        } catch (ValidationException e) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
            logger.warn(e.getMessage());
        } catch (NullPointerException e) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            logger.warn("Please fill in all fields.");
        }
    }

    private void addNewClient() {
        Client client = new Client(clientNameTextField.getText().trim(), clientEmailTextField.getText().trim(), companyNameTextField.getText().trim());
        DatabaseUtils.saveClient(client);

        ChangeLogEntry<Client> entry = new ChangeLogEntry<>(
                LocalDateTime.now(),
                "Client",
                "CREATE",
                null,
                client,
                HelloApplication.getUser().getRole()
        );
        FileUtils.logChange(entry);
    }

    private void updateExistingClient() {
        logger.info("Client before update: " + clientToEdit);

        Client oldValue = new Client(clientToEdit.getName(), clientToEdit.getEmail(), clientToEdit.getCompanyName());

        clientToEdit.setName(clientNameTextField.getText().trim());
        clientToEdit.setEmail(clientEmailTextField.getText().trim());
        clientToEdit.setCompanyName(companyNameTextField.getText().trim());
        DatabaseUtils.updateClient(clientToEdit);

        ChangeLogEntry<Client> entry = new ChangeLogEntry<>(
                LocalDateTime.now(),
                "Client",
                "UPDATE",
                oldValue,
                clientToEdit,
                HelloApplication.getUser().getRole()
        );
        FileUtils.logChange(entry);

        logger.info("Client after update: " + clientToEdit);
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

    public void setClientToEdit(Client client) {
        this.clientToEdit = client;
        clientNameTextField.setText(client.getName());
        clientEmailTextField.setText(client.getEmail());
        companyNameTextField.setText(client.getCompanyName());
    }
}
