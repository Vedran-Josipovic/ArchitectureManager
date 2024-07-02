package javafx.prod.project;

import app.prod.enumeration.Status;
import app.prod.exception.ValidationException;
import app.prod.model.Client;
import app.prod.model.Project;
import app.prod.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class ProjectAddController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectAddController.class);

    @FXML
    private TextField projectNameTextField;
    @FXML
    private TextField projectDescriptionTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private ComboBox<Status> statusComboBox;

    public void initialize() {
        statusComboBox.getItems().setAll(Status.values());
        List<Client> clients = DatabaseUtils.getClients();
        clientComboBox.getItems().setAll(clients);
    }

    public void addProject() {
        try {
            validateInputs();
            String name = projectNameTextField.getText().trim();
            String description = projectDescriptionTextField.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            Status status = statusComboBox.getValue();
            Client client = clientComboBox.getValue();

            Project project = new Project(name, description, startDate, endDate, status, client, null, null);

            DatabaseUtils.saveProject(project);

            JavaFxUtils.clearForm(projectNameTextField, projectDescriptionTextField, startDatePicker, endDatePicker, statusComboBox, clientComboBox);

            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Project added successfully.");
        } catch (ValidationException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while adding project: " + ex.getMessage());
        }
    }

    private void validateInputs() throws ValidationException {
        if (projectNameTextField.getText().isEmpty() ||
                projectDescriptionTextField.getText().isEmpty() ||
                startDatePicker.getValue() == null ||
                endDatePicker.getValue() == null ||
                statusComboBox.getValue() == null ||
                clientComboBox.getValue() == null) {
            throw new ValidationException("Please fill in all fields.");
        }
        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            throw new ValidationException("Start date cannot be after end date.");
        }
    }
}
