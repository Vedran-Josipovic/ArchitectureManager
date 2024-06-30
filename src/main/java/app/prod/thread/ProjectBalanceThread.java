package app.prod.thread;

import app.prod.model.Project;
import app.prod.utils.DatabaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import app.prod.model.Transaction;
import app.prod.enumeration.TransactionType;



public class ProjectBalanceThread extends DatabaseThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ProjectBalanceThread.class);
    private static final Map<Long, BigDecimal> projectBalances = new HashMap<>();

    public static BigDecimal getProjectBalance(Long projectId) {
        synchronized (projectBalances) {
            return projectBalances.getOrDefault(projectId, BigDecimal.ZERO);
        }
    }

    public static void startBalanceRefresher() {
        new Thread(new ProjectBalanceThread()).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                startDatabaseOperation();
                List<Project> projects = DatabaseUtils.getProjects();
                synchronized (projectBalances) {
                    projectBalances.clear();

                    for (Project project : projects) {
                        BigDecimal projectBalance = calculateProjectBalance(project.getTransactions());
                        projectBalances.put(project.getId(), projectBalance);
                    }
                }

                endDatabaseOperation();

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread was interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("An error occurred while updating the project balances", e);
            }
        }
    }

    private BigDecimal calculateProjectBalance(Set<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                balance = balance.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                balance = balance.subtract(transaction.getAmount());
            }
        }
        return balance;
    }
}
