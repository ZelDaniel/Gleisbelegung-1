package org.gleisbelegung.sts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gleisbelegung.annotations.Threadsafe;
import org.gleisbelegung.database.StSDataInterface;
import org.gleisbelegung.xml.XML;

/**
 * Represenation of zugliste
 */
public class Trainlist implements Iterable<Train> {

	/**
	 * Creates a new trainlist or updates the existing one by parsing given XML describing a train list in
	 * XML notation.
	 *
	 * @param handler
	 *            instance of a singleton to assert uniqueness of the train list instance
	 * @param xml
	 *            the trainlist to parse
	 * @return parsed trainlist
	 */
	@Threadsafe
	public static Trainlist parse(final StSDataInterface handler, final XML xml) {
		final Trainlist list;
		synchronized (Trainlist.class) {
			if (null != handler.getTrainList()) {
				list = handler.getTrainList();
			} else {
				list = new Trainlist(handler);
			}
		}
		if (!xml.getKey().equals("zugliste")) {
			throw new IllegalArgumentException();
		}
		return list.update(xml);
	}

	/**
	 * The real collection of train currently known
	 */
	private final Map<Integer, Train> idMap = new HashMap<>();

	/**
	 * Contains the list of trains removed by reported status code=402 to prevent re-adding trains which are about to
	 * leave the sim.
	 */
	private final Map<Integer, Train> history = new HashMap<>();

	private final StSDataInterface handler;

	private Trainlist(final StSDataInterface handler) {
		this.handler = handler;
		handler.setTrainList(this);
	}

	@Threadsafe
	public synchronized Train get(final Integer id) {
		return this.idMap.get(id);
	}

	@Threadsafe
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

	synchronized void purge(final Integer id) {
		remove(id);
		System.err.printf("Destroying train id=%d\n", id);
	}

	@Threadsafe
	public synchronized void remove(final Integer id) {
		final Train old = this.idMap.remove(id);
	}

	private void remove(final Integer id, final Train old) {
		if (old != null) {
			old.removedFromList(this.handler);
		}
		this.history.put(id, old);
	}

	@Threadsafe
	public void remove(final Train train) {
		remove(train.getID());
	}

	@Threadsafe
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

	@Threadsafe
	public final synchronized Trainlist update(final XML trainlistXML) {
		final List<XML> trainsXML = trainlistXML.getInternXML();
		final Map<Integer, Train> oldMap = this.idMap;
		final Map<Integer, Train> newMap = new HashMap<>();
		final Map<Integer, Train> historyOld = new HashMap<>(this.history);
		this.history.clear();

		/*
		 * step 1: create new list of train
		 */
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

		/*
		 * step 2: remove all train which are not part of new list
		 */
		for (final Iterator<Integer> ids = oldMap.keySet().iterator(); ids.hasNext(); ) {
			Integer id = ids.next();
			Train removedTrain = oldMap.get(id);
			ids.remove();
			remove(id, removedTrain);
			if (id.intValue() < 0) {
				this.history.remove(id);
			}
		}
		assert oldMap.isEmpty();
		this.idMap.clear();
		this.idMap.putAll(newMap);

		return this;
	}

	public boolean isEmpty() {
		return this.idMap.isEmpty();
	}

}
