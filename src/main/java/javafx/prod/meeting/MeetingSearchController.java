package javafx.prod.meeting;

import app.prod.model.Location;
import app.prod.model.Meeting;
import app.prod.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MeetingSearchController {
    private static final Logger logger = LoggerFactory.getLogger(MeetingSearchController.class);
    private final ObservableList<Meeting> meetingList = FXCollections.observableArrayList();

    @FXML
    private TextField meetingNameTextField;
    @FXML
    private DatePicker meetingStartDatePicker;
    @FXML
    private DatePicker meetingEndDatePicker;
    @FXML
    private ComboBox<Location> locationComboBox;
    @FXML
    private TableView<Meeting> meetingTableView;
    @FXML
    private TableColumn<Meeting, String> meetingNameColumn;
    @FXML
    private TableColumn<Meeting, String> meetingStartDateColumn;
    @FXML
    private TableColumn<Meeting, String> meetingEndDateColumn;
    @FXML
    private TableColumn<Meeting, String> meetingLocationColumn;
    @FXML
    private TableColumn<Meeting, String> meetingParticipantsColumn;
    @FXML
    private TableColumn<Meeting, String> meetingNotesColumn;

    public void initialize() {
        List<Location> locations = DatabaseUtils.getLocations();
        JavaFxUtils.setCustomComboBoxCellFactory(locationComboBox, Location::getFullLocationDetails);
        locationComboBox.setItems(FXCollections.observableArrayList(locations));

        meetingNameColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        meetingStartDateColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getMeetingStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        meetingEndDateColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getMeetingEnd().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));

        meetingLocationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getLocation().getFullLocationDetails()));


        meetingParticipantsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getParticipants().toString()));
        meetingNotesColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getNotes()));

        List<Meeting> meetings = DatabaseUtils.getMeetingsByFilters(new Meeting());
        meetingList.setAll(meetings);
        meetingTableView.setItems(meetingList);
    }

    @FXML
    public void searchMeetings() {
        try {
            String name = meetingNameTextField.getText().trim();
            LocalDateTime meetingStart = meetingStartDatePicker.getValue() != null ? meetingStartDatePicker.getValue().atStartOfDay() : null;
            LocalDateTime meetingEnd = meetingEndDatePicker.getValue() != null ? meetingEndDatePicker.getValue().atStartOfDay() : null;
            Location location = locationComboBox.getValue();

            Meeting filter = new Meeting(name, meetingStart, meetingEnd, location, null, null);
            List<Meeting> meetings = DatabaseUtils.getMeetingsByFilters(filter);
            meetingTableView.setItems(FXCollections.observableArrayList(meetings));
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for meetings: " + ex.getMessage());
            logger.error("Error while searching for meetings: ", ex);
        }
    }
}
