package org.gleisbelegung.sts;

import java.util.HashMap;
import java.util.Map;

import org.gleisbelegung.xml.XML;


/**
 * Representation of event
 */
public class Event {

    private final EventType type;
    private final Train train;
    private final long time;
    private final Details details;

    Event(final EventType type, final Train train, final Details details) {
        this.type = type;
        this.train = train;
        this.details = details;
        this.time = 0;
    }

    Event(final EventType type, final Train train, final long time) {
        this.type = type;
        this.train = train;
        this.time = time;
        this.details = train.getDetails();
    }

    private Event(Event event, Plattform plattform) {
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
    public static Event parse(final XML xml, final Train train) {
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

    public Details getDetails() {
        return this.details;
    }

    Plattform getPlattform() {
        return this.details.plattform;
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

        public Event createEvent(final Train train) {
            return new Event(this, train, System.currentTimeMillis());
        }

        Event createEvent(final Train train, final Details details) {
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
