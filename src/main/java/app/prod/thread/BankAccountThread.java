package app.prod.thread;

import app.prod.enumeration.TransactionType;

import java.math.BigDecimal;

import app.prod.model.TransactionRecord;
import app.prod.utils.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class BankAccountThread extends DatabaseThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountThread.class);

    private static BigDecimal accountBalance = BigDecimal.ZERO;

    public static BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public static void startBalanceRefresher() {
        new Thread(new BankAccountThread()).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                startDatabaseOperation();
                List<TransactionRecord<TransactionType, BigDecimal>> transactionDataList = DatabaseUtils.getTransactionData();

                BigDecimal newBalance = BigDecimal.ZERO;
                for (TransactionRecord<TransactionType, BigDecimal> record : transactionDataList) {
                    if (record.type().equals(TransactionType.INCOME)) {
                        newBalance = newBalance.add(record.amount());
                    } else if (record.type().equals(TransactionType.EXPENSE)) {
                        newBalance = newBalance.subtract(record.amount());
                    }
                }

                updateAccountBalance(newBalance);
                endDatabaseOperation();

                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread was interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("An error occurred while updating the account balance", e);
            }
        }
    }

    private static synchronized void updateAccountBalance(BigDecimal newBalance) {
        accountBalance = newBalance;
    }
}
