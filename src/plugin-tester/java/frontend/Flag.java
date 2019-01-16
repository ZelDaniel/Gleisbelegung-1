package frontend;

public class Flag {

    private String facilityName = "PluginTest";
    private int simbuild = 1;
    private int aid = Integer.MAX_VALUE;
    private boolean echoInput = true;

    public static Flag parse(final String[] args) {
        return new Flag();
    }

    public String getFacilityName() {
        return this.facilityName;
    }

    public int getSimbuild() {
        return this.simbuild;
    }

    public int getAid() {
        return this.aid;
    }

    public boolean isEchoInput()
    {
        return this.echoInput;
    }
}
