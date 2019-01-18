package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsPlatformInterface;
import org.gleisbelegung.database.StsScheduleEntryInterface;
import org.gleisbelegung.database.StsScheduleFlagsInterface;
import org.gleisbelegung.database.StsScheduleInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ScheduleFlags implements StsScheduleFlagsInterface {

    public static final ScheduleFlags EMPTY = new ScheduleFlags();

    private boolean r;
    private boolean d;
    private boolean a;
    private boolean w, l; // either or semantics, not both may be active
    private StsTrainInterface k;
    private StsTrainInterface k_reverse; // helper Flag of k
    private StsTrainInterface e;
    private StsTrainInterface f;
    private Integer eId;
    private Integer fId;
    private Integer kId;

    protected ScheduleFlags() {
    }

    private ScheduleFlags(final String init, final StsTrainInterface train,
            final Map<Integer, ? extends StsTrainInterface> trains, final Set<Integer> missingIds,
            final StsScheduleEntryInterface schedule) {
        if (init == null) {
            return;
        }
        for (int i = 0; i < init.length(); ++i) {
            switch (init.charAt(i)) {
                case 'A':
                    this.a = true;
                    break;
                case 'D':
                    this.d = true;
                    break;
                case 'R':
                    this.r = true;
                    while (i + 1 < init.length() && init.charAt(i + 1) >= '0' && init.charAt(i + 1) <= '9') {
                        i++;
                    }
                    break;

                case 'W':
                case 'L':
                    switch (init.charAt(i)) {
                        case 'W':
                            this.w = true;
                            final int startW1Pos = init.indexOf('[', ++i);
                            final int startW2Pos = init.indexOf('[', startW1Pos + 1);
                            final int endWPos = init.indexOf(']', startW2Pos + 1);
                            i = endWPos;
                            break;
                        case 'L':
                            this.l = true;
                            break;
                    }
                    break;

                case 'F':
                    final int startFPos = init.indexOf('(', ++i) + 1;
                    final int endFPos = init.indexOf(')', startFPos);
                    final String f = init.substring(startFPos, endFPos);
                    this.fId = Integer.valueOf(f);
                    if (trains != null) {
                        this.f = trains.get(this.fId);
                        if (this.f == null || this.f.getDetails() == null) {
                            if (missingIds != null) {
                                missingIds.add(this.fId);
                            }
                        } else if (train.getDetails() == null) {
                            missingIds.add(train.getId());
                        } else {
                            this.f.setPredecessor(train);
                            // TODO set f_reverse
                        }
                    }
                    i = endFPos;
                    continue;

                case 'K':
                    final int startKPos = init.indexOf('(', ++i) + 1;
                    final int endKPos = init.indexOf(')', startKPos);
                    final String k = init.substring(startKPos, endKPos);
                    this.kId = Integer.valueOf(k);
                    if (trains != null) {
                        this.k = trains.get(this.kId);
                        if (this.k == null || this.k.getDetails() == null) {
                            missingIds.add(this.kId);
                        } else if (train.getDetails() == null) {
                            missingIds.add(train.getId());
                        } else {
                            train.setSuccessor(this.k);
                            final StsScheduleInterface kSchedule = this.k.getSchedule();
                            StsScheduleEntryInterface lastMatch = null;
                            if (kSchedule == null) {
                                if (missingIds != null) {
                                    missingIds.add(this.kId);
                                }
                            } else {
                                for (final Iterator<StsScheduleEntryInterface> iter =
                                        kSchedule.iterator(); iter.hasNext(); ) {
                                    final StsScheduleEntryInterface kse = iter.next();
                                    if (kse.getPlatformPlanned()
                                            .equals(schedule.getPlatformPlanned())) {
                                        final int kDep = kse.getDepature();
                                        if (kDep < schedule.getArrival()) {
                                            break;
                                        }
                                        lastMatch = kse;
                                    }
                                }
                            }
                            if (lastMatch != null) {
                                final StsScheduleFlagsInterface flagsOther = lastMatch.getFlags();
                                if (ScheduleFlags.class.isAssignableFrom(flagsOther.getClass())) {
                                    ((ScheduleFlags) lastMatch.getFlags()).k_reverse = train;
                                }
                            }
                        }
                    }
                    i = endKPos;
                    continue;

                case 'E':
                    final int startEPos = init.indexOf('(', ++i) + 1;
                    final int endEPos = init.indexOf(')', startEPos);
                    final String e = init.substring(startEPos, endEPos);
                    this.eId = Integer.valueOf(e);
                    if (trains != null) {
                        this.e = trains.get(this.eId);
                        if (this.e == null || train.getDetails() == null || this.e.getDetails() == null) {
                            missingIds.add(this.eId);
                        } else {
                            train.setSuccessor(this.e);
                            this.e.setPredecessor(train);
                        }
                    }
                    i = endEPos;
                    continue;

                case 'B':
                    // no relevance, multi-flag
                    while (i + 1 < init.length()) {
                        final char next = init.charAt(i + 1);
                        if ((next >= '0') && (next <= '9')) {
                            ++i;
                        } else {
                            break;
                        }
                    }
                    continue;

                case 'P':
                    // no relevance
                    if (init.length() > i + 1) {
                        if (init.charAt(i + 1) == '[') {
                            i = init.indexOf(']', i);
                        }
                    }
                    continue;

                case '[':
                    if (init.length() > i + 1) {
                        i = init.indexOf(']', i);
                    }
                    continue;

                default:
                    System.err.println("unparsed flag " + init.charAt(i));
            }
        }
    }

    public static ScheduleFlags parse(final XML xml, final StsTrainInterface train,
            final Map<Integer, ? extends StsTrainInterface> trains, final Set<Integer> missingIds,
            final StsScheduleEntryInterface scheduleEntry) {
        if (!xml.getKey().equals("gleis")) {
            throw new IllegalArgumentException();
        }
        final ScheduleFlags flags = new ScheduleFlags(xml.get("flags"), train,
                trains, missingIds, scheduleEntry);
        return flags;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.d) {
            sb.append("D");
        }
        if (this.a) {
            sb.append("A");
        }
        if (this.l) {
            sb.append("L");
        }
        if (this.w) {
            sb.append("W");
        }
        if (this.k != null) {
            sb.append("K");
        }
        if (this.k_reverse != null) {
            sb.append("G");
        }
        if (this.f != null) {
            sb.append("F");
        }
        return sb.toString();
    }

    public StsTrainInterface getE() {
        return this.e;
    }

    public StsTrainInterface getF() {
        return this.f;
    }

    public StsTrainInterface getK() {
        return this.k;
    }

    public boolean hasA() {
        return this.a;
    }

    public boolean hasE() {
        return this.eId != null;
    }

    public boolean hasF() {
        return this.fId != null;
    }

    public boolean hasK() {
        return this.kId != null;
    }

    public boolean hasL() {
        return this.l;
    }

    public boolean hasR() {
        return this.r;
    }

    public boolean hasW() {
        return this.w;
    }

    public boolean hasD() {
        return this.d;
    }

    public void invalidateG(final StsTrainInterface train) {
        if (this.k_reverse == train) {
            this.k_reverse = null;
            if (train.getId() < 0) {
                this.l = this.w = false;
            }
        }

    }
}
