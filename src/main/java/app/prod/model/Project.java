package app.prod.model;

//To be implemented in the future
public final class Project extends Entity implements Issue{
    private Client client;

    @Override
    public double getExpectedProgress() {
        return 0;
    }
}
