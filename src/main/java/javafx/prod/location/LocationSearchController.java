package javafx.prod.location;

import app.prod.model.Address;
import app.prod.model.Location;
import app.prod.model.Project;
import app.prod.model.VirtualLocation;
import app.prod.service.DatabaseService;
import app.prod.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    public void initialize() {
        List<Location> locations = DatabaseUtils.getLocations();
        locationList.setAll(locations);
        locationTableView.setItems(locationList);

        fullLocationDetailsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFullLocationDetails()));
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

        locationTableView.setItems(FXCollections.observableArrayList(locations));
    }
}
