package org.gleisbelegung.sts;

import org.gleisbelegung.database.StsPlatformInterface;
import org.gleisbelegung.xml.XML;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Platform implements StsPlatformInterface {

    public static final Platform EMPTY = new Platform();

    private static final Map<String, Platform> generatorMap = new HashMap<>();

    private final Set<Platform> neighbours = new HashSet<>();
    private final String name;

    private Platform() {
        this.name = null;
    }

    private Platform(final String name) {
        Platform.generatorMap.put(name, this);
        this.name = name;
    }

    public boolean equals(Object o) {
        if (Platform.class.isAssignableFrom(o.getClass())) {
            return this.equals((Platform) o);
        }

        return false;
    }

    public boolean equals(Platform o) {
        if (this.name == null) {
            return o.name == null;
        }
        return name.equals(o.name);
    }

    public static Platform get(final String name) {
        final Platform pf = Platform.generatorMap.get(name);
        if (pf != null) {
            return pf;
        }
        return new Platform(name);
    }

    public static Platform parse(final XML xml) {
        final Platform generated = get(xml.get("name"));
        boolean complete = true;
        for (final XML neighbour : xml.getInternXML()) {
            if (neighbour.getKey().equals("n")) {
                final Platform neighbourParsed = generatorMap.get(neighbour.get("name"));
                if (neighbourParsed == null) {
                    complete = false;
                    break;
                }
                generated.neighbours.add(neighbourParsed);
            }
        }
        if (complete) {
            for (final Platform p : generated.neighbours) {
                p.synchronizeNeighbours(generated);
            }
        }

        return generated;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Set<Platform> getNeighbours() {
        return new HashSet<>(this.neighbours);
    }

    private void synchronizeNeighbours(final Platform platform) {
        this.neighbours.clear();
        this.neighbours.addAll(platform.getNeighbours());
        this.neighbours.remove(this);
        this.neighbours.add(platform);
    }
}
