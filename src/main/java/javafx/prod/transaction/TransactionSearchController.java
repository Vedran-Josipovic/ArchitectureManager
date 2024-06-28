package javafx.prod.transaction;

import app.prod.enumeration.TransactionType;
import app.prod.model.Project;
import app.prod.model.Transaction;
import app.prod.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.prod.utils.JavaFxUtils;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionSearchController {
    private final ObservableList<String> transactionTypeNames = FXCollections.observableArrayList();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();

    @FXML
    private TextField transactionNameTextField;
    @FXML
    private ComboBox<String> transactionTypeComboBox;
    @FXML
    private DatePicker transactionDateDatePicker;
    @FXML
    private TextField minAmountTextField;
    @FXML
    private TextField maxAmountTextField;
    @FXML
    private ComboBox<Project> projectComboBox;
    @FXML
    private TableView<Transaction> transactionTableView;
    @FXML
    private TableColumn<Transaction, String> transactionNameTableColumn;
    @FXML
    private TableColumn<Transaction, String> transactionTypeTableColumn;
    @FXML
    private TableColumn<Transaction, String> transactionAmountTableColumn;
    @FXML
    private TableColumn<Transaction, String> transactionDateTableColumn;
    @FXML
    private TableColumn<Transaction, String> transactionDescriptionTableColumn;
    @FXML
    private TableColumn<Transaction, String> transactionProjectTableColumn;

    public void initialize() {
        transactionTypeNames.add("All");
        for (TransactionType t : TransactionType.values()) {
            transactionTypeNames.add(t.getName());
        }
        transactionTypeComboBox.setItems(transactionTypeNames);
        transactionTypeComboBox.getSelectionModel().selectFirst(); // Select "All" by default

        projects.add(new Project("All")); // Add "All" option for no filter
        projects.addAll(DatabaseUtils.getProjects());
        projectComboBox.setItems(projects);
        projectComboBox.getSelectionModel().selectFirst(); // Select "All" by default

        transactionNameTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        transactionTypeTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTransactionType().getName()));
        transactionAmountTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getAmount().toString()));
        transactionDateTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDate().format(DateTimeFormatter.ofPattern("dd. MMM yyyy."))));
        transactionDescriptionTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getDescription()));
        transactionProjectTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getProject().getName()));

        List<Transaction> filteredTransactions = DatabaseUtils.getTransactionsByFilters(new Transaction(), BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Long.MAX_VALUE));
        transactionTableView.setItems(FXCollections.observableArrayList(filteredTransactions));
    }

    public void transactionSearch() {
        BigDecimal minAmount = JavaFxUtils.parseNumberSafely(minAmountTextField.getText(), BigDecimal.class);
        if (minAmount == null) {
            if (!minAmountTextField.getText().isEmpty()) {
                JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "Invalid minimum amount format");
                return;
            }
            minAmount = BigDecimal.valueOf(Long.MIN_VALUE);
        }

        BigDecimal maxAmount = JavaFxUtils.parseNumberSafely(maxAmountTextField.getText(), BigDecimal.class);
        if (maxAmount == null) {
            if (!maxAmountTextField.getText().isEmpty()) {
                JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "Invalid maximum amount format");
                return;
            }
            maxAmount = BigDecimal.valueOf(Long.MAX_VALUE);
        }

        if (minAmount.compareTo(maxAmount) > 0) {
            JavaFxUtils.showAlert(Alert.AlertType.ERROR, "Error", "Minimum amount can't be greater than maximum amount");
            return;
        }

        Transaction filter = createTransactionFilter();
        List<Transaction> filteredTransactions = DatabaseUtils.getTransactionsByFilters(filter, minAmount, maxAmount);
        transactionTableView.setItems(FXCollections.observableArrayList(filteredTransactions));
    }

    private Transaction createTransactionFilter() {
        Transaction filter = new Transaction();
        if (!transactionNameTextField.getText().isEmpty()) {
            filter.setName(transactionNameTextField.getText());
        }
        String type = transactionTypeComboBox.getValue();
        if (type != null && !type.equals("All")) {
            filter.setTransactionType(TransactionType.valueOf(type.toUpperCase()));
        }
        if (transactionDateDatePicker.getValue() != null) {
            filter.setDate(transactionDateDatePicker.getValue());
        }
        Project selectedProject = projectComboBox.getValue();
        if (selectedProject != null && selectedProject.getId() != null) {
            filter.setProject(selectedProject);
        }
        return filter;
    }
}
