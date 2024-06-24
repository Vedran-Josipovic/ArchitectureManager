package app.prod.enumeration;

public enum TransactionType {
    INCOME(1L,"Income"),
    EXPENSE(2L,"Expense");

    private final Long id;
    private final String name;

    TransactionType(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

}
