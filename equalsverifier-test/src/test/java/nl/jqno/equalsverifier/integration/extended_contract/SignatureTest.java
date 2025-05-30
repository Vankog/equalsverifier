package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class SignatureTest {

    private static final String OVERLOADED = "Overloaded";
    private static final String SIGNATURE_SHOULD_BE = "Signature should be";
    private static final String SIGNATURE = "public boolean equals(Object obj)";

    @Test
    void fail_whenEqualsIsOverloadedWithTypeInsteadOfObject() {
        expectOverloadFailure(
            "Parameter should be an Object, not " + OverloadedWithOwnType.class.getSimpleName(),
            () -> EqualsVerifier.forClass(OverloadedWithOwnType.class).verify());
    }

    @Test
    void fail_whenEqualsIsOverloadedWithTwoParameters() {
        expectOverloadFailure(
            "Too many parameters",
            () -> EqualsVerifier.forClass(OverloadedWithTwoParameters.class).verify());
    }

    @Test
    void fail_whenEqualsIsOverloadedWithNoParameter() {
        expectOverloadFailure("No parameter", () -> EqualsVerifier.forClass(OverloadedWithNoParameter.class).verify());
    }

    @Test
    void fail_whenEqualsIsOverloadedWithUnrelatedParameter() {
        expectOverloadFailure(
            "Parameter should be an Object",
            () -> EqualsVerifier.forClass(OverloadedWithUnrelatedParameter.class).verify());
    }

    @Test
    void fail_whenEqualsIsProperlyOverriddenButAlsoOverloaded() {
        expectOverloadFailure(
            "More than one equals method found",
            () -> EqualsVerifier
                    .forClass(OverloadedAndOverridden.class)
                    .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                    .verify());
    }

    @Test
    void succeed_whenEqualsIsNeitherOverriddenOrOverloaded_givenInheritedDirectlyWarningIsSuppressed() {
        EqualsVerifier
                .forClass(NoEqualsMethod.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void succeed_whenAStaticEqualsExists() {
        EqualsVerifier.forClass(StaticEqualsMethod.class).verify();
    }

    private void expectOverloadFailure(String extraMessage, Runnable runnable) {
        ExpectedException
                .when(runnable)
                .assertFailure()
                .assertMessageContains(OVERLOADED, SIGNATURE_SHOULD_BE, SIGNATURE, extraMessage);
    }

    static final class OverloadedWithOwnType {

        private final int i;

        OverloadedWithOwnType(int i) {
            this.i = i;
        }

        @SuppressWarnings("NonOverridingEquals")
        public boolean equals(OverloadedWithOwnType obj) {
            if (obj == null) {
                return false;
            }
            return i == obj.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class OverloadedWithTwoParameters {

        @SuppressWarnings("unused")
        private final int i;

        OverloadedWithTwoParameters(int i) {
            this.i = i;
        }

        public boolean equals(Object red, Object blue) {
            return red == null ? blue == null : red.equals(blue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class OverloadedWithNoParameter {

        @SuppressWarnings("unused")
        private final int i;

        OverloadedWithNoParameter(int i) {
            this.i = i;
        }

        public boolean equals() {
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class OverloadedWithUnrelatedParameter {

        private final int i;

        OverloadedWithUnrelatedParameter(int i) {
            this.i = i;
        }

        @SuppressWarnings("NonOverridingEquals")
        public boolean equals(int obj) {
            return this.i == obj;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class OverloadedAndOverridden {

        private final int i;

        OverloadedAndOverridden(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OverloadedAndOverridden)) {
                return false;
            }
            return i == ((OverloadedAndOverridden) obj).i;
        }

        @SuppressWarnings("NonOverridingEquals")
        public boolean equals(OverloadedAndOverridden obj) {
            if (obj == null) {
                return false;
            }
            return i == obj.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class NoEqualsMethod {

        @SuppressWarnings("unused")
        private final int i;

        public NoEqualsMethod(int i) {
            this.i = i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    static final class StaticEqualsMethod {

        private final int i;

        StaticEqualsMethod(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StaticEqualsMethod)) {
                return false;
            }
            return i == ((StaticEqualsMethod) obj).i;
        }

        public static boolean equals(StaticEqualsMethod theOne, StaticEqualsMethod theOther) {
            return Objects.equals(theOne, theOther);
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }
}
