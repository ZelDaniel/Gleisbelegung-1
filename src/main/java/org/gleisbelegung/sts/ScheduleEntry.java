package org.gleisbelegung.sts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.gleisbelegung.xml.XML;


public class ScheduleEntry {

	static class ScheduleEntryParseResult {

		final List<ScheduleEntry> entries;
		final Set<Integer> missing;

		public ScheduleEntryParseResult(final List<ScheduleEntry> entries,
				final Set<Integer> complete) {
			this.entries = entries;
			this.missing = complete;
		}
	}

	public static final ScheduleEntry EMPTY =
			new ScheduleEntry(Plattform.EMPTY, 0, 0, ScheduleFlags.EMPTY);

	private static long getTimeDiff(long timeNow, int time, int delay) {
		long diff;
		if (time == 0)
			return Long.MAX_VALUE;
		diff = TimeUnit.MINUTES.toMillis(time + delay) - timeNow;
		return diff;
	}

	private static String timeDiffToString(long diff, boolean neg) {
		if (diff == Long.MAX_VALUE)
			return "";
		final String signum;
		if (diff < 0) {
			diff = -diff;
			signum = neg ? " " : "+";
		} else
			signum = neg ? "-" : " ";
		if (diff == 0)
			return "";
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
			final Train train, final Trainlist trains) {
		final List<ScheduleEntry> entries = new ArrayList<>(entriesXML.size());
		Set<Integer> referredIds = new HashSet<>();
		for (final XML entryXML : entriesXML) {
			final ScheduleEntry e =
					ScheduleEntry.parse(entryXML, train, trains, referredIds);
			entries.add(e);
		}
		return new ScheduleEntryParseResult(entries, referredIds);
	}

	private static ScheduleEntry parse(final XML xml, final Train train,
			final Trainlist trains, final Set<Integer> missingIDs) {
		if (!xml.getKey().equals("gleis")) {
			throw new IllegalArgumentException();
		}
		final String plan = xml.get("plan");
		final String plattform = xml.get("name");
		final String depS = xml.get("ab");
		final String arrS = xml.get("an");
		final Plattform pf = Plattform.get(plan, plattform);
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
				ScheduleFlags.parse(xml, train, trains, missingIDs, pf, arr);

		return new ScheduleEntry(pf, dep, arr, flags);
	}

	private static int timeToMinutes(final String s) {
		final String parts[] = s.split(":");
		return (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
	}

	private final int arrival, depature;

	private final ScheduleFlags flags;

	private final Plattform plattform;

	private ScheduleEntry(final Plattform plattform, final int depature,
			final int arrival, final ScheduleFlags flags) {
		this.plattform = plattform;
		this.depature = depature;
		this.arrival = arrival;
		this.flags = flags;
	}

	public ScheduleFlags getFlags() {
		return this.flags;
	}

	public Plattform getPlattform() {
		return this.plattform;
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
	 * 
	 * @return Time of Depature in minutes since 00:00
	 */
	public int getDepature() {
		return this.depature;
	}

	/**
	 * 
	 * @return Time of Arrival in minutes since 00:00
	 */
	public int getArrival() {
		return this.arrival;
	}
}
