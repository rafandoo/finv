package finv.data;

public enum Event {

    DIVIDENDS("div"),
    SPLIT("split"),
    HISTORY("history");

    private final String name;

    Event(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the object.
     *
     * @return  the name of the object
     */
    public String getName() {
        return this.name;
    }
}
