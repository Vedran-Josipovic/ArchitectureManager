package javafx.prod.utils;

import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

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

    public static <T> void setCustomCellFactory(ComboBox<T> comboBox, Callback<T, String> displayTextCallback) {
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

    public static boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}
