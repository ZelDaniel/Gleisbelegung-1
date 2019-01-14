package org.gleisbelegung.sts;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gleisbelegung.xml.XML;

/**
 * Represents zugfahrplan.
 * <p>
 * After parsing the schedule the first time, the instance will be kept and the internal pointer moved to indicate the
 * current position.
 */
public class Schedule implements Iterable<ScheduleEntry> {

    private final List<ScheduleEntry> entries;
    private final Train train;
    private int pos = 0;

    private Schedule(final List<ScheduleEntry> entries, final Train train) {
        this.entries = entries;
        this.train = train;
    }

    public static Schedule parse(final XML xml, final Train train,
            final Map<Integer, Train> trains
    ) {
        if (!xml.getKey().equals("zugfahrplan")) {
            throw new IllegalArgumentException();
        }
        final List<XML> entriesXML = xml.getInternXML();
        final ScheduleEntry.ScheduleEntryParseResult result =
                ScheduleEntry.parse(entriesXML, train, trains);
        final List<ScheduleEntry> entries = result.entries;
        final Schedule schedule = new Schedule(entries, train);
        return schedule;
    }

    public ScheduleEntry getCurrentEntry() {
        if (this.pos < 0) {
            return ScheduleEntry.EMPTY;
        }
        return this.entries.get(this.pos);
    }

    public ScheduleFlags getFlags() {
        if (this.pos < 0) {
            return ScheduleFlags.EMPTY;
        }
        return this.entries.get(this.pos).getFlags();
    }

    /**
     * @return
     */
    public ScheduleEntry getNextEntry() {
        if (this.pos < 0) {
            // after last stop
            return ScheduleEntry.EMPTY;
        }
        if (this.pos + 1 == this.entries.size()) {
            // before last stop
            return ScheduleEntry.EMPTY;
        }
        return this.entries.get(this.pos + 1);
    }

    /**
     * Returns the next entry where the L flag is set
     *
     * @return
     */
    public ScheduleEntry getNextL() {
        if (this.pos < 0) {
            return null;
        }
        for (int i = this.pos; i < this.entries.size(); ++i) {
            final ScheduleEntry se = this.entries.get(i);
            if (se.getFlags().getL()) {
                return se;
            }
        }
        return null;
    }

    /**
     * Returns the next entry where the W flag is set
     *
     * @return
     */
    public ScheduleEntry getNextW() {
        if (this.pos < 0) {
            return null;
        }
        for (int i = this.pos; i < this.entries.size(); ++i) {
            final ScheduleEntry se = this.entries.get(i);
            if (se.getFlags().getW()) {
                return se;
            }
        }
        return null;
    }

    /**
     * Returns the entry one position before current position
     *
     * @return
     */
    public ScheduleEntry getPrevEntry() {
        if (this.pos < 0) {
            return this.entries.get(this.entries.size() - 1);
        }
        if (this.pos == 0) {
            return ScheduleEntry.EMPTY;
        }
        return this.entries.get(this.pos - 1);
    }

    @Override
    public Iterator<ScheduleEntry> iterator() {
        return this.entries.iterator();
    }

    /**
     * Updates the pointer for current position
     *
     * @param details
     */
    public void setPos(final Details details) {
        for (int i = 0; i < this.entries.size(); ++i) {
            if (this.entries.get(i).getPlattform() == details.plattform) {
                this.pos = i;
                return;
            }
        }
        assert details.plattform == Plattform.EMPTY;
        this.pos = -1;
    }

    public boolean updatePos(final Plattform pl) {
        for (int i = this.pos; i >= 0 && i < this.entries.size(); ++i) {
            if (this.entries.get(i).getPlattform().equals(pl)) {
                if (this.pos == i) {
                    return false;
                }
                this.pos = i;
                return true;
            }
        }
        for (int i = this.entries.size(); --i >= 0; ) {
            if (this.entries.get(i).getPlattform().equals(pl)) {
                this.pos = i;
                return true;
            }
        }
        if (this.pos != -1) {
            this.pos = -1;
            return true;
        }
        return false;
    }

    /**
     * Sets internal pointer to next position
     */
    public void advance() {
        if (this.pos + 1 >= this.entries.size()) {
            this.pos = -1;
        } else {
            this.pos++;
        }
    }

    public ScheduleEntry getLastEntry() {
        return this.entries.get(this.entries.size() - 1);
    }

    public ScheduleEntry getFirstEntry() {
        return this.entries.get(0);
    }

    public void updateByXml(XML xml) {
        ScheduleEntry.updateWithExisting(xml.getInternXML(), train, this);
    }
}
