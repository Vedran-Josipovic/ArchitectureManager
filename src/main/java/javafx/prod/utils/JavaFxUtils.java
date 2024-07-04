package javafx.prod.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.prod.HelloApplication;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

public class JavaFxUtils {
    private static final Logger logger = LoggerFactory.getLogger(JavaFxUtils.class);

    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void clearForm(Control... controls) {
        for (Control control : controls) {
            if (control instanceof TextInputControl) {
                ((TextInputControl) control).clear();
            } else if (control instanceof ComboBox) {
                ((ComboBox<?>) control).getSelectionModel().clearSelection();
            } else if (control instanceof DatePicker) {
                ((DatePicker) control).setValue(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumberSafely(String value, Class<T> type) {
        try {
            if (type == Long.class) {
                return (T) Long.valueOf(value);
            } else if (type == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (type == Double.class) {
                return (T) Double.valueOf(value);
            } else if (type == BigDecimal.class) {
                return (T) new BigDecimal(value);
            } else {
                throw new UnsupportedOperationException("Type not supported: " + type.getName());
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid format for type " + type.getName() + ": " + value + "[" + e.getMessage() + "]");
            return null;
        }
    }

    public static <T> void setCustomComboBoxCellFactory(ComboBox<T> comboBox, Callback<T, String> displayTextCallback) {
        comboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(displayTextCallback.call(item));
                        }
                    }
                };
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(displayTextCallback.call(item));
                }
            }
        });
    }

    public static <T> void setCustomTableColumnCellFactory(TableColumn<T, LocalDateTime> column, DateTimeFormatter formatter) {
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<T, LocalDateTime> call(TableColumn<T, LocalDateTime> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        });
    }



    public static boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static <T> void addButtonToTable(TableColumn<T, Void> column, Consumer<T> onEdit, Consumer<T> onDelete) {
        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                final TableCell<T, Void> cell = new TableCell<>() {

                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDelete = new Button("Delete");

                    {
                        btnEdit.setOnAction((event) -> {
                            T item = getTableView().getItems().get(getIndex());
                            onEdit.accept(item);
                        });
                        btnDelete.setOnAction((event) -> {
                            T item = getTableView().getItems().get(getIndex());
                            onDelete.accept(item);
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
        column.setCellFactory(cellFactory);
    }
}
