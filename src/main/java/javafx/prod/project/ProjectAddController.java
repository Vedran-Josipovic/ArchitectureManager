package javafx.prod.project;

import app.prod.enumeration.Status;
import app.prod.exception.ValidationException;
import app.prod.model.ChangeLogEntry;
import app.prod.model.Client;
import app.prod.model.Project;
import app.prod.utils.DatabaseUtils;
import app.prod.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.prod.HelloApplication;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectAddController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectAddController.class);
    private Project projectToEdit;
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
        clientComboBox.getItems().setAll(DatabaseUtils.getClients());
    }

    @FXML
    private void addProject() {
        try {
            validateInputs();
            if (projectToEdit == null) {
                addNewProject();
            } else {
                updateExistingProject();
            }
            JavaFxUtils.clearForm(projectNameTextField, projectDescriptionTextField, startDatePicker, endDatePicker, clientComboBox, statusComboBox);
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Project saved successfully.");
        } catch (ValidationException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while saving project: ", ex);
        }
    }

    private void addNewProject() {
        Project project = new Project(
                projectNameTextField.getText().trim(),
                projectDescriptionTextField.getText().trim(),
                startDatePicker.getValue(),
                endDatePicker.getValue(),
                statusComboBox.getValue(),
                clientComboBox.getValue(),
                null,
                null
        );

        DatabaseUtils.saveProject(project);

        ChangeLogEntry<Project> entry = new ChangeLogEntry<>(
                LocalDateTime.now(),
                "Project",
                "CREATE",
                null,
                project,
                HelloApplication.getUser().getRole()
        );
        FileUtils.logChange(entry);
    }

    private void updateExistingProject() {
        Project oldValue = new Project(
                projectToEdit.getName(),
                projectToEdit.getDescription(),
                projectToEdit.getStartDate(),
                projectToEdit.getDeadline(),
                projectToEdit.getStatus(),
                projectToEdit.getClient(),
                projectToEdit.getTransactions(),
                projectToEdit.getEmployees()
        );

        projectToEdit.setName(projectNameTextField.getText().trim());
        projectToEdit.setDescription(projectDescriptionTextField.getText().trim());
        projectToEdit.setStartDate(startDatePicker.getValue());
        projectToEdit.setDeadline(endDatePicker.getValue());
        projectToEdit.setStatus(statusComboBox.getValue());
        projectToEdit.setClient(clientComboBox.getValue());

        DatabaseUtils.updateProject(projectToEdit);

        ChangeLogEntry<Project> entry = new ChangeLogEntry<>(
                LocalDateTime.now(),
                "Project",
                "UPDATE",
                oldValue,
                projectToEdit,
                HelloApplication.getUser().getRole()
        );
        FileUtils.logChange(entry);
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

    public void setProjectToEdit(Project project) {
        this.projectToEdit = project;
        projectNameTextField.setText(project.getName());
        projectDescriptionTextField.setText(project.getDescription());
        startDatePicker.setValue(project.getStartDate());
        endDatePicker.setValue(project.getDeadline());
        clientComboBox.setValue(project.getClient());
        statusComboBox.setValue(project.getStatus());
    }
}
