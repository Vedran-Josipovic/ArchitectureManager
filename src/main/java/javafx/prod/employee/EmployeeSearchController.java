package javafx.prod.employee;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.prod.utils.DatabaseUtils;
import app.prod.model.Employee;
import javafx.prod.utils.JavaFxUtils;

import java.util.List;
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

    private static final Logger logger = LoggerFactory.getLogger(EmployeeSearchController.class);

    public void initialize() {
        employeeNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        employeeEmailColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getEmail()));
        employeePositionColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getPosition()));
        employeeProjectColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getProject().getName()));

        List<Employee> employees = DatabaseUtils.getEmployeesByFilters(new Employee());
        employeeList.setAll(employees);
        employeeTableView.setItems(employeeList);
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

            List<Employee> employees = DatabaseUtils.getEmployeesByFilters(filter);
            employeeList.setAll(employees);
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for employees: " + ex.getMessage());
            logger.error("Error while searching for employees: ", ex);
        }
    }
}
