package org.skriptlang.skript.lang.comparator;

/**
 * Used to compare two objects of a different or the same type.
 *
 * @param <T1> The first type for comparison.
 * @param <T2> The second type for comparison.
 * @see Comparators#registerComparator(Class, Class, Comparator)
 */
@FunctionalInterface
public interface Comparator<T1, T2> {

	/**
	 * The main method for this Comparator to determine the Relation between two objects.
	 *
	 * @param o1 The first object for comparison.
	 * @param o2 The second object for comparison.
	 * @return The Relation between the two provided objects.
	 */
	Relation compare(T1 o1, T2 o2);

	/**
	 * @return Whether this comparator supports ordering of elements or not.
	 */
	default boolean supportsOrdering() {
		return false;
	}

	/**
	 * @return Whether this comparator supports argument inversion through {@link InverseComparator}.
	 */
	default boolean supportsInversion() {
		return true;
	}

	/**
	 * Used in {@link ch.njol.skript.conditions.CondZCompare} for debug messages
	 *
	 * @param c1 First class of comparator
	 * @param c2 Second class of comparator
	 * @return Simplified string of comparator
	 */
	default String debugString(Class<T1> c1, Class<T2> c2) {
		return c1.getName() + "/" + c2.getName();
	}

}
