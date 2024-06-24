package javafx.prod.location;

import app.prod.exception.ValidationException;
import app.prod.model.Address;
import app.prod.model.Location;
import app.prod.model.VirtualLocation;
import app.prod.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.prod.HelloApplication;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.prod.utils.*;

public class LocationAddController {
    Logger logger = LoggerFactory.getLogger(LocationAddController.class);


    @FXML
    private ComboBox<String> locationTypeComboBox;

    @FXML
    private Label streetLabel;

    @FXML
    private TextField streetTextField;

    @FXML
    private Label houseNumberLabel;

    @FXML
    private TextField houseNumberTextField;

    @FXML
    private Label cityLabel;

    @FXML
    private TextField cityTextField;

    @FXML
    private Label meetingLinkLabel;

    @FXML
    private TextField meetingLinkTextField;

    @FXML
    private Label platformLabel;

    @FXML
    private TextField platformTextField;

    @FXML
    private void handleLocationTypeChange() {
        String selectedType = locationTypeComboBox.getSelectionModel().getSelectedItem().replace(" ", "");
        boolean isAddress = "Address".equals(selectedType);
        boolean isVirtualLocation = "VirtualLocation".equals(selectedType);

        // Toggle visibility for Address fields
        streetLabel.setVisible(isAddress);
        streetTextField.setVisible(isAddress);
        houseNumberLabel.setVisible(isAddress);
        houseNumberTextField.setVisible(isAddress);
        cityLabel.setVisible(isAddress);
        cityTextField.setVisible(isAddress);

        // Toggle visibility for VirtualLocation fields
        meetingLinkLabel.setVisible(isVirtualLocation);
        meetingLinkTextField.setVisible(isVirtualLocation);
        platformLabel.setVisible(isVirtualLocation);
        platformTextField.setVisible(isVirtualLocation);
    }

    @FXML
    private void addLocation() {
        try {
            String selectedType = locationTypeComboBox.getSelectionModel().getSelectedItem().replace(" ", "");
            validateInputs(selectedType);
            Location location = null;
            if ("Address".equals(selectedType)) location = createAddress();
            else if ("VirtualLocation".equals(selectedType)) location = createVirtualLocation();
            if (location != null) {
                DatabaseUtils.saveLocation(location, selectedType);
                JavaFxUtils.clearForm(streetTextField, houseNumberTextField, cityTextField, meetingLinkTextField, platformTextField);
                JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Location added successfully.");
            }
        } catch (ValidationException e) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
            logger.warn(e.getMessage());
        } catch (NullPointerException e) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select the location type.");
            logger.warn("Please select the location type.");
        }
    }

    private Address createAddress() {
        String street = streetTextField.getText().trim();
        String houseNumber = houseNumberTextField.getText().trim();
        String city = cityTextField.getText().trim();
        return new Address(street, houseNumber, city);
    }

    private VirtualLocation createVirtualLocation() {
        String meetingLink = meetingLinkTextField.getText().trim();
        String platform = platformTextField.getText().trim();
        return new VirtualLocation(meetingLink, platform);
    }

    private void validateInputs(String selectedType) throws ValidationException {
        if ("Address".equals(selectedType)) {
            if (streetTextField.getText().isEmpty() || houseNumberTextField.getText().isEmpty() || cityTextField.getText().isEmpty()) {
                throw new ValidationException("Please fill in all fields.");
            }
        } else if ("VirtualLocation".equals(selectedType)) {
            if (meetingLinkTextField.getText().isEmpty() || platformTextField.getText().isEmpty()) {
                throw new ValidationException("Please fill in all fields.");
            }
        }
    }


}
