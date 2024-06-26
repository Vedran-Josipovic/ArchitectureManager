package app.prod.model;

import app.prod.enumeration.TransactionType;
import app.prod.exception.TransactionAmountException;
import app.prod.exception.entityInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Transaction extends Entity {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Transaction that = (Transaction) o;
        return getTransactionType() == that.getTransactionType() && Objects.equals(getAmount(), that.getAmount()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTransactionType(), getAmount(), getDescription(), getDate());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionType=" + transactionType +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
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

        public Transaction build() throws entityInitializationException, TransactionAmountException {
            Transaction transaction = new Transaction();
            //Pri korištenju tapraviti while petlju sa try-catch
            //Ovo se koristi negdje drugdje, možda dodati klasu/sučelje sa statičkim validation metodama
            if (name == null || name.isEmpty()) {
                throw new entityInitializationException("Transaction must have a non-empty name.");
            }
            validateTransactionAmount();

            transaction.id = this.id;
            transaction.name = this.name;
            transaction.transactionType = this.transactionType;
            transaction.amount = this.amount;
            transaction.description = this.description;
            transaction.date = this.date;

            return transaction;
        }

        //Pri korištenju napraviti while petlju sa try-catch
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
