package javafx.prod.employee;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.prod.utils.DatabaseUtils;
import app.prod.exception.ValidationException;
import app.prod.model.Employee;
import app.prod.model.Project;
import javafx.prod.utils.JavaFxUtils;

import java.util.List;

public class EmployeeAddController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeAddController.class);

    @FXML
    private TextField employeeNameTextField;
    @FXML
    private TextField employeeEmailTextField;
    @FXML
    private TextField employeePositionTextField;
    @FXML
    private ComboBox<Project> projectComboBox;

    public void initialize() {
        List<Project> projects = DatabaseUtils.getProjects();
        projectComboBox.getItems().setAll(projects);
    }

    public void addEmployee() {
        try {
            validateInputs();
            String name = employeeNameTextField.getText().trim();
            String email = employeeEmailTextField.getText().trim();
            String position = employeePositionTextField.getText().trim();
            Project project = projectComboBox.getValue();

            Employee employee = new Employee(name, email, position, project);
            DatabaseUtils.saveEmployee(employee);

            JavaFxUtils.clearForm(employeeNameTextField, employeeEmailTextField, employeePositionTextField, projectComboBox);
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully.");
        } catch (ValidationException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while adding employee: " + ex.getMessage());
        }
    }

    private void validateInputs() throws ValidationException {
        if (employeeNameTextField.getText().isEmpty() ||
                employeeEmailTextField.getText().isEmpty() ||
                employeePositionTextField.getText().isEmpty() ||
                projectComboBox.getValue() == null) {
            throw new ValidationException("Please fill in all fields.");
        }
        if (!employeeEmailTextField.getText().contains("@")) {
            throw new ValidationException("Please provide a valid email address.");
        }
    }
}
