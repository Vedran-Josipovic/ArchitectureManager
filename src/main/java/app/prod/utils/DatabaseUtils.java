package app.prod.utils;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.io.*;
import java.sql.*;

import app.prod.enumeration.TransactionType;
import app.prod.model.*;
import org.slf4j.*;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
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

    public static void saveTransaction(Transaction transaction) {
        try (Connection connection = connectToDatabase()) {
            String insertTransactionSql = "INSERT INTO TRANSACTION (NAME, TRANSACTION_TYPE, AMOUNT, DESCRIPTION, DATE) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSql);
            preparedStatement.setString(1, transaction.getName());
            preparedStatement.setString(2, transaction.getTransactionType().getName());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setString(4, transaction.getDescription());
            preparedStatement.setDate(5, Date.valueOf(transaction.getDate()));
            preparedStatement.execute();
            logger.info("Transaction saved successfully.");
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while saving transaction to database!";
            logger.error(message, ex);
            System.out.println(message);
        }
    }

    public static List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT * FROM TRANSACTION";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            transactions = mapResultSetToTransactionList(resultSet);
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return transactions;
    }

    private static List<Transaction> mapResultSetToTransactionList(ResultSet resultSet) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        while (resultSet.next()) {
            Transaction transaction = new Transaction();
            transaction.setId(resultSet.getLong("ID"));
            transaction.setName(resultSet.getString("NAME"));
            transaction.setTransactionType(TransactionType.valueOf(resultSet.getString("TRANSACTION_TYPE").toUpperCase()));
            transaction.setAmount(resultSet.getBigDecimal("AMOUNT"));
            transaction.setDescription(resultSet.getString("DESCRIPTION"));
            transaction.setDate(resultSet.getDate("DATE").toLocalDate());
            transactions.add(transaction);
        }
        return transactions;
    }

    public static List<Transaction> getTransactionsByFilters(Transaction transactionFilter, BigDecimal minAmount, BigDecimal maxAmount) {
        List<Transaction> transactions = new ArrayList<>();
        Map<Integer, Object> queryParams = new HashMap<>();
        int paramOrdinalNumber = 1;


        try (Connection connection = connectToDatabase()) {
            StringBuilder baseSqlQuery = new StringBuilder("SELECT * FROM TRANSACTION WHERE 1=1");

            if (Optional.ofNullable(transactionFilter.getName()).filter(s -> !s.isEmpty()).isPresent()) {
                baseSqlQuery.append(" AND LOWER(NAME) LIKE ?");
                queryParams.put(paramOrdinalNumber++, "%" + transactionFilter.getName().toLowerCase() + "%");
            }

            if (Optional.ofNullable(transactionFilter.getTransactionType()).isPresent()) {
                baseSqlQuery.append(" AND TRANSACTION_TYPE = ?");
                queryParams.put(paramOrdinalNumber++, transactionFilter.getTransactionType().getName());
            }

            if (Optional.ofNullable(transactionFilter.getDate()).isPresent()) {
                baseSqlQuery.append(" AND DATE = ?");
                queryParams.put(paramOrdinalNumber++, Date.valueOf(transactionFilter.getDate()));
            }

            if (minAmount != null && maxAmount != null) {
                baseSqlQuery.append(" AND AMOUNT BETWEEN ? AND ?");
                queryParams.put(paramOrdinalNumber++, minAmount);
                queryParams.put(paramOrdinalNumber++, maxAmount);
            } else if (minAmount != null) {
                baseSqlQuery.append(" AND AMOUNT >= ?");
                queryParams.put(paramOrdinalNumber++, minAmount);
            } else if (maxAmount != null) {
                baseSqlQuery.append(" AND AMOUNT <= ?");
                queryParams.put(paramOrdinalNumber++, maxAmount);
            }


            PreparedStatement preparedStatement = connection.prepareStatement(baseSqlQuery.toString());
            logger.info(preparedStatement.toString());

            for (Integer paramNumber : queryParams.keySet()) {
                if (queryParams.get(paramNumber) instanceof String stringQueryParam) {
                    preparedStatement.setString(paramNumber, stringQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Long longQueryParam) {
                    preparedStatement.setLong(paramNumber, longQueryParam);
                } else if (queryParams.get(paramNumber) instanceof Date dateQueryParam) {
                    preparedStatement.setDate(paramNumber, dateQueryParam);
                } else if (queryParams.get(paramNumber) instanceof BigDecimal bigDecimal) {
                    preparedStatement.setBigDecimal(paramNumber, bigDecimal);
                }
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            logger.info(resultSet.toString());
            transactions = mapResultSetToTransactionList(resultSet);
            transactions.forEach(t -> logger.info(t.toString()));
        } catch (SQLException | IOException ex) {
            String message = "An error occurred while retrieving filtered transactions from the database!";
            logger.error(message, ex);
        }
        return transactions;
    }

    public static List<TransactionRecord<TransactionType, BigDecimal>> getTransactionData() {
        List<TransactionRecord<TransactionType, BigDecimal>> transactionDataList = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "SELECT TRANSACTION_TYPE, AMOUNT FROM TRANSACTION";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                TransactionType transactionType = TransactionType.valueOf(resultSet.getString("TRANSACTION_TYPE").toUpperCase());
                BigDecimal amount = resultSet.getBigDecimal("AMOUNT");
                transactionDataList.add(new TransactionRecord<>(transactionType, amount));
            }
        } catch (SQLException | IOException e) {
            String message = "An error occurred while connecting to the database!";
            logger.error(message, e);
            System.out.println(message);
        }
        return transactionDataList;
    }





}
