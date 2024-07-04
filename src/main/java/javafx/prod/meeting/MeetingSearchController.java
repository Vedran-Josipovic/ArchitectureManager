package javafx.prod.meeting;

import app.prod.exception.EntityDeleteException;
import app.prod.model.ChangeLogEntry;
import app.prod.model.Location;
import app.prod.model.Meeting;
import app.prod.service.DatabaseService;
import app.prod.utils.DatabaseUtils;
import app.prod.utils.FileUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class MeetingSearchController {
    private static final Logger logger = LoggerFactory.getLogger(MeetingSearchController.class);
    private final ObservableList<Meeting> meetingList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Meeting, Void> editOrDeleteColumn;
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
        meetings = DatabaseService.sortMeetings(meetings);

        meetingList.setAll(meetings);
        meetingTableView.setItems(meetingList);

        JavaFxUtils.addButtonToTable(editOrDeleteColumn, this::handleEdit, this::handleDelete);
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
            meetings = DatabaseService.sortMeetings(meetings);
            meetingTableView.setItems(FXCollections.observableArrayList(meetings));
        } catch (Exception ex) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while searching for meetings: " + ex.getMessage());
            logger.error("Error while searching for meetings: ", ex);
        }
    }

    public void handleEdit(Meeting selectedMeeting) {
        if (selectedMeeting != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Edit Meeting", "Are you sure you want to edit this meeting?");
            if (confirmed) {
                try {
                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/javafx/prod/meeting/meetingAdd.fxml"));
                    Scene scene = new Scene(loader.load(), HelloApplication.width, HelloApplication.height);
                    scene.getStylesheets().add(Objects.requireNonNull(HelloApplication.class.getResource("/javafx/prod/styles/style.css")).toExternalForm());
                    MeetingAddController controller = loader.getController();
                    controller.setMeetingToEdit(selectedMeeting);

                    Stage primaryStage = (Stage) meetingTableView.getScene().getWindow();
                    primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/javafx/prod/images/icon.png"))));
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Edit Meeting");
                } catch (IOException e) {
                    logger.error("Error loading the edit meeting screen", e);
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a meeting to edit.");
        }
    }

    public void handleDelete(Meeting selectedMeeting) {
        if (selectedMeeting != null) {
            boolean confirmed = JavaFxUtils.showConfirmationDialog("Delete Meeting", "Are you sure you want to delete this meeting?");
            if (confirmed) {
                try {
                    DatabaseUtils.deleteMeeting(selectedMeeting.getId());

                    ChangeLogEntry<Meeting> entry = new ChangeLogEntry<>(
                            LocalDateTime.now(),
                            "Meeting",
                            "DELETE",
                            selectedMeeting,
                            null,
                            HelloApplication.getUser().getRole()
                    );
                    FileUtils.logChange(entry);

                    meetingList.remove(selectedMeeting);
                } catch (EntityDeleteException e) {
                    JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Deletion Error", e.getMessage());
                    logger.warn("Cannot delete meeting! It is referenced by another entity.");
                }
            }
        } else {
            JavaFxUtils.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a meeting to delete.");
        }
    }
}
