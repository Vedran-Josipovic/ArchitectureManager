package javafx.prod.utils;

import app.prod.utils.DatabaseUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class JavaFxUtils {
    private static Logger logger = LoggerFactory.getLogger(JavaFxUtils.class);

    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void clearForm(TextInputControl... fields) {
        for (var f : fields) f.clear();
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

}
