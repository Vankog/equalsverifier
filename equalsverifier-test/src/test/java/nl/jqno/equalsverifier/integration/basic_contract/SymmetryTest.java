package nl.jqno.equalsverifier.integration.basic_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class SymmetryTest {

    private static final String SYMMETRY = "Symmetry";
    private static final String NOT_SYMMETRIC = "objects are not symmetric";
    private static final String AND = "and";

    @Test
    void fail_whenEqualsIsNotSymmetrical() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SymmetryIntentionallyBroken.class).verify())
                .assertFailure()
                .assertMessageContains(SYMMETRY, NOT_SYMMETRIC, AND, SymmetryIntentionallyBroken.class.getSimpleName());
    }

    static final class SymmetryIntentionallyBroken {

        private final int x;
        private final int y;

        public SymmetryIntentionallyBroken(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        @SuppressWarnings("EqualsUsingHashCode")
        public boolean equals(Object obj) {
            if (goodEquals(obj)) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            return hashCode() > obj.hashCode();
        }

        public boolean goodEquals(Object obj) {
            if (!(obj instanceof SymmetryIntentionallyBroken)) {
                return false;
            }
            SymmetryIntentionallyBroken p = (SymmetryIntentionallyBroken) obj;
            return p.x == x && p.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
