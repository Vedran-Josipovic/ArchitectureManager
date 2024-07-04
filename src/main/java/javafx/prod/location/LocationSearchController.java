package javafx.prod.location;

import app.prod.exception.EntityDeleteException;
import app.prod.model.*;
import app.prod.service.DatabaseService;
import app.prod.utils.DatabaseUtils;
import app.prod.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.prod.HelloApplication;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class LocationSearchController {

    private static final Logger logger = LoggerFactory.getLogger(LocationSearchController.class);
    private final ObservableList<Location> locationList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TableView<Location> locationTableView;
    @FXML
    private TableColumn<Location, String> fullLocationDetailsColumn;

    @FXML
    private TableColumn<Location, Void> editOrDeleteColumn;
    @FXML
    public void initialize() {
        List<Location> locations = DatabaseUtils.getLocations();
        locations = DatabaseService.sortLocations(locations);

        locationList.setAll(locations);
        locationTableView.setItems(locationList);

        fullLocationDetailsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFullLocationDetails()));

        JavaFxUtils.addButtonToTable(editOrDeleteColumn, this::handleEdit, this::handleDelete);
    }

    @FXML
    public void handleSearch() {
        String selectedType = typeComboBox.getValue();
        List<Location> locations;

        if ("Address".equals(selectedType)) {
            locations = DatabaseService.getLocationsByType("Address");
        } else if ("VirtualLocation".equals(selectedType)) {
            locations = DatabaseService.getLocationsByType("VirtualLocation");
        } else {
            locations = DatabaseUtils.getLocations();
        }

        locations = DatabaseService.sortLocations(locations);

        locationTableView.setItems(FXCollections.observableArrayList(locations));
    }

    /*
    private void addButtonToTable() {
        Callback<TableColumn<Location, Void>, TableCell<Location, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Location, Void> call(final TableColumn<Location, Void> param) {
                final TableCell<Location, Void> cell = new TableCell<>() {

                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");
                    {
                        btnEdit.setOnAction((event) -> {
                            Location location = getTableView().getItems().get(getIndex());
                            handleEdit(location);
                        });
                        btnDelete.setOnAction((event) -> {
                            Location location = getTableView().getItems().get(getIndex());
                            handleDelete(location);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox pane = new HBox(btnEdit, btnDelete);
                            pane.setAlignment(Pos.CENTER);
                            pane.setSpacing(10);
                            pane.setPadding(new Insets(0, 5, 0, 5));
                            if (HelloApplication.isAdmin()) {
                                btnEdit.setDisable(false);
                                btnDelete.setDisable(false);
                            } else {
                                btnEdit.setDisable(true);
                                btnDelete.setDisable(true);
                                btnEdit.setStyle("-fx-opacity: 0.5;");
                                btnDelete.setStyle("-fx-opacity: 0.5;");
                            }
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        };
        editOrDeleteColumn.setCellFactory(cellFactory);
    }
     */
    public void handleEdit(Location selectedLocation) {
        if (selectedLocation != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Edit Location", "Are you sure you want to edit this location?");
            if (confirmed){
                try {
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/javafx/prod/location/_locationAdd.fxml"));
                    Scene scene = new Scene(loader.load(), HelloApplication.width, HelloApplication.height);
                    scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/javafx/prod/styles/style.css")).toExternalForm());
                    LocationAddController controller = loader.getController();
                    controller.setLocationToEdit(selectedLocation);

                    Stage primaryStage = (Stage) locationTableView.getScene().getWindow();
                    primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/javafx/prod/images/icon.png"))));
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Edit Location");
                } catch (IOException e) {
                    logger.error("Error loading the edit location screen", e);
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a location to edit.");
        }
    }


    public void handleDelete(Location selectedLocation) {
        if (selectedLocation != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Delete Location", "Are you sure you want to delete this location?");
            if (confirmed) {
                try {
                    DatabaseUtils.deleteLocation(selectedLocation);

                    ChangeLogEntry<Location> entry = new ChangeLogEntry<>(
                            LocalDateTime.now(),
                            "Location",
                            "DELETE",
                            selectedLocation,
                            null,
                            HelloApplication.getUser().getRole()
                    );
                    FileUtils.logChange(entry);

                } catch (EntityDeleteException e) {
                    JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                    logger.error("Cannot delete location! It is referenced by another entity.");
                } finally {
                    refreshTable();
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a location to delete.");
        }
    }

    private void refreshTable() {
        List<Location> locations = DatabaseUtils.getLocations();
        locationTableView.setItems(FXCollections.observableArrayList(locations));
    }
}
