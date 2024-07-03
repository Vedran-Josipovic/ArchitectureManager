package javafx.prod.meeting;

import app.prod.exception.ValidationException;
import app.prod.model.*;
import app.prod.utils.DatabaseUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MeetingAddController {
    private static final Logger logger = LoggerFactory.getLogger(MeetingAddController.class);

    @FXML
    private TextField meetingNameTextField;
    @FXML
    private DatePicker meetingStartDatePicker;
    @FXML
    private DatePicker meetingEndDatePicker;
    @FXML
    private ComboBox<Location> locationComboBox;
    @FXML
    private TextArea notesTextArea;
    @FXML
    private ListView<Contact> participantsListView;

    public void initialize() {
        List<Location> locations = DatabaseUtils.getLocations();

        JavaFxUtils.setCustomComboBoxCellFactory(locationComboBox, Location::getFullLocationDetails);
        locationComboBox.setItems(FXCollections.observableArrayList(locations));

        List<Contact> participants = DatabaseUtils.getAllContacts();
        participantsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Contact> call(ListView<Contact> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Contact item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getContactDetails());
                        }
                    }
                };
            }
        });

        participantsListView.setItems(FXCollections.observableArrayList(participants));

        participantsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void addMeeting() {
        try {
            validateInputs();

            String name = meetingNameTextField.getText().trim();
            LocalDateTime meetingStart = LocalDateTime.of(meetingStartDatePicker.getValue(), LocalTime.MIN);
            LocalDateTime meetingEnd = LocalDateTime.of(meetingEndDatePicker.getValue(), LocalTime.MIN);
            Location location = locationComboBox.getValue();
            String notes = notesTextArea.getText().trim();
            Set<Contact> participants = new HashSet<>(participantsListView.getSelectionModel().getSelectedItems());

            Meeting meeting = new Meeting(name, meetingStart, meetingEnd, location, participants, notes);

            DatabaseUtils.saveMeeting(meeting);

            JavaFxUtils.clearForm(meetingNameTextField, meetingStartDatePicker, meetingEndDatePicker, locationComboBox, notesTextArea, participantsListView);
            JavaFxUtils.showAlert(Alert.AlertType.INFORMATION, "Success", "Meeting added successfully.");
        } catch (ValidationException ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Validation Error", ex.getMessage());
            logger.error("Error while adding meeting: " + ex.getMessage());
        }
    }

    private void validateInputs() throws ValidationException {
        if (meetingNameTextField.getText().isEmpty() ||
            meetingStartDatePicker.getValue() == null ||
            meetingEndDatePicker.getValue() == null ||
            locationComboBox.getValue() == null) {
            throw new ValidationException("Please fill in all required fields.");
        }
        if (meetingStartDatePicker.getValue().isAfter(meetingEndDatePicker.getValue())) {
            throw new ValidationException("Meeting start date cannot be after meeting end date.");
        }
    }
}
