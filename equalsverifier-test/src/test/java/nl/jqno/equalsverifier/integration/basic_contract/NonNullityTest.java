package nl.jqno.equalsverifier.integration.basic_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

class NonNullityTest {

    @Test
    void fail_whenNullPointerExceptionIsThrown_givenNullInput() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullPointerExceptionThrower.class).verify())
                .assertFailure()
                .assertMessageContains("Non-nullity: NullPointerException thrown");
    }

    @Test
    void fail_whenEqualsReturnsTrue_givenNullInput() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullReturnsTrue.class).verify())
                .assertFailure()
                .assertMessageContains("Non-nullity: true returned for null value");
    }

    static final class NullPointerExceptionThrower extends Point {

        public NullPointerExceptionThrower(int x, int y) {
            super(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (!obj.getClass().equals(getClass())) {
                return false;
            }
            return super.equals(obj);
        }
    }

    static final class NullReturnsTrue extends Point {

        public NullReturnsTrue(int x, int y) {
            super(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return true;
            }
            return super.equals(obj);
        }
    }
}
