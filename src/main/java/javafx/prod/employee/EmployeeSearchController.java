package javafx.prod.employee;
import app.prod.exception.EntityDeleteException;
import app.prod.model.ChangeLogEntry;
import app.prod.service.DatabaseService;
import app.prod.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.prod.HelloApplication;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.prod.utils.DatabaseUtils;
import app.prod.model.Employee;
import javafx.prod.utils.JavaFxUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EmployeeSearchController {
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    private TextField employeeNameTextField;
    @FXML
    private TextField employeeEmailTextField;
    @FXML
    private TextField employeePositionTextField;
    @FXML
    private TableView<Employee> employeeTableView;
    @FXML
    private TableColumn<Employee, String> employeeNameColumn;
    @FXML
    private TableColumn<Employee, String> employeeEmailColumn;
    @FXML
    private TableColumn<Employee, String> employeePositionColumn;
    @FXML
    private TableColumn<Employee, String> employeeProjectColumn;
    @FXML
    private TableColumn<Employee, Void> editOrDeleteColumn;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSearchController.class);

    public void initialize() {
        employeeNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        employeeEmailColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getEmail()));
        employeePositionColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getPosition()));
        employeeProjectColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getProject().getName()));

        List<Employee> employees = DatabaseService.getEmployeesByFilters(new Employee());
        employeeList.setAll(employees);
        employeeTableView.setItems(employeeList);

        JavaFxUtils.addButtonToTable(editOrDeleteColumn, this::handleEdit, this::handleDelete);
    }

    @FXML
    public void searchEmployees() {
        try {
            String name = employeeNameTextField.getText().trim();
            String email = employeeEmailTextField.getText().trim();
            String position = employeePositionTextField.getText().trim();

            Employee filter = new Employee();
            filter.setName(name);
            filter.setEmail(email);
            filter.setPosition(position);

            List<Employee> employees = DatabaseService.getEmployeesByFilters(filter);
            employeeList.setAll(employees);
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for employees: " + ex.getMessage());
            logger.error("Error while searching for employees: ", ex);
        }
    }

    private void handleEdit(Employee selectedEmployee) {
        if (selectedEmployee != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Edit Employee", "Are you sure you want to edit this employee?");
            if (confirmed) {
                try {
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/javafx/prod/employee/employeeAdd.fxml"));
                    Scene scene = new Scene(loader.load(), HelloApplication.width, HelloApplication.height);
                    scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/javafx/prod/styles/style.css")).toExternalForm());
                    EmployeeAddController controller = loader.getController();
                    controller.setEmployeeToEdit(selectedEmployee);

                    Stage primaryStage = (Stage) employeeTableView.getScene().getWindow();
                    primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/javafx/prod/images/icon.png"))));
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Edit Employee");
                } catch (IOException e) {
                    logger.error("Error loading the edit employee screen", e);
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to edit.");
        }
    }

    private void handleDelete(Employee selectedEmployee) {
        if (selectedEmployee != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Delete Employee", "Are you sure you want to delete this employee?");
            if (confirmed) {
                try {
                    DatabaseUtils.deleteEmployee(selectedEmployee.getId());

                    ChangeLogEntry<Employee> entry = new ChangeLogEntry<>(
                            LocalDateTime.now(),
                            "Employee",
                            "DELETE",
                            selectedEmployee,
                            null,
                            HelloApplication.getUser().getRole()
                    );
                    FileUtils.logChange(entry);

                    employeeList.remove(selectedEmployee);
                } catch (EntityDeleteException e) {
                    JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Deletion Error", e.getMessage());
                    logger.warn("Cannot delete employee! It is referenced by another entity.");
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to delete.");
        }
    }

}
