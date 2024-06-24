package app.prod.utils;

import java.util.*;
import java.io.*;
import java.sql.*;

import app.prod.model.Address;
import app.prod.model.Location;
import app.prod.model.VirtualLocation;
import org.slf4j.*;

public class DatabaseUtils {
    private static Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String DATABASE_FILE = "conf/database.properties";

    //Make private when it won't be used in Main
    public static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String databaseUrl = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(databaseUrl, username, password);
    }

    public static void saveLocation(Location newLocation, String selectedType){
        if("Address".equals(selectedType)){
            saveAddress((Address) newLocation);
        } else if("VirtualLocation".equals(selectedType)){
            saveVirtualLocation((VirtualLocation) newLocation);
        }
    }

    private static void saveAddress(Address address){
        try(Connection connection = connectToDatabase()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LOCATION (TYPE, STREET, HOUSE_NUMBER, CITY) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, "Address");
            preparedStatement.setString(2, address.getStreet());
            preparedStatement.setString(3, address.getHouseNumber());
            preparedStatement.setString(4, address.getCity());
            preparedStatement.execute();
            logger.info("Address saved successfully");
        } catch (SQLException | IOException e) {
            logger.error("Error while saving address: " + e.getMessage());
        }
    }

    private static void saveVirtualLocation(VirtualLocation virtualLocation){
        try(Connection connection = connectToDatabase()){
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO LOCATION (TYPE, MEETING_LINK, PLATFORM) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "VirtualLocation");
            preparedStatement.setString(2, virtualLocation.getMeetingLink());
            preparedStatement.setString(3, virtualLocation.getPlatform());
            preparedStatement.execute();
            logger.info("Virtual location saved successfully");
        } catch (SQLException | IOException e) {
            logger.error("Error while saving virtual location", e);
        }
    }

}
