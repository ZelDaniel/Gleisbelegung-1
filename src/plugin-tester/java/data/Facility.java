package data;

public class Facility {

    private final String name;
    private final int simbuild;
    private final int aid;

    public Facility(final String name, int simbuild, int aid) {
        this.name = name;
        this.simbuild = simbuild;
        this.aid = aid;
    }

    public String getName() {
        return this.name;
    }
}
