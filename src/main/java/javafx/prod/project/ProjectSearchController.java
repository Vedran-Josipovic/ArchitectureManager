package javafx.prod.project;

import app.prod.enumeration.Status;
import app.prod.exception.EntityDeleteException;
import app.prod.model.ChangeLogEntry;
import app.prod.model.Client;
import app.prod.model.Entity;
import app.prod.model.Project;
import app.prod.service.DatabaseService;
import app.prod.thread.ProjectBalanceThread;
import app.prod.utils.DatabaseUtils;
import app.prod.utils.FileUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.prod.HelloApplication;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
    @FXML
    private TableColumn<Project, String> projectTransactionsColumn;
    @FXML
    private TableColumn<Project, String> projectValueColumn;
    @FXML
    private TableColumn<Project, String> estimatedProgressColumn;
    @FXML
    private TableColumn<Project, String> projectEmployeesColumn;
    @FXML
    private TableColumn<Project, Void> editOrDeleteColumn;

    private final ObservableList<Project> projectList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerFactory.getLogger(ProjectSearchController.class);

    public void initialize() {
        projectNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        projectDescriptionColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDescription()));
        projectClientColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getClient().toString()));
        projectStartDateColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd. MMM yyyy."))));
        projectEndDateColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDeadline().format(DateTimeFormatter.ofPattern("dd. MMM yyyy."))));
        projectStatusColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getStatus().toString()));
        projectTransactionsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTransactions().stream()
                .map(transaction -> transaction.getName() + "[" + transaction.getTransactionType().getName()+ " - " + transaction.getAmount() + "]")
                .reduce((a, b) -> a + ", " + b).orElse("")));

        projectValueColumn.setCellValueFactory(param -> {
            BigDecimal balance = ProjectBalanceThread.getProjectBalance(param.getValue().getId());
            return new ReadOnlyStringWrapper(balance.toString() + " €");
        });

        projectEmployeesColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
                DatabaseUtils.getEmployeesByProjectId(param.getValue().getId()).stream()
                        .map(e -> e.getName() + " [" + e.getPosition() + "]" )
                        .reduce((a, b) -> a + ", " + b).orElse("")));

        estimatedProgressColumn.setCellValueFactory(param -> {
            if(param.getValue().isCompleted()) {
                return new ReadOnlyStringWrapper("100.00%");
            }
            else if (param.getValue().getStatus() == Status.TO_DO) {
                return new ReadOnlyStringWrapper("0.00%");
            }
            else {
                double progress = param.getValue().getExpectedProgress();
                return new ReadOnlyStringWrapper(String.format("%.2f", progress) + "%");
            }
        });



        List<Project> projects = DatabaseService.getProjectsByFilters(new Project());
        projects = DatabaseService.sortProjects(projects);

        projectList.setAll(projects);
        projectTableView.setItems(projectList);

        clientComboBox.setItems(FXCollections.observableArrayList(DatabaseUtils.getClients()));
        statusComboBox.setItems(FXCollections.observableArrayList(Status.values()));

        JavaFxUtils.addButtonToTable(editOrDeleteColumn, this::handleEdit, this::handleDelete);

        ProjectBalanceThread.startBalanceRefresher();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            Platform.runLater(() -> {
                logger.debug("Refreshing project table view");
                projectTableView.refresh();
            });
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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

            Project filter = new Project(name, description, startDate, endDate, status, client, null, null);
            List<Project> projects = DatabaseService.getProjectsByFilters(filter);
            projects = DatabaseService.sortProjects(projects);
            projectList.setAll(projects);
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for projects: " + ex.getMessage());
            logger.error("Error while searching for projects: ", ex);
        }
    }

    private void handleEdit(Project selectedProject) {
        if (selectedProject != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Edit Project", "Are you sure you want to edit this project?");
            if (confirmed) {
                try {
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/javafx/prod/project/projectAdd.fxml"));
                    Scene scene = new Scene(loader.load(), HelloApplication.width, HelloApplication.height);
                    scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/javafx/prod/styles/style.css")).toExternalForm());
                    ProjectAddController controller = loader.getController();
                    controller.setProjectToEdit(selectedProject);

                    Stage primaryStage = (Stage) projectTableView.getScene().getWindow();
                    primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/javafx/prod/images/icon.png"))));
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Edit Project");
                } catch (IOException e) {
                    logger.error("Error loading the edit project screen", e);
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a project to edit.");
        }
    }

    private void handleDelete(Project selectedProject) {
        if (selectedProject != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Delete Project", "Are you sure you want to delete this project?");
            if (confirmed) {
                try {
                    DatabaseUtils.deleteProject(selectedProject.getId());

                    ChangeLogEntry<Project> entry = new ChangeLogEntry<>(
                            LocalDateTime.now(),
                            "Project",
                            "DELETE",
                            selectedProject,
                            null,
                            HelloApplication.getUser().getRole()
                    );
                    FileUtils.logChange(entry);

                    projectList.remove(selectedProject);
                } catch (EntityDeleteException e) {
                    JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Deletion Error", e.getMessage());
                    logger.warn("Cannot delete project! It is referenced by another entity.");
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a project to delete.");
        }
    }
}
