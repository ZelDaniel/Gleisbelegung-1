package org.gleisbelegung.sts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapping of train names to certain categories for assigning priorities to the train
 */
public class TrainType implements Comparable<TrainType> {

	private static final Map<String, TrainType> valueMap = new HashMap<>();

	public static final TrainType ICE = new TrainType("ICE");
	public static final TrainType TGV = new TrainType("TGV");
	public static final TrainType IC = new TrainType("IC");
	public static final TrainType EC = new TrainType("EC");
	public static final TrainType IRE = new TrainType("IRE");
	public static final TrainType RE = new TrainType("RE");
	public static final TrainType CSQ = new TrainType("CSQ");
	public static final TrainType CNL = new TrainType("CNL");

	/* passenger train types */
	public static final TrainType RB = new TrainType("RB");
	public static final TrainType SDZ = new TrainType("SDZ");

	public static final TrainType IRC = new TrainType(
			"IRC"); /* cargo high prio */

	/* */
	public static final TrainType GZ = new TrainType("GZ");

	/* passenger lowest prio */
	public static final TrainType S = new TrainType("S");

	/* bottom prio, empty trains */
	public static final TrainType LR = new TrainType("LR");
	public static final TrainType TFZ = new TrainType("TZF");
	public static final TrainType LOK = new TrainType("LOK");
	public static final TrainType RF = new TrainType("RF");
	public static final TrainType BAUZ = new TrainType("BAUZ");
	public static final TrainType HILFSZ = new TrainType("Hilfsz");
	public static final TrainType OTHER = new TrainType("");

	private final String name;
	private final Map<String, TrainType> map = new HashMap<>();
	private final int ord;

	private final TrainType base;

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
		if (string.contains(" "))
			return create(string.split(" ")[0]);
		switch (string.replaceAll("[0-9]", "").toUpperCase()) {
		case "TGV":
			return TGV;
		case "THA":
			return TGV.get("THALYS");
		case "ICE":
			return ICE;
		case "OEC":
			return ICE.get("OEC");
		case "IC":
			return IC;
		case "EC":
			return EC;
		case "EN":
			return EC.get("EN");
		case "CNL":
			return CNL;
		case "IRE":
			return IRE;
		case "IR":
			return IRE.get("IR");
		case "RE":
			return RE;
		case "REX":
			return RE.get("REX");
		case "ALX":
			return RE.get("ALX");
			
		case "RB":
			return RB;
		case "EB":
			return RB.get("EB");
		case "EIB":
			return RB.get("EIB");
		case "ERB":
			return RB.get("ERB");
		case "MR":
			return RB.get("MR");
		case "ME":
			return RB.get("ME");
		case "MER":
			return RB.get("MEr");
		case "OE":
			return RB.get("OE");
		case "OS":
			return RB.get("OS");
		case "STB":
			return RB.get("STB");
		case "VIA":
			return RB.get("VIA");
		case "HLB":
			return RB.get("HLB");
		case "CAN":
			return RB.get("CAN");
		case "LYN":
			return RB.get("LYN");
		case "NEG":
			return RB.get("NEG");
		case "NWB":
			return RB.get("NWB");
		case "PAGA":
			return RB.get("PAGA");
    case "DNR":
      return RB.get("DNR");

		case "SDZ":
			return SDZ;
		case "DPE":
			return SDZ.get("DPE");

		case "CSQ":
			return CSQ;
		case "IRC":
			return IRC;
		case "AZ":
			return IRC.get("AZ");
		case "CB":
			return GZ.get("CB");
		case "CBZ":
			return GZ.get("CBZ");
		case "CFA":
			return GZ.get("CFA");
		case "CFN":
			return GZ.get("CFN");
		case "CIL":
			return GZ.get("CIL");
		case "CHL":
			return GZ.get("CHL");
		case "CL":
			return GZ.get("CL");
		case "CS":
			return GZ.get("CS");
		case "CT":
			return GZ.get("CT");
		case "DFG":
			return GZ.get("DFG");
		case "DGS":
			return GZ.get("DGS");
		case "DGX":
			return GZ.get("DGX");
		case "DGZ":
			return GZ.get("DGZ");
		case "DNG":
			return GZ.get("DNG");
		case "EUC":
			return GZ.get("EUC");
		case "FE":
			return GZ.get("FE");
		case "FBZ":
			return GZ.get("FBZ");
		case "FIR":
			return GZ.get("FIR");
		case "FR":
			return GZ.get("FR");
		case "FS":
			return GZ.get("FS");
		case "FX":
			return GZ.get("FX");
		case "FZ":
			return GZ.get("FZ");
		case "FZT":
			return GZ.get("FZT");
		case "GAG":
			return GZ.get("GAG");
		case "GC":
			return GZ.get("GC");
		case "ICG":
			return GZ.get("ICG");
		case "ICL":
			return GZ.get("ICL");
		case "IKE":
			return GZ.get("IKE");
		case "IKL":
			return GZ.get("IKL");
		case "IKS":
			return GZ.get("IKS");
		case "KC":
			return GZ.get("KC");
		case "KCL":
			return GZ.get("KCL");
		case "MCT":
			return GZ.get("MCT");
		case "RC":
			return GZ.get("RC");
		case "NG":
			return GZ.get("NG");
		case "PIC":
			return GZ.get("PIC");
		case "TC":
			return GZ.get("TC");
		case "TEC":
			return GZ.get("TEC");
		case "TGC":
			return GZ.get("TGC");
		case "TKC":
			return GZ.get("TKC");
		case "TKE":
			return GZ.get("TKE");
		case "TRC":
			return GZ.get("TRC");
		case "ROLA":
			return GZ.get("ROLA");

		case "LR":
			return LR;
		case "LS":
		case "LICE":
		case "L-ICE":
			return LR.get(string);
		case "S":
			return S;

		case "ERSATZLOK":
		case "LOK":
			return LOK.get(string);
		case "RLOK":
			return LOK.get("RLok");

		case "BAUZ":
		case "Bauz":
			return BAUZ;
		case "TFZ":
			return TFZ;
		case "Tfz":
			return TFZ;
		case "TFZL":
			return TFZ.get("TFZL");
		case "L":
			return RF.get("L");
		case "LRE":
			return RF.get("LRE");
			
		case "RF":
			return RF;
		case "RA":
    case "RABT":
			return RF.get("RA");
		case "R":
			return RF.get("R");
		case "SCHADW":
			return RF.get(string);
			
		case "HILFSZ":
			return HILFSZ;

		default:
			return OTHER.get(string);
		}
	}

	private TrainType get(final String name) {
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

	public static Iterator<TrainType> valueOfBase(final String string) {
		final TrainType tt;
		final ArrayList<TrainType> l = new ArrayList<>();
		if (!string.endsWith("*")) {
			tt = valueOf(string);
			if (tt != null)
				l.add(tt);
		} else {
			tt = valueOf(string.substring(0, string.length() - 1));
			if (tt != null) {
				l.add(tt);
				l.addAll(tt.map.values());
			}
		}
		return l.iterator();
	}

	public static TrainType valueOf(final String string) {
		return TrainType.valueMap.get(string);
	}

	public TrainType getBase() {
		return this.base;
	}

}
