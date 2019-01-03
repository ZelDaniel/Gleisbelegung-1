package data;

import org.gleisbelegung.xml.XML;

import java.util.concurrent.TimeUnit;

public class Clock {

    public final long startTime = System.currentTimeMillis();

    public XML getTime(XML request) {
        return request.set(
                "zeit",
                Long.toString(
                    System.currentTimeMillis() - startTime + TimeUnit.HOURS.toMillis(5)
                )
        );
    }
}
