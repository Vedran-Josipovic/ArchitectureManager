package javafx.prod.project;

import app.prod.enumeration.Status;
import app.prod.model.Client;
import app.prod.model.Project;
import app.prod.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProjectSearchController {

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
    @FXML
    private TableView<Project> projectTableView;
    @FXML
    private TableColumn<Project, String> projectNameColumn;
    @FXML
    private TableColumn<Project, String> projectDescriptionColumn;
    @FXML
    private TableColumn<Project, String> projectClientColumn;
    @FXML
    private TableColumn<Project, String> projectStartDateColumn;
    @FXML
    private TableColumn<Project, String> projectEndDateColumn;
    @FXML
    private TableColumn<Project, String> projectStatusColumn;

    private final ObservableList<Project> projectList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerFactory.getLogger(ProjectSearchController.class);

    public void initialize() {
        projectNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        projectDescriptionColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDescription()));
        projectClientColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getClient().toString()));
        projectStartDateColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd. MMM yyyy."))));
        projectEndDateColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDeadline().format(DateTimeFormatter.ofPattern("dd. MMM yyyy."))));
        projectStatusColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getStatus().toString()));

        List<Project> projects = DatabaseUtils.getProjectsByFilters(new Project());
        projectList.setAll(projects);
        projectTableView.setItems(projectList);

        List<Client> clients = DatabaseUtils.getClients();
        clientComboBox.setItems(FXCollections.observableArrayList(clients));

        statusComboBox.setItems(FXCollections.observableArrayList(Status.values()));
    }

    @FXML
    public void searchProjects() {
        try {
            String name = projectNameTextField.getText().trim();
            String description = projectDescriptionTextField.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            Client client = clientComboBox.getValue();
            Status status = statusComboBox.getValue();

            Project filter = new Project(name, description, startDate, endDate, status, client, null, null, null);
            List<Project> projects = DatabaseUtils.getProjectsByFilters(filter);
            projectList.setAll(projects);
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for projects: " + ex.getMessage());
            logger.error("Error while searching for projects: ", ex);
        }
    }
}
