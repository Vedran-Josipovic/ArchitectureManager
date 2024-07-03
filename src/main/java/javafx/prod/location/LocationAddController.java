package javafx.prod.location;

import app.prod.exception.EntityEditException;
import app.prod.exception.ValidationException;
import app.prod.model.Address;
import app.prod.model.Location;
import app.prod.model.VirtualLocation;
import app.prod.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationAddController {
    Logger logger = LoggerFactory.getLogger(LocationAddController.class);
    private Location locationToEdit;

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

        streetLabel.setVisible(isAddress);
        streetTextField.setVisible(isAddress);
        houseNumberLabel.setVisible(isAddress);
        houseNumberTextField.setVisible(isAddress);
        cityLabel.setVisible(isAddress);
        cityTextField.setVisible(isAddress);

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
            if (locationToEdit == null) {
                addNewLocation(selectedType);
            } else {
                updateExistingLocation(selectedType);
            }
            JavaFxUtils.clearForm(streetTextField, houseNumberTextField, cityTextField, meetingLinkTextField, platformTextField);
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Location saved successfully.");
        } catch (ValidationException | EntityEditException e) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
            logger.warn(e.getMessage());
        } catch (NullPointerException e) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select the location type.");
            logger.warn("Please select the location type.");
        }
    }

    private void addNewLocation(String selectedType) {
        Location location = null;
        if ("Address".equals(selectedType)) {
            location = new Address(streetTextField.getText().trim(), houseNumberTextField.getText().trim(), cityTextField.getText().trim());
        } else if ("VirtualLocation".equals(selectedType)) {
            location = new VirtualLocation(meetingLinkTextField.getText().trim(), platformTextField.getText().trim());
        }
        if (location != null) {
            DatabaseUtils.saveLocation(location, selectedType);
        }
    }

    private void updateExistingLocation(String selectedType) throws EntityEditException {
        logger.info("Location before update: " + locationToEdit);
        if ("Address".equals(selectedType) && locationToEdit instanceof Address address) {
            address.setStreet(streetTextField.getText().trim());
            address.setHouseNumber(houseNumberTextField.getText().trim());
            address.setCity(cityTextField.getText().trim());
            DatabaseUtils.updateLocation(address);
            logger.info("Location after update: " + address);
        } else if ("VirtualLocation".equals(selectedType) && locationToEdit instanceof VirtualLocation virtualLocation) {
            virtualLocation.setMeetingLink(meetingLinkTextField.getText().trim());
            virtualLocation.setPlatform(platformTextField.getText().trim());
            DatabaseUtils.updateLocation(virtualLocation);
            logger.info("Location after update:  " + virtualLocation);
        }
        else {
            throw new EntityEditException("Error updating location: Please select the correct location type.");
        }
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

    public void setLocationToEdit(Location location) {
        this.locationToEdit = location;
        if (location instanceof Address address) {
            locationTypeComboBox.setValue("Address");
            streetTextField.setText(address.getStreet());
            houseNumberTextField.setText(address.getHouseNumber());
            cityTextField.setText(address.getCity());
            handleLocationTypeChange();
        } else if (location instanceof VirtualLocation virtualLocation) {
            locationTypeComboBox.setValue("VirtualLocation");
            meetingLinkTextField.setText(virtualLocation.getMeetingLink());
            platformTextField.setText(virtualLocation.getPlatform());
            handleLocationTypeChange();
        }
    }
}
