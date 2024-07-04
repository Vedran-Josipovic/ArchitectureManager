package javafx.prod.client;

import app.prod.exception.EntityDeleteException;
import app.prod.exception.ValidationException;
import app.prod.model.ChangeLogEntry;
import app.prod.model.Client;
import app.prod.model.Entity;
import app.prod.model.Project;
import app.prod.service.DatabaseService;
import app.prod.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.prod.HelloApplication;
import javafx.prod.utils.JavaFxUtils;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ClientSearchController {
    @FXML
    private TextField clientNameTextField;
    @FXML
    private TextField clientEmailTextField;
    @FXML
    private TextField companyNameTextField;
    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableColumn<Client, String> clientNameColumn;
    @FXML
    private TableColumn<Client, String> clientEmailColumn;
    @FXML
    private TableColumn<Client, String> companyNameColumn;
    @FXML
    private TableColumn<Client, String> clientProjectsColumn;
    @FXML
    private TableColumn<Client, Void> editOrDeleteColumn;

    private final ObservableList<Client> clientList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerFactory.getLogger(ClientSearchController.class);

    public void initialize() {
        clientNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        clientEmailColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getEmail()));
        companyNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getCompanyName()));

        clientProjectsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
                DatabaseService.getProjectsByClientId(param.getValue().getId()).stream()
                .map(Entity::getName)
                .reduce((a, b) -> a + ", " + b).orElse("")));

        List<Client> clients = DatabaseService.getClientsByFilters(new Client());
        clientList.setAll(clients);
        clientTableView.setItems(clientList);

        JavaFxUtils.addButtonToTable(editOrDeleteColumn, this::handleEdit, this::handleDelete);
    }

    @FXML
    public void searchClients() {
        try {
            String name = clientNameTextField.getText().trim();
            String email = clientEmailTextField.getText().trim();
            String companyName = companyNameTextField.getText().trim();

            List<Client> clients = DatabaseService.getClientsByFilters(new Client(name, email, companyName));
            clientList.setAll(clients);
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for clients: " + ex.getMessage());
            logger.error("Error while searching for clients: ", ex);
        }


    }

    public void handleEdit(Client selectedClient) {
        if (selectedClient != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Edit Client", "Are you sure you want to edit this client?");
            if (confirmed) {
                try {
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/javafx/prod/client/clientAdd.fxml"));
                    Scene scene = new Scene(loader.load(), HelloApplication.width, HelloApplication.height);
                    scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/javafx/prod/styles/style.css")).toExternalForm());
                    ClientAddController controller = loader.getController();
                    controller.setClientToEdit(selectedClient);

                    Stage primaryStage = (Stage) clientTableView.getScene().getWindow();
                    primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/javafx/prod/images/icon.png"))));
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Edit Client");
                } catch (IOException e) {
                    logger.error("Error loading the edit client screen", e);
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a client to edit.");
        }
    }

    public void handleDelete(Client selectedClient) {
        if (selectedClient != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Delete Client", "Are you sure you want to delete this client?");
            if (confirmed) {
                try {
                    DatabaseUtils.deleteClient(selectedClient.getId());

                    ChangeLogEntry<Client> entry = new ChangeLogEntry<>(
                            LocalDateTime.now(),
                            "Client",
                            "DELETE",
                            selectedClient,
                            null,
                            HelloApplication.getUser().getRole()
                    );
                    FileUtils.logChange(entry);

                    clientList.remove(selectedClient);
                } catch (EntityDeleteException e) {
                    JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Deletion Error", e.getMessage());
                    logger.warn("Cannot delete client! It is referenced by another entity.");
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a client to delete.");
        }
    }
}
