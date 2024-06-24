package app.prod.model;

import java.math.BigDecimal;

public class BankAccount {
    //Možda ovo izvaditi iz datoteke ubuduće
    private static BigDecimal accountBalance = BigDecimal.ZERO;
    private BankAccount(){}

    public static synchronized void addAmount(BigDecimal amount) {
        accountBalance = accountBalance.add(amount);
    }

    public static synchronized void subtractAmount(BigDecimal amount) {
        accountBalance = accountBalance.subtract(amount);
    }

    public static BigDecimal getAccountBalance() {
        return accountBalance;
    }

}
