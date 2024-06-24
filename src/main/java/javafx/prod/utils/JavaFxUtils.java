package javafx.prod.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;

public class JavaFxUtils {
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
}
