package javafx.prod.changes;

import app.prod.model.ChangeLogEntry;
import app.prod.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.prod.utils.JavaFxUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChangeLogController {

    @FXML
    private TableView<ChangeLogEntry<?>> changeLogTableView;
    @FXML
    private TableColumn<ChangeLogEntry<?>, LocalDateTime> timestampColumn;
    @FXML
    private TableColumn<ChangeLogEntry<?>, String> entityColumn;
    @FXML
    private TableColumn<ChangeLogEntry<?>, String> changeTypeColumn;
    @FXML
    private TableColumn<ChangeLogEntry<?>, Object> oldValueColumn;
    @FXML
    private TableColumn<ChangeLogEntry<?>, Object> newValueColumn;
    @FXML
    private TableColumn<ChangeLogEntry<?>, String> userRoleColumn;

    @FXML
    public void initialize() {
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        entityColumn.setCellValueFactory(new PropertyValueFactory<>("entity"));
        changeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("changeType"));
        oldValueColumn.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        newValueColumn.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("userRole"));

        JavaFxUtils.setCustomTableColumnCellFactory(timestampColumn, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        changeLogTableView.getItems().setAll(FileUtils.deserializeChangeLog());
    }
}
