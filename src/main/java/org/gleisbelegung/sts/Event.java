package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.database.StsTrainInterface;
import org.gleisbelegung.xml.XML;


/**
 * Representation of event
 */
public class Event {

    private final EventType type;
    private final StsTrainInterface train;
    private final long time;
    private final StsTrainDetailsInterface details;

    Event(final EventType type, final StsTrainInterface train, final Details details) {
        this.type = type;
        this.train = train;
        this.details = details;
        this.time = 0;
    }

    Event(final EventType type, final StsTrainInterface train, final long time) {
        this.type = type;
        this.train = train;
        this.time = time;
        this.details = train.getDetails();
    }

    private Event(final Event event, final Platform platform) {
        this.type = event.type;
        this.train = event.train;
        this.time = event.time;
        this.details = event.details;
    }

    /**
     * @param xml XML to parse
     * @param train Train for which given event has been generated
     * @return Event describing new state of given train
     */
    public static Event parse(final XML xml, final StsTrainInterface train) {
        if (!xml.getKey().equals("ereignis")) {
            throw new IllegalArgumentException();
        }
        final EventType type;
        switch (xml.get("art")) {
            case "einfahrt":
                type = EventType.ENTER;
                break;
            case "ankunft":
                type = EventType.ARRIVAL;
                break;
            case "abfahrt":
                type = EventType.DEPARTURE;
                break;
            case "ausfahrt":
                type = EventType.EXIT;
                break;
            default:
                return null;
        }
        final Details details = Details.parse(xml.setKey("zugdetails"));
        return type.createEvent(train, details);
    }

    public StsTrainDetailsInterface getDetails() {
        return this.details;
    }

    public long getTime() {
        return this.time;
    }

    public EventType getType() {
        return this.type;
    }

    public String toString() {
        return this.type.toString();
    }

    /**
     *
     */
    public enum EventType {
        /**
         * initial value
         */
        NONE(null),
        /**
         * no event to expect
         */
        NULL(null),
        /**
         * train is at platform and is about to leave
         */
        DEPARTURE("abfahrt"),
        /**
         * train arrives at next platform
         */
        ARRIVAL("ankunft"),
        /**
         * train will leave the facility
         */
        EXIT("ausfahrt"),
        /**
         * train is not yet visible
         */
        ENTER("einfahrt");

        final String key;

        EventType(final String key) {
            this.key = key;
        }


        Event createEvent(final StsTrainInterface train, final Details details) {
            return new Event(this, train, details);
        }

        /**
         * @return the id for given EventType to use for registrating and
         * handling events in StS
         */
        public String getKey() {
            return this.key;
        }
    }
}
