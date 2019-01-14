package org.gleisbelegung.sts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.gleisbelegung.xml.XML;


public class ScheduleEntry {

    public static final ScheduleEntry EMPTY =
            new ScheduleEntry(Plattform.EMPTY, Plattform.EMPTY, 0, 0, ScheduleFlags.EMPTY);
    private final int arrival, depature;
    private final ScheduleFlags flags;
    private Plattform plattform;
    private final Plattform plattformPlanned;

    private ScheduleEntry(final Plattform plattformPlanned, final Plattform plattformActual, final int depature,
            final int arrival, final ScheduleFlags flags) {
        this.plattform = plattformActual;
        this.plattformPlanned = plattformPlanned;
        this.depature = depature;
        this.arrival = arrival;
        this.flags = flags;
    }

    private static long getTimeDiff(long timeNow, int time, int delay) {
        long diff;
        if (time == 0) {
            return Long.MAX_VALUE;
        }
        diff = TimeUnit.MINUTES.toMillis(time + delay) - timeNow;
        return diff;
    }

    private static String timeDiffToString(long diff, boolean neg) {
        if (diff == Long.MAX_VALUE) {
            return "";
        }
        final String signum;
        if (diff < 0) {
            diff = -diff;
            signum = neg ? " " : "+";
        } else {
            signum = neg ? "-" : " ";
        }
        if (diff == 0) {
            return "";
        }
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(diff) % 60);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(diff));
        if (minutes / 60 >= 1000) {
            return " - - - -";
        }
        if (minutes >= 100) {
            return signum + String.format(" %2d.%02d", minutes / 60,
                    minutes % 60 * 100 / 60);
        }
        return signum + String.format(" %02d:%02d", minutes % 100, seconds);
    }

    public static ScheduleEntryParseResult parse(final List<XML> entriesXML,
            final Train train, final Map<Integer, Train> trains) {
        final List<ScheduleEntry> entries = new ArrayList<>(entriesXML.size());
        final Map<ScheduleEntry, Set<Integer>> missingIds = new HashMap<>();
        for (final XML entryXML : entriesXML) {
            Set<Integer> referredIds = new HashSet<>();
            final ScheduleEntry e =
                    ScheduleEntry.parse(entryXML, train, trains, referredIds);
            entries.add(e);
            missingIds.put(e, referredIds);
        }
        return new ScheduleEntryParseResult(entries, missingIds);
    }

    public static void updateWithExisting(final List<XML> entriesXML,
            final Train train, Schedule existing) {
        ScheduleEntryParseResult result = parse(entriesXML, train, null);

        // find the first entry of result within existing
        Iterator<ScheduleEntry> existingIter = existing.iterator();
        Iterator<ScheduleEntry> resultIter = result.entries.iterator();
        ScheduleEntry firstOfResultInExisting = null;
        if (resultIter.hasNext()) {
            ScheduleEntry resultEntry = resultIter.next();
            while (existingIter.hasNext()) {
                ScheduleEntry next = existingIter.next();
                if (next.arrival == resultEntry.arrival) {
                    firstOfResultInExisting = next;
                    break;
                }
            }
            firstOfResultInExisting.updatePlattform(resultEntry);

            while (existingIter.hasNext() && resultIter.hasNext()) {
                existingIter.next().updatePlattform(resultIter.next());
            }
        }
    }

    private static ScheduleEntry parse(final XML xml, final Train train,
            final Map<Integer, Train> trains, final Set<Integer> missingIDs) {
        if (!xml.getKey().equals("gleis")) {
            throw new IllegalArgumentException();
        }
        final String plan = xml.get("plan");
        final String plattform = xml.get("name");
        final String depS = xml.get("ab");
        final String arrS = xml.get("an");
        final Plattform pPlan = Plattform.get(plan);
        final Plattform pActual = Plattform.get(plattform);
        final int dep, arr;
        if ((depS == null) || depS.isEmpty()) {
            dep = 0;
        } else {
            dep = ScheduleEntry.timeToMinutes(depS);
        }
        if ((arrS == null) || arrS.isEmpty()) {
            arr = 0;
        } else {
            arr = ScheduleEntry.timeToMinutes(arrS);
        }
        final ScheduleFlags flags =
                ScheduleFlags.parse(xml, train, trains, missingIDs, pPlan, arr);

        return new ScheduleEntry(pPlan, pActual, dep, arr, flags);
    }

    private static int timeToMinutes(final String s) {
        final String parts[] = s.split(":");
        return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
    }

    public ScheduleFlags getFlags() {
        return this.flags;
    }

    public Plattform getPlattform() {
        return this.plattform;
    }

    public Plattform getPlattformPlanned() {
        return this.plattformPlanned;
    }

    public String getTimeDiffArrival(long timeMillis, int delay) {

        return timeDiffToString(getTimeDiffArrivalMillis(timeMillis, delay),
                false);
    }

    public long getTimeDiffArrivalMillis(long timeMillis, int delay) {
        if (this == EMPTY) {
            return Long.MAX_VALUE;
        }
        return getTimeDiff(timeMillis, this.arrival, delay);
    }

    public String getTimeDiffDepature(long timeMillis, int delay,
            boolean atPlattform, boolean currentStop) {
        if (this == EMPTY) {
            return "";
        }
        if (currentStop && atPlattform && delay != 0) {
            return getTimeDiffDepature(timeMillis, 0, true, true);
        }
        int haltTime = this.depature - this.arrival;
        long diff = getTimeDiff(timeMillis, this.depature,
                Math.max(delay - haltTime, 0));
        return timeDiffToString(diff, !atPlattform && currentStop);
    }

    @Override
    public String toString() {
        return this.plattform.toString() + "(" + this.flags.toString() + ")";
    }

    public long getTimeDiffDepatureMillis(long timeMillis, int delay) {
        return getTimeDiff(timeMillis, this.depature, delay);
    }

    /**
     * @return Time of Depature in minutes since 00:00
     */
    public int getDepature() {
        return this.depature;
    }

    /**
     * @return Time of Arrival in minutes since 00:00
     */
    public int getArrival() {
        return this.arrival;
    }

    private void updatePlattform(ScheduleEntry recent) {
        if (this.plattform != recent.plattform) {
            System.err.printf("Replacing %s by %s\n", this.plattform, recent.plattform);
            this.plattform = recent.plattform;
        }
    }

    static class ScheduleEntryParseResult {

        final List<ScheduleEntry> entries;
        final Map<ScheduleEntry, Set<Integer>> missing;

        public ScheduleEntryParseResult(final List<ScheduleEntry> entries,
                final Map<ScheduleEntry, Set<Integer>> complete) {
            this.entries = entries;
            this.missing = complete;
        }
    }
}
