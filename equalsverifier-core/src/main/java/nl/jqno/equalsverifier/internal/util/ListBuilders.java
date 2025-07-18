package nl.jqno.equalsverifier.internal.util;

import java.util.*;

/** Helper functions for building lists with examples. */
public final class ListBuilders {

    private ListBuilders() {}

    /**
     * Builds a list with at least one example.
     *
     * @param first The first example.
     * @param more  Zero or more additional examples.
     * @param <T>   The type of example.
     * @return A list with at least one example.
     */
    @SafeVarargs
    public static <T> List<T> buildListOfAtLeastOne(T first, T... more) {
        if (first == null) {
            throw new IllegalArgumentException("First example is null.");
        }

        var result = new ArrayList<T>();
        result.add(first);
        addArrayElementsToList(result, more);

        return result;
    }

    /**
     * Builds a list with at least two examples.
     *
     * @param first  The first example.
     * @param second The second example.
     * @param more   Zero or more additional examples.
     * @param <T>    The type of example.
     * @return A list with at least two examples.
     */
    @SafeVarargs
    public static <T> List<T> buildListOfAtLeastTwo(T first, T second, T... more) {
        if (first == null) {
            throw new IllegalArgumentException("First example is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("Second example is null.");
        }

        var result = new ArrayList<T>();
        result.add(first);
        result.add(second);
        addArrayElementsToList(result, more);

        return result;
    }

    @SafeVarargs
    private static <T> void addArrayElementsToList(List<T> list, T... more) {
        if (more != null) {
            for (T e : more) {
                if (e == null) {
                    throw new IllegalArgumentException("One of the examples is null.");
                }
                list.add(e);
            }
        }
    }

    /**
     * Builds a list from the elements of an Iterable.
     *
     * @param iterable The iterable containing the elements.
     * @param <T>      The type of the elements.
     * @return A list with the elements of the Iterable.
     */
    public static <T> List<T> fromIterable(Iterable<T> iterable) {
        var result = new ArrayList<T>();
        for (T t : iterable) {
            result.add(t);
        }
        return result;
    }

    /**
     * Determines whether a list contains the same example more than once.
     *
     * @param list The list that may or may not contain duplicates.
     * @param <T>  The type of example.
     * @return Whether the given list contains duplicates.
     */
    public static <T> boolean listContainsDuplicates(List<T> list) {
        return list.size() != new HashSet<>(list).size();
    }
}
