package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.integration.extended_contract.SealedTypes.SealedChild;
import nl.jqno.equalsverifier.integration.extended_contract.SealedTypes.SealedParent;
import nl.jqno.equalsverifier.integration.extended_contract.SealedTypes.SealedTypeContainer;
import org.junit.jupiter.api.Test;

public class SealedTypesTest {

    @Test
    public void dontCrashOnParent() {
        EqualsVerifier
            .forClass(SealedParent.class)
            .withRedefinedSubclass(SealedChild.class)
            .verify();
    }

    @Test
    public void dontCrashOnChild() {
        EqualsVerifier.forClass(SealedChild.class).withRedefinedSuperclass().verify();
    }

    @Test
    void succeed_whenClassContainsASealedType() {
        EqualsVerifier.forClass(SealedTypeContainer.class).verify();
    }
}
