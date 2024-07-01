package app.prod.utils;

import app.prod.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileUtils {
    //logger
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String FILE_PATH = "src/main/files/";

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            String message = "No such algorithm exists!";
            logger.error(message, ex);
            throw new RuntimeException(ex);
        }
    }

    public static void addUserToFile(String username, String password, String role) {
        try (FileWriter writer = new FileWriter(FILE_PATH + "users.txt", true)) {
            writer.write(username + "," + password + "," + role + "\n");
        } catch (IOException ex) {
            String message = "Error writing to the users file!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<User> getUsersFromFile() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH + "users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 3) {
                    users.add(new User(userData[0], userData[1], userData[2]));
                }
            }
        } catch (IOException ex) {
            String message = "Error reading the users file!";
            logger.error(message, ex);
            System.out.println(message);
        }
        return users;
    }
}
