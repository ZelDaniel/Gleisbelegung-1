package org.gleisbelegung.sts;

import org.gleisbelegung.database.StSDataInterface;
import org.gleisbelegung.xml.XML;

/**
 * Representation of Zug.
 *
 * Every train is referred by an unique ID. Methods calling the constructor have
 * to ensure that same ID results in the same train object.
 */
public class Train implements Comparable<Train> {

	private final Integer id;
	private final String name;
	private final TrainType name_traintype;
	private final int name_trainid;
	private Schedule schedule;
	private Details details;
	private Train succ;
	private Train pred;
	private Plattform lastArrived;

	private Event registeredEvent;

	private Train(final Integer id, final String name) {
		this.id = id;
		this.name = name;
		int delim = this.name.lastIndexOf(" ");
		if (delim < 0) {
			this.name_trainid =
					Integer.parseInt(name.replaceAll("[A-Za-z]*", ""));
			this.name_traintype =
					TrainType.create(name.replaceAll("[^A-Za-z]*", ""));
		} else {
			this.name_trainid = Integer.parseInt(name.substring(delim + 1));
			this.name_traintype = TrainType.create(name.substring(0, delim));
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
		int cmpType = this.name_traintype.compareTo(o.name_traintype);
		if (o.name_traintype == this.name_traintype || cmpType == 0) {
			int cmpId = this.name_trainid - o.name_trainid;
			if (cmpId == 0)
				cmp = this.id.intValue() - o.id.intValue();
			else
				cmp = cmpId;
		} else
			cmp = cmpType;
		return cmp;
	}

	public String formatDelay() {
		return String.format(
				"%s %3d", this.details.delay <= 0
						? this.details.delay == 0 ? " " : "-" : "+",
				Math.abs(this.details.delay));
	}

	public Details getDetails() {
		return this.details;
	}

	/**
	 * @return the ID of this train
	 */
	public Integer getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getNameId() {
		return this.name_trainid;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

	public Train getSuccessor() {
		return this.succ;
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

	// E-/ K-flag
	void setSuccesor(final Train t) {
		if (this.details != null) {
			this.details.target = null;
		}
		this.succ = t;
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

	public boolean updateByDetails(final Details details) {
		final Schedule schedule = this.schedule;
		if (this.details.source == null || this.pred != null) {
			details.source = null;
		}
		if (this.details.target == null || this.succ != null) {
			details.target = null;
		}
		assert schedule != null;
		this.details = details;
		if (schedule != null) {
			if (schedule.updatePos(details.plattform)) {
				if (details.atPlattform) {
					final ScheduleFlags flags = schedule.getFlags();
					if (flags.getE() != null) {
						flags.getE().details.setVisible();
					}
				}
				return true;
			}
		}
		return false;
	}

	public void updateByEvent(final Event event) {
		if (this.details.source == null || this.pred != null) {
			event.getDetails().source = null;
		}
		if (this.details.target == null || this.succ != null) {
			event.getDetails().target = null;
		}
		this.details = event.getDetails();
		this.schedule.updatePos(event.getPlattform());
		switch (event.getType()) {
		case ARRIVAL:
			this.lastArrived = event.getPlattform();
			final Train eTrain = schedule.getCurrentEntry().getFlags().getE();
			final Train fTrain = schedule.getCurrentEntry().getFlags().getF();
			if (eTrain != null) {
				eTrain.details.source = details.source;
				eTrain.updateByEvent(event);
			}
			if (fTrain != null) {
				fTrain.details.source = details.source;
				fTrain.updateByEvent(event);
			}
			break;
		case DEPARTURE:
			if (this.details.atPlattform)
				break;
			if (this.lastArrived != null
					& this.details.plattform == this.lastArrived) {
				this.schedule.advance();
			}
			final Train kTrain = schedule.getCurrentEntry().getFlags().getK();
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

	/**
	 * Callback from Trainlist that this instance is no longer needed.
	 *
	 * @param stsHandler
	 */
	void removedFromList(StSDataInterface stsHandler)
	{
		// TODO notify observers once implemented
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
			this.details.source = null;
		}
		this.pred = t;
	}

	public Event.EventType getRegisteredEventType() {
		if (null == registeredEvent) {
			return Event.EventType.NULL;
		}
		return registeredEvent.getType();
	}
}
