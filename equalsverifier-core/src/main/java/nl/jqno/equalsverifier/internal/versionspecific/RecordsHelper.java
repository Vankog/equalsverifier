package nl.jqno.equalsverifier.internal.versionspecific;

/**
 * Note: this is a generic implementation for a multi-release jar class. See equalsverifier-16 submodule.
 */
public final class RecordsHelper {

    private RecordsHelper() {}

    public static boolean isRecord(Class<?> type) {
        return type.isRecord();
    }
}
