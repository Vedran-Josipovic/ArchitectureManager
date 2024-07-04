package javafx.prod.employee;

import app.prod.exception.EntityEditException;
import app.prod.model.ChangeLogEntry;
import app.prod.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.prod.HelloApplication;
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

import java.time.LocalDateTime;
import java.util.List;

public class EmployeeAddController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeAddController.class);
    private Employee employeeToEdit;
    @FXML
    private TextField employeeNameTextField;
    @FXML
    private TextField employeeEmailTextField;
    @FXML
    private TextField employeePositionTextField;
    @FXML
    private ComboBox<Project> projectComboBox;

    public void initialize() {
        projectComboBox.getItems().setAll(DatabaseUtils.getProjects());
    }

    @FXML
    private void addEmployee() {
        try {
            validateInputs();
            if (employeeToEdit == null) {
                addNewEmployee();
            } else {
                updateExistingEmployee();
            }
            JavaFxUtils.clearForm(employeeNameTextField, employeeEmailTextField, employeePositionTextField, projectComboBox);
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Employee saved successfully.");
        } catch (ValidationException | EntityEditException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while saving employee: ", ex);
        }
    }

    private void addNewEmployee() {
        Employee employee = new Employee(
                employeeNameTextField.getText().trim(),
                employeeEmailTextField.getText().trim(),
                employeePositionTextField.getText().trim(),
                projectComboBox.getValue()
        );

        DatabaseUtils.saveEmployee(employee);

        ChangeLogEntry<Employee> entry = new ChangeLogEntry<>(
                LocalDateTime.now(),
                "Employee",
                "CREATE",
                null,
                employee,
                HelloApplication.getUser().getRole()
        );
        FileUtils.logChange(entry);
    }

    private void updateExistingEmployee() throws EntityEditException {
        Employee oldValue = new Employee(
                employeeToEdit.getName(),
                employeeToEdit.getEmail(),
                employeeToEdit.getPosition(),
                employeeToEdit.getProject()
        );

        employeeToEdit.setName(employeeNameTextField.getText().trim());
        employeeToEdit.setEmail(employeeEmailTextField.getText().trim());
        employeeToEdit.setPosition(employeePositionTextField.getText().trim());
        employeeToEdit.setProject(projectComboBox.getValue());

        DatabaseUtils.updateEmployee(employeeToEdit);

        ChangeLogEntry<Employee> entry = new ChangeLogEntry<>(
                LocalDateTime.now(),
                "Employee",
                "UPDATE",
                oldValue,
                employeeToEdit,
                HelloApplication.getUser().getRole()
        );
        FileUtils.logChange(entry);
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

    public void setEmployeeToEdit(Employee employee) {
        this.employeeToEdit = employee;
        employeeNameTextField.setText(employee.getName());
        employeeEmailTextField.setText(employee.getEmail());
        employeePositionTextField.setText(employee.getPosition());
        projectComboBox.setValue(employee.getProject());
    }
}
