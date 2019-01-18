package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsTrainDetailsInterface;
import org.gleisbelegung.xml.XML;

/**
 * Represents the information of zugdetails
 */
public class Details implements StsTrainDetailsInterface {

    private final String name;
    private final Platform platform;
    private final Platform platformPlanned;
    private final int delay;
    private String target;
    private String source;
    private boolean atPlatform;
    private long updated_at = System.currentTimeMillis();
    private boolean visible;

    private Details(final String name, final Platform platformPlanned, final Platform platform,
            final String target, final String source, final int delay,
            final boolean atPlatform, final boolean visible) {
        this.name = name;
        this.platform = platform;
        this.platformPlanned = platformPlanned;
        this.target = target.isEmpty() ? null : target;
        this.source = source.isEmpty() ? null : source;
        this.atPlatform = atPlatform;
        this.delay = delay;
        this.visible = visible;
    }

    public static Details parse(final XML xml) {
        if (!xml.getKey().equals("zugdetails")) {
            throw new IllegalArgumentException();
        }
        final String name = xml.get("name");
        final String plan = xml.get("plangleis");
        final String track = xml.get("gleis");
        final Platform platformPlanned = Platform.get(plan);
        final Platform platform = Platform.get(track);
        final String target = xml.get("nach");
        final String source = xml.get("von");
        final int delay = Integer.parseInt(xml.get("verspaetung"));
        final boolean atPlatform = Boolean.parseBoolean(xml.get("amgleis"));
        final boolean visible = Boolean.parseBoolean(xml.get("sichtbar"));
        return new Details(name, platformPlanned, platform, target, source, delay, atPlatform,
                visible);
    }

    public boolean isAtPlatform() {
        return this.atPlatform;
    }

    public String from() {
        return this.source;
    }

    public int getDelay() {
        return this.delay;
    }

    public String formatDelay() {
        return String.format(
                "%s %3d", this.delay <= 0
                        ? this.delay == 0 ? " " : "-" : "+",
                Math.abs(this.delay));
    }

    @Override
    public Platform getPlatform() {
        return this.platform;
    }

    public void setVisible() {
        this.visible = true;
        this.updated_at = System.currentTimeMillis();
    }

    public boolean equals(final Details o) {
        if (this.atPlatform ^ o.atPlatform || this.visible ^ o.visible) {
            return false;
        }
        if (this.source == null ^ o.source == null) {
            return false;
        }
        if (this.target == null ^ o.target == null) {
            return false;
        }
        if (this.source != null && !this.source.equals(o.source) || this.target != null && !this.target.equals(o.target)) {
            return false;
        }
        if (this.platform != o.platform || this.delay != o.delay) {
            return false;
        }
        return this.name.equals(o.name);

    }

    public void setInvisible() {
        this.visible = false;
    }

    public String to() {
        return this.target;
    }

    @Override
    public String toString() {
        return this.name + " " + this.source + " -> " + this.target + " "
                + this.delay + " "
                + (!this.visible ? " X" : (this.atPlatform ? " -" : ""));
    }

    public boolean isVisible() {
        return this.visible;
    }

    void copySource(Details details) {
        this.source = details.source;
    }

    void invalidateSource() {
        this.source = null;
    }

    void invalidateTarget() {
        this.target = null;
    }

    boolean isSourceValid() {
        return source != null;
    }

    boolean isTargetValid() {
        return target != null;
    }

    void toggleAtPlatform() {
        this.atPlatform = !this.atPlatform;
    }
}
