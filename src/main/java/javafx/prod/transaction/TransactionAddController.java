package javafx.prod.transaction;

import app.prod.enumeration.TransactionType;
import app.prod.exception.TransactionAmountException;
import app.prod.exception.ValidationException;
import app.prod.exception.EntityInitializationException;
import app.prod.model.Project;
import app.prod.model.Transaction;
import app.prod.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionAddController {
    Logger logger = LoggerFactory.getLogger(TransactionAddController.class);

    @FXML
    private TextField transactionNameTextField;
    @FXML
    private ComboBox<TransactionType> transactionTypeComboBox;
    @FXML
    private TextField amountTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private ComboBox<Project> projectComboBox;

    public void initialize() {
        transactionTypeComboBox.getItems().setAll(TransactionType.values());

        List<Project> projects = DatabaseUtils.getProjects();
        projectComboBox.getItems().setAll(projects);
    }

    public void addTransaction() {
        try {
            validateInputs();
            String name = transactionNameTextField.getText().trim();
            TransactionType transactionType = transactionTypeComboBox.getValue();
            BigDecimal amount = new BigDecimal(amountTextField.getText().trim());
            String description = descriptionTextField.getText().trim();
            LocalDate date = dateDatePicker.getValue();
            Project selectedProject = projectComboBox.getValue();

            Transaction transaction = new Transaction.Builder()
                    .withName(name)
                    .withTransactionType(transactionType)
                    .withAmount(amount)
                    .withDescription(description)
                    .withDate(date)
                    .withProject(selectedProject)
                    .build();

            DatabaseUtils.saveTransaction(transaction);

            JavaFxUtils.clearForm(transactionNameTextField, transactionTypeComboBox, amountTextField, descriptionTextField, dateDatePicker, projectComboBox);

            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Transaction added successfully.");
        } catch (TransactionAmountException | EntityInitializationException | ValidationException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while adding transaction: " + ex.getMessage());
        }
    }

    private void validateInputs() throws ValidationException {
        if (transactionNameTextField.getText().isEmpty() ||
                transactionTypeComboBox.getValue() == null ||
                amountTextField.getText().isEmpty() ||
                descriptionTextField.getText().isEmpty() ||
                dateDatePicker.getValue() == null ||
                projectComboBox.getValue() == null) {
            throw new ValidationException("Please fill in all fields.");
        }

        try {
            new BigDecimal(amountTextField.getText());
        } catch (NumberFormatException e) {
            throw new ValidationException("Amount must be a valid number.");
        }
    }
}
