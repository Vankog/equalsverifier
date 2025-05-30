/**
 * Defines EqualsVerifier's needs in the modular world.
 *
 * @since 4.0
 */
// When making changes to this file, make sure they are
// reflected in equalsverifier-release-nodep/../module-info.java!
module nl.jqno.equalsverifier {
    exports nl.jqno.equalsverifier;
    exports nl.jqno.equalsverifier.api;

    // Direct dependencies
    requires transitive net.bytebuddy;
    requires org.objenesis;

    // Optional dependencies
    requires static org.mockito;

    // Built-in prefab values
    requires static java.desktop;
    requires static java.naming;
    requires static java.rmi;
    requires static java.sql;
}
