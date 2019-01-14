package org.gleisbelegung.sts;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gleisbelegung.xml.XML;


public class ScheduleFlags {

    public static final ScheduleFlags EMPTY = new ScheduleFlags();

    private boolean r;
    private boolean d;
    private boolean a;
    private boolean w, l; // either or semantics, not both may be active
    private Train k;
    private Train k_reverse; // helper Flag of k
    private Train e;
    private Train f;
    private Integer eId;
    private Integer fId;
    private Integer kId;

    private ScheduleFlags() {
    }

    private ScheduleFlags(final String init, final Train train,
            final Map<Integer, Train> trains, final Set<Integer> missingIDs,
            final Plattform plattform, int arr) {
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
                        default:
                    }

                    break;

                case 'F':
                    final int startFPos = init.indexOf('(', ++i) + 1;
                    final int endFPos = init.indexOf(')', startFPos);
                    final String f = init.substring(startFPos, endFPos);
                    this.fId = Integer.valueOf(f);
                    this.f = trains.get(this.fId);
                    if (this.f == null || this.f.getDetails() == null) {
                        missingIDs.add(this.fId);
                    } else if (train.getDetails() == null) {
                        missingIDs.add(train.getID());
                    } else {
                        this.f.setPredecessor(train);
                        // TODO set f_reverse
                    }
                    i = endFPos;
                    continue;

                case 'K':
                    final int startKPos = init.indexOf('(', ++i) + 1;
                    final int endKPos = init.indexOf(')', startKPos);
                    final String k = init.substring(startKPos, endKPos);
                    this.kId = Integer.valueOf(k);
                    this.k = trains.get(this.kId);
                    if (this.k == null || this.k.getDetails() == null) {
                        missingIDs.add(this.kId);
                    } else if (train.getDetails() == null) {
                        missingIDs.add(train.getID());
                    } else {
                        train.setSuccesor(this.k);
                        final Schedule kSchedule = this.k.getSchedule();
                        ScheduleEntry lastMatch = null;
                        if (kSchedule == null) {
                            missingIDs.add(this.kId);
                        } else {
                            for (final Iterator<ScheduleEntry> iter =
                                    kSchedule.iterator(); iter.hasNext(); ) {
                                final ScheduleEntry kse = iter.next();
                                if (kse.getPlattformPlanned()
                                        .equals(plattform)) {
                                    final int kDep = kse.getDepature();
                                    if (kDep < arr) {
                                        break;
                                    }
                                    lastMatch = kse;
                                }
                            }
                        }
                        if (lastMatch != null) {
                            lastMatch.getFlags().k_reverse = train;
                        }
                    }
                    i = endKPos;
                    continue;

                case 'E':
                    final int startEPos = init.indexOf('(', ++i) + 1;
                    final int endEPos = init.indexOf(')', startEPos);
                    final String e = init.substring(startEPos, endEPos);
                    this.eId = Integer.valueOf(e);
                    this.e = trains.get(this.eId);
                    if (this.e == null || train.getDetails() == null | this.e.getDetails() == null) {
                        missingIDs.add(this.eId);
                    } else {
                        train.setSuccesor(this.e);
                        this.e.setPredecessor(train);
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

    public static ScheduleFlags parse(final XML xml, final Train train,
            final Map<Integer, Train> trains, final Set<Integer> missingIDs,
            Plattform plattform, int arr) {
        if (!xml.getKey().equals("gleis")) {
            throw new IllegalArgumentException();
        }
        final ScheduleFlags flags = new ScheduleFlags(xml.get("flags"), train,
                trains, missingIDs, plattform, arr);
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

    public Train getE() {
        return this.e;
    }

    public Train getF() {
        return this.f;
    }

    public Train getK() {
        return this.k;
    }

    public boolean getL() {
        return this.l;
    }

    public boolean getR() {
        return this.r;
    }

    public boolean getW() {
        return this.w;
    }

    public Integer getsEID() {
        return this.eId;
    }

    public boolean getD() {
        return this.d;
    }

    public void invalidateG(final Train train) {
        if (this.k_reverse == train) {
            this.k_reverse = null;
            if (train.getID() < 0) {
                this.l = this.w = false;
            }
        }

    }
}
