package data;

import org.gleisbelegung.xml.XML;

import java.util.concurrent.TimeUnit;

public class Clock {

    public final long startTime = System.currentTimeMillis();

    public long getSimTime() {
        return System.currentTimeMillis() - startTime + TimeUnit.HOURS.toMillis(5);
    }

    public XML getTime(XML request) {
        return request.set(
                "zeit",
                Long.toString(getSimTime())
        );
    }

    public String getTimeString() {
        long simTime = getSimTime();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(simTime) % 24, TimeUnit.MILLISECONDS.toMinutes(simTime) % 60, TimeUnit.MILLISECONDS.toSeconds(simTime) % 60);
    }
}
