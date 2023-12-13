package finv.data;

public enum Frequency {

    DAILY("1d"),
    WEEKLY("5d"),
    MONTHLY("1mo"),
    YEARLY("1y");

    private final String name;

    Frequency(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the object.
     *
     * @return the name of the object
     */
    public String getName() {
        return this.name;
    }
}
