package org.gleisbelegung.sts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gleisbelegung.xml.XML;


/**
 * Represenation of zugliste
 */
public class Trainlist implements Iterable<Train> {

	/**
	 * Creates a new trainlist by parsing given XML describing a trainlist in
	 * XML notation.
	 *
	 * @param handler
	 *            instance of caller
	 * @param xml
	 *            the trainlist to parse
	 * @return parsed trainlist
	 */
	public static Trainlist parse(final StSHandlerInterface handler, final XML xml) {
		if (!xml.getKey().equals("zugliste")) {
			throw new IllegalArgumentException();
		}
		final List<XML> trainsXML = xml.getInternXML();
		final Trainlist list = new Trainlist(handler);
		for (final XML trainXML : trainsXML) {
			final Train train = Train.createFromXML(trainXML);
			list.idMap.put(train.getID(), train);
		}

		return list;
	}

	private final Map<Integer, Train> idMap = new HashMap<>();
	private final Map<Integer, Train> history = new HashMap<>();
	private final Map<Integer, Train> dummyIdMap = new HashMap<>();
	private final StSHandlerInterface handler;

	private Trainlist(final StSHandlerInterface handler) {
		this.handler = handler;
	}

	synchronized Train createByIdOnly(final Integer Id) {
		final Train t = Train.createByIdOnly(Id);
		this.dummyIdMap.put(Id, t);
		return t;
	}

	public synchronized Train get(final Integer id) {
		final Train t = this.idMap.get(id);
		if (t != null)
			return t;
		return this.dummyIdMap.get(id);
	}

	public Train get(final String id) {
		final Integer zid = Integer.valueOf(id);
		return get(zid);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Train> iterator() {
		return this.idMap.values().iterator();
	}

	synchronized void purge(final Integer id) throws InterruptedException {
		final Train t = this.idMap.get(id);
		remove(id);
		this.history.remove(id);
		System.err.printf("Destroying train id=%d\n", id);
		if (t == null)
			return;

	}

	public synchronized void remove(final Integer id)
			throws InterruptedException {
		final Train old = this.idMap.remove(id);
		if (old != null)
			old.removedFromList(this.handler);
		this.history.put(id, old);
	}

	public void remove(final Train train) throws InterruptedException {
		remove(train.getID());
	}

	public synchronized List<Train> toList() {
		final List<Train> l = new ArrayList<>(this.idMap.size());
		l.addAll(this.idMap.values());
		return l;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.idMap.values().toString();
	}

	@SafeVarargs
	public final synchronized void update(final XML trainlistXML,
			final Map<Train, ?>... otherMaps) throws InterruptedException {
		final List<XML> trainsXML = trainlistXML.getInternXML();
		final Map<Integer, Train> oldMap = this.idMap;
		final Map<Integer, Train> newMap = new HashMap<>();
		final ArrayDeque<Train> toRemove = new ArrayDeque<>();
		this.dummyIdMap.clear();
		final Map<Integer, Train> historyOld = new HashMap<>(this.history);
		this.history.clear();
		for (final XML trainXML : trainsXML) {
			final Integer id = Integer.valueOf(trainXML.get("zid"));
			final Train train;
			if (oldMap.containsKey(id)) {
				train = oldMap.remove(id);
			} else if (historyOld.containsKey(id)) {
				// removed by event, but update of trainlist still shows this
				// train
				this.history.put(id, historyOld.remove(id));
				continue;
			} else {
				train = Train.createFromXML(trainXML);
			}
			newMap.put(id, train);
		}
		for (final Integer id : oldMap.keySet()) {
			final Train old = oldMap.get(id);
			toRemove.add(old);
		}
		for (final Train t : toRemove) {
			final Integer id = t.getID();
			if (id.intValue() > 0) {
				remove(id);
			} else {
				purge(id);
			}
		}
		assert oldMap.isEmpty();
		this.idMap.clear();
		this.idMap.putAll(newMap);
		if (!toRemove.isEmpty())
			for (final Map<Train, ?> map : otherMaps) {
				synchronized (map) {
					for (final Train t : toRemove) {
						map.remove(t);
					}
				}
			}
	}

	public boolean isEmpty() {
		return this.idMap.isEmpty();
	}

}
