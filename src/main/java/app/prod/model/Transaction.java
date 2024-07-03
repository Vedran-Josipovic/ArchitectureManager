package app.prod.model;

import app.prod.enumeration.TransactionType;
import app.prod.exception.TransactionAmountException;
import app.prod.exception.EntityInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction extends Entity implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Project project; // Add Project reference

    public Transaction() {

    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Transaction that = (Transaction) o;
        return getTransactionType() == that.getTransactionType() && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getDate(), that.getDate()) && Objects.equals(getProject(), that.getProject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTransactionType(), getAmount(), getDescription(), getDate(), getProject());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionType=" + transactionType +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", project=" + project +
                ", id=" + id +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }

    public static class Builder {
        private Long id;
        private String name;
        private TransactionType transactionType;
        private BigDecimal amount;
        private String description;
        private LocalDate date;
        private Project project; // Add Project reference

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder withProject(Project project) {
            this.project = project;
            return this;
        }

        public Transaction build() throws EntityInitializationException, TransactionAmountException {
            Transaction transaction = new Transaction();
            if (name == null || name.isEmpty()) {
                throw new EntityInitializationException("Transaction must have a non-empty name.");
            }
            validateTransactionAmount();

            transaction.id = this.id;
            transaction.name = this.name;
            transaction.transactionType = this.transactionType;
            transaction.amount = this.amount;
            transaction.description = this.description;
            transaction.date = this.date;
            transaction.project = this.project; // Set project

            return transaction;
        }

        private void validateTransactionAmount() throws TransactionAmountException {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new TransactionAmountException("Transactions must have a non-negative amount.");
            }
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                logger.warn("Transaction is zero.");
            }
        }
    }
}
