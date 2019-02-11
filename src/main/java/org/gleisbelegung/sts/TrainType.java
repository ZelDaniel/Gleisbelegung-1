package org.gleisbelegung.sts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapping of train names to certain categories for assigning priorities to the train
 */
public class TrainType implements Comparable<TrainType> {

    private final String name;
    private final Map<String, TrainType> map = new HashMap<>();
    private final int ord;
    private static final Map<String, TrainType> valueMap = new HashMap<>();

    private final TrainType base;


    /*
     * List of train types order of declaration is in descending order of priority
     */
    public static final TrainType HILFSZ = new TrainType("HILFSZ");
    public static final TrainType ICE = new TrainType("ICE");
    public static final TrainType IC = new TrainType("IC");
    public static final TrainType IRE = new TrainType("IRE");
    public static final TrainType RE = new TrainType("RE");
    public static final TrainType CSQ = new TrainType("CSQ");
    public static final TrainType CNL = new TrainType("CNL");
    public static final TrainType RB = new TrainType("RB");
    public static final TrainType SDZ = new TrainType("SDZ");
    public static final TrainType IRC = new TrainType("IRC");
    public static final TrainType GZ = new TrainType("GZ");
    public static final TrainType OTHER = new TrainType("OTHER");
    public static final TrainType S = new TrainType("S");
    /* trains with no passengers below this line */
    public static final TrainType LR = new TrainType("LR");
    public static final TrainType TFZ = new TrainType("TFZ");
    public static final TrainType BAUZ = new TrainType("BAUZ");
    public static final TrainType RF = new TrainType("RF");

    private TrainType(final String name) {
        this.name = name;
        valueMap.put(name, this);
        this.base = this;
        this.ord = valueMap.size();
    }

    private TrainType(final String name, final TrainType base) {
        this.name = name;
        this.ord = base.ord;
        this.base = base;
        base.map.put(name, this);
    }

    public static TrainType create(final String string) {
        if (string.contains(" ")) {
            return create(string.split(" ")[0]);
        }
        switch (string.replaceAll("[0-9]", "").toUpperCase()) {
            case "BAUZ":
                return BAUZ;

            case "CNL":
                return CNL;

            case "CSQ":
                return CSQ;

            case "CB":
            case "CBZ":
            case "CFA":
            case "CFN":
            case "CIL":
            case "CHL":
            case "CL":
            case "CS":
            case "CT":
            case "DFG":
            case "DGS":
            case "DGX":
            case "DGZ":
            case "DNG":
            case "EUC":
            case "FBZ":
            case "FE":
            case "FIR":
            case "FR":
            case "FS":
            case "FX":
            case "FZ":
            case "FZT":
            case "GAG":
            case "GC":
            case "GZ":
            case "ICG":
            case "ICL":
            case "IKE":
            case "IKL":
            case "IKS":
            case "KC":
            case "KCL":
            case "MCT":
            case "NG":
            case "RC":
            case "ROLA":
            case "PIC":
            case "TC":
            case "TEC":
            case "TGC":
            case "TKC":
            case "TKE":
            case "TRC":
                return GZ.get(string);

            case "ICE":
            case "TGV":
            case "THA":
            case "OEC":
                return ICE.get(string);

            case "IC":
            case "EC":
            case "EN":
                return IC.get(string);

            case "HILFSZ":
                return HILFSZ;


            case "IRE":
            case "IR":
                return IRE.get(string);

            case "IRC":
            case "AZ":
                return IRC.get(string);

            case "LR":
            case "L":
            case "L-ICE":
            case "LRE":
            case "LS":
            case "LICE":
                return LR.get(string);

            case "RB":
            case "CAN":
            case "DNR":
            case "EB":
            case "EIB":
            case "ERB":
            case "HLB":
            case "LYN":
            case "NEG":
            case "NWB":
            case "MR":
            case "ME":
            case "MER":
            case "PAGA":
            case "OE":
            case "OS":
            case "STB":
            case "VIA":
                return RB.get(string);

            case "RE":
            case "ALX":
            case "REX":
                return RE.get(string);

            case "S":
                return S;

            case "SDZ":
            case "DPE":
                return SDZ.get(string);

            case "TFZ":
            case "ERSATZLOK":
            case "LOK":
            case "RLOK":
            case "TFZL":
                return TFZ.get(string);

            case "RF":
            case "RA":
            case "RABT":
            case "R":
            case "SCHADW":
                return RF.get(string);

            default:
                return OTHER.get(string);
        }
    }

    public static TrainType valueOf(final String string) {
        return TrainType.valueMap.get(string);
    }

    private TrainType get(final String name) {
        if (name.equals(this.name)) {
            return this;
        }
        TrainType tt = this.map.get(name);
        if (tt == null) {
            tt = new TrainType(name, this);
        }
        return tt;

    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(final TrainType o) {
        return this.ord - o.ord;
    }

    public TrainType getBase() {
        return this.base;
    }

}
