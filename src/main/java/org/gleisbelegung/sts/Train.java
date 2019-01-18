package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsPlatformInterface;
import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of Zug.
 * <p>
 * Every train is referred by an unique ID. Methods calling the constructor have
 * to ensure that same ID results in the same train object.
 */
public class Train implements Comparable<Train>, StsTrainInterface {

    private static final Pattern trainNamePattern = Pattern.compile("^([^0-9]*)( )?([0-9]+)");
    private final Integer id;
    private final String name;
    private final TrainType name_traintype;
    private final int name_trainid;
    private Schedule schedule;
    private Details details;
    private Train succ;
    private Train pred;
    private StsPlatformInterface lastArrived;

    protected Train(final Integer id, final String name) {
        this.id = id;
        this.name = name;
        final Matcher matcher = trainNamePattern.matcher(name);
        if (matcher.matches()) {
            this.name_trainid = Integer.parseInt(matcher.group(3));
            this.name_traintype =
                    TrainType.create(matcher.group(1));
        } else {
            this.name_trainid = 0;
            this.name_traintype = TrainType.create(name);
        }

    }

    static Train createFromXML(final XML xml) {
        final Integer id = Integer.valueOf(xml.get("zid"));
        final String name = xml.get("name");
        assert id != null;
        assert name != null;
        return new Train(id, name);
    }

    @Override
    public int compareTo(final Train o) {
        final int cmp;
        final int cmpType = this.name_traintype.compareTo(o.name_traintype);
        if (o.name_traintype == this.name_traintype || cmpType == 0) {
            final int cmpId = this.name_trainid - o.name_trainid;
            if (cmpId == 0) {
                cmp = this.id.intValue() - o.id.intValue();
            } else {
                cmp = cmpId;
            }
        } else {
            cmp = cmpType;
        }
        return cmp;
    }

    public String formatDelay() {
       return this.details.formatDelay();
    }

    @Override
    public Details getDetails() {
        return this.details;
    }

    /**
     * @return the ID of this train
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getNameId() {
        return this.name_trainid;
    }

    @Override
    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(final Schedule schedule) {
        this.schedule = schedule;
    }

    public Train getSuccessor() {
        return this.succ;
    }

    // E-/ K-flag
    void setSuccessor(final Train t) {
        if (this.details != null) {
            this.details.invalidateTarget();
        }
        this.succ = t;
    }

    @Override
    public <T extends StsTrainInterface> void setSuccessor(final T successor) {
        if (Train.class.isAssignableFrom(successor.getClass())) {
            this.setSuccessor((Train) successor);
        }
    }

    public TrainType getType() {
        return this.name_traintype;
    }

    /**
     * Updates the position in the schedule according to details
     *
     * @param details
     */
    public void setPosition(final Details details) {
        final Schedule schedule = this.schedule;
        this.details = details;

        if (schedule != null) {
            schedule.setPos(details);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("javadoc")
    @Override
    public String toString() {
        return this.id.toString() + ":" + this.name;
    }

    public boolean updateByDetails(final StsTrainDetailsInterface details) {
        if (Details.class.isAssignableFrom(details.getClass())) {
            return updateByDetails((Details) details);
        }

        throw new UnsupportedOperationException();
    }

    public boolean updateByDetails(final Details details) {
        final Schedule schedule = this.schedule;
        if (!this.details.isSourceValid() || this.pred != null) {
            details.invalidateSource();
        }
        if (!this.details.isTargetValid() || this.succ != null) {
            details.invalidateTarget();
        }
        assert schedule != null;
        this.details = details;
        if (schedule != null) {
            if (schedule.updatePos(details.getPlatform())) {
                if (details.isAtPlatform()) {
                    final ScheduleFlags flags = schedule.getFlags();
                    if (flags.getE() != null) {
                        ((Train) flags.getE()).details.setVisible();
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void updateByEvent(final Event event) {
        updateByDetails(event.getDetails());

        switch (event.getType()) {
            case ARRIVAL:
                this.lastArrived = event.getDetails().getPlatform();
                final Train eTrain = (Train) this.schedule.getCurrentEntry().getFlags().getE();
                final Train fTrain = (Train) this.schedule.getCurrentEntry().getFlags().getF();
                if (eTrain != null) {
                    eTrain.details.copySource(this.details);
                    eTrain.updateByEvent(event);
                }
                if (fTrain != null) {
                    fTrain.details.copySource(this.details);
                    fTrain.updateByEvent(event);
                }
                break;
            case DEPARTURE:
                if (this.details.isAtPlatform()) {
                    break;
                }
                if (this.lastArrived != null
                        & this.details.getPlatform() == this.lastArrived) {
                    this.schedule.advance();
                }
                final Train kTrain = (Train) this.schedule.getCurrentEntry().getFlags().getK();
                if (kTrain != null) {
                    kTrain.details.setInvisible();
                }
                break;
            case ENTER:
                break;
            case EXIT:
                break;
            default:
                break;

        }
    }

    private void updateRemovedPredecessor(final Train removedTrain) {
        if (this.pred == removedTrain) {
            if (!this.pred.details.isVisible()) {
                this.pred.details.setVisible();
            }
        }
        this.schedule.getFlags().invalidateG(removedTrain);
    }

    public Train getPredecessor() {
        return this.pred;
    }

    // E- / F-flag
    void setPredecessor(final Train t) {
        if (this.details != null) {
            this.details.invalidateSource();
        }
        this.pred = t;
    }

    @Override
    public <T extends StsTrainInterface> void setPredecessor(final T predecessor) {
        if (Train.class.isAssignableFrom(predecessor.getClass())) {
            this.setPredecessor((Train) predecessor);
        }
    }
}
