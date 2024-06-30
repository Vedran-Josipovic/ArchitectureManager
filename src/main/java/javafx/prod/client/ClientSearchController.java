package javafx.prod.client;

import app.prod.exception.ValidationException;
import app.prod.model.Client;
import app.prod.model.Entity;
import app.prod.model.Project;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.prod.utils.DatabaseUtils;

import java.util.List;

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

    private final ObservableList<Client> clientList = FXCollections.observableArrayList();
    private static final Logger logger = LoggerFactory.getLogger(ClientSearchController.class);

    public void initialize() {
        clientNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        clientEmailColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getEmail()));
        companyNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getCompanyName()));

        clientProjectsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
                DatabaseUtils.getProjectsByClientId(param.getValue().getId()).stream()
                .map(Entity::getName)
                .reduce((a, b) -> a + ", " + b).orElse("")));

        List<Client> clients = DatabaseUtils.getClientsByFilters(new Client());
        clientList.setAll(clients);

        clientTableView.setItems(clientList);
    }

    @FXML
    public void searchClients() {
        try {
            String name = clientNameTextField.getText().trim();
            String email = clientEmailTextField.getText().trim();
            String companyName = companyNameTextField.getText().trim();

            List<Client> clients = DatabaseUtils.getClientsByFilters(new Client(name, email, companyName));
            clientList.setAll(clients);
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for clients: " + ex.getMessage());
            logger.error("Error while searching for clients: ", ex);
        }


    }

}
