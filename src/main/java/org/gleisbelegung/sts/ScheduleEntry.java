package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsPlatformInterface;
import org.gleisbelegung.database.StsScheduleEntryInterface;
import org.gleisbelegung.database.StsScheduleFlagsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class ScheduleEntry implements StsScheduleEntryInterface {

    public static final ScheduleEntry EMPTY =
            new ScheduleEntry(Platform.EMPTY, Platform.EMPTY, 0, 0, ScheduleFlags.EMPTY);
    private final int arrival, depature;
    private final ScheduleFlags flags;
    private final Platform platformPlanned;
    private Platform platform;

    ScheduleEntry(final Platform platformPlanned, final Platform platformActual, final int depature,
            final int arrival, final ScheduleFlags flags) {
        this.platform = platformActual;
        this.platformPlanned = platformPlanned;
        this.depature = depature;
        this.arrival = arrival;
        this.flags = flags;
    }

    static long getTimeDiff(final long timeNow, final int time, final int delay) {
        return TimeUnit.MINUTES.toMillis(time + delay) - timeNow;
    }

    static String timeDiffToString(long diff, final boolean neg) {
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
        final int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(diff) % 60);
        final int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(diff));
        if (minutes / 60 >= 1000) {
            return " - -- -";
        }
        if (minutes >= 100) {
            return signum + String.format(" %2d.%02d", minutes / 60,
                    minutes % 60 * 100 / 60);
        }
        return signum + String.format(" %02d:%02d", minutes % 100, seconds);
    }

    static ScheduleEntryParseResult parse(final List<XML> entriesXML,
            final StsTrainInterface train, final Map<Integer, ? extends StsTrainInterface> trains) {
        final List<ScheduleEntry> entries = new ArrayList<>(entriesXML.size());
        final Map<ScheduleEntry, Set<Integer>> missingIds = new HashMap<>();
        for (final XML entryXML : entriesXML) {
            final Set<Integer> referredIds = new HashSet<>();
            final ScheduleEntry e =
                    ScheduleEntry.parse(entryXML, train, trains, referredIds);
            entries.add(e);
            missingIds.put(e, referredIds);
        }
        return new ScheduleEntryParseResult(entries, missingIds);
    }

    public static void updateWithExisting(final List<XML> entriesXML,
            final StsTrainInterface train, final List<ScheduleEntry> existing) {
        final ScheduleEntryParseResult result = parse(entriesXML, train, null);

        // find the first entry of result within existing
        final Iterator<ScheduleEntry> existingIter = existing.iterator();
        final Iterator<ScheduleEntry> resultIter = result.entries.iterator();
        ScheduleEntry firstOfResultInExisting = null;
        if (resultIter.hasNext()) {
            final ScheduleEntry resultEntry = resultIter.next();
            while (existingIter.hasNext()) {
                final ScheduleEntry next = existingIter.next();
                if (next.getArrival() == resultEntry.arrival) {
                    firstOfResultInExisting = next;
                    break;
                }
            }
            if (firstOfResultInExisting == null) {
                return;
            }

            firstOfResultInExisting.updatePlattform(resultEntry);

            while (existingIter.hasNext() && resultIter.hasNext()) {
                existingIter.next().updatePlattform(resultIter.next());
            }
        }
    }

    private static ScheduleEntry parse(final XML xml, final StsTrainInterface train,
            final Map<Integer, ? extends StsTrainInterface> trains, final Set<Integer> missingIDs) {
        if (!xml.getKey().equals("gleis")) {
            throw new IllegalArgumentException();
        }
        final String plan = xml.get("plan");
        final String platform = xml.get("name");
        final String depS = xml.get("ab");
        final String arrS = xml.get("an");
        final Platform pPlan = Platform.get(plan);
        final Platform pActual = Platform.get(platform);
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
                ScheduleFlags.parse(xml);
        ScheduleEntry se = new ScheduleEntry(pPlan, pActual, dep, arr, flags);
        flags.linkWithEntry(se, train, trains);

        return se;
    }

    static int timeToMinutes(final String s) {
        final String[] parts = s.split(":");
        return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
    }

    @Override
    public ScheduleFlags getFlags() {
        return this.flags;
    }

    public Platform getPlatform() {
        return this.platform;
    }

    @Override
    public Platform getPlatformPlanned() {
        return this.platformPlanned;
    }

    public String getTimeDiffArrival(final long timeMillis, final int delay) {

        return timeDiffToString(this.getTimeDiffArrivalMillis(timeMillis, delay),
                false);
    }

    public long getTimeDiffArrivalMillis(final long timeMillis, final int delay) {
        if (this == EMPTY) {
            return Long.MAX_VALUE;
        }
        return getTimeDiff(timeMillis, this.arrival, delay);
    }

    public String getTimeDiffDepature(final long timeMillis, final int delay,
            final boolean atPlattform, final boolean currentStop) {
        if (this == EMPTY) {
            return "";
        }
        if (currentStop && atPlattform && delay != 0) {
            return this.getTimeDiffDepature(timeMillis, 0, true, true);
        }
        final int haltTime = this.depature - this.arrival;
        final long diff = getTimeDiff(timeMillis, this.depature,
                Math.max(delay - haltTime, 0));
        return timeDiffToString(diff, !atPlattform && currentStop);
    }

    @Override
    public String toString() {
        return this.platform.toString() + "(" + this.flags.toString() + ")";
    }

    public long getTimeDiffDepatureMillis(final long timeMillis, final int delay) {
        return getTimeDiff(timeMillis, this.depature, delay);
    }

    /**
     * @return Time of Depature in minutes since 00:00
     */
    @Override
    public int getDepature() {
        return this.depature;
    }

    /**
     * @return Time of Arrival in minutes since 00:00
     */
    @Override
    public int getArrival() {
        return this.arrival;
    }

    private void updatePlattform(final ScheduleEntry recent) {
        if (this.platform != recent.platform) {
            System.err.printf("Replacing %s by %s\n", this.platform, recent.platform);
            this.platform = recent.platform;
        }
    }

    /**
     * Helper class for {@link #parse(XML, StsTrainInterface, Map, Set)}
     */
    private static class StsScheduleEntry implements StsScheduleEntryInterface {

        int arr, dep;
        Platform platform;

        @Override
        public int getArrival() {
            return arr;
        }

        @Override
        public int getDepature() {
            return dep;
        }

        @Override
        public StsScheduleFlagsInterface getFlags() {
            return null;
        }

        @Override
        public StsPlatformInterface getPlatformPlanned() {
            return platform;
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
