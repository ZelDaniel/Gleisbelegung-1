package org.gleisbelegung.utils;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class MathX {

	private static final Set<Integer> primes = new TreeSet<>();
	private static int nextCandidate = 2; // next number to check for prime
	private static int maxDiv = 2; // smallest integer that 2 * maxDiv = nextCandidate 

	public static boolean isPrime(int i) {
		if (i < 0)
			throw new IllegalArgumentException(
					"Argument needs to be non negative integer");
		if (i < 2)
			return false;

		if (primes.isEmpty()) {
			primes.add(Integer.valueOf(nextCandidate++));
		}
		if (i < nextCandidate)
			return false;

		/*
		 * test prime and fill lookup list with all numbers starting from nextCandidate
		 */
		while (nextCandidate <= i) {
			if (maxDiv * 2 < nextCandidate) {
				maxDiv++;
			}
			int div = maxDiv;
			boolean isPrime = true;
			int prime = 0;
			Iterator<Integer> primes = MathX.primes.iterator();
			while (primes.hasNext()) {
				prime = primes.next().intValue();
				/*
				 *  is prime > sqrt(nextCandidate)
				 *  if yes nextCandidate can't be prime anymore
				 */
				if (prime * prime > nextCandidate) {
					break;
				}
				int product = prime * div;
				while (product > nextCandidate) {
					div--;
					product = prime * div;
				}
				if (product == nextCandidate) {
					isPrime = false;
					break;
				}
			}
			if (isPrime) {
				MathX.primes.add(nextCandidate);
			}
			nextCandidate++;
		}

		/*
		 * lookup
		 */
		return MathX.primes.contains(Integer.valueOf(i));

	}
}
