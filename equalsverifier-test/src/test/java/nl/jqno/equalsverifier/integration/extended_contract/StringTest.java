package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Locale;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.StringFieldCheck;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class StringTest {

    @Test
    void fail_whenStringIsComparedUsingEqualsIgnoreCaseAndHashCodeIsCaseSensitive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(IncorrectIgnoreCaseStringEquals.class).verify())
                .assertFailure()
                .assertMessageContains(
                    StringFieldCheck.ERROR_DOC_TITLE,
                    "equalsIgnoreCase",
                    "hashCode",
                    "toUpperCase()",
                    "String field caseInsensitiveString");
    }

    @Test
    void succeed_whenStringIsComparedUsingEqualsIgnoreCaseAndHashCodeIsAlsoCaseInsensitive() {
        EqualsVerifier.forClass(CorrectIgnoreCaseStringEquals.class).verify();
    }

    @Test
    void fail_whenStringIsComparedUsingEqualsIgnoreCaseAndHashCodeIsCaseSensitive_givenHashCodeIsCached() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(IncorrectCachedIgnoreCaseStringEquals.class)
                            .withCachedHashCode(
                                "cachedHashCode",
                                "calcHashCode",
                                new IncorrectCachedIgnoreCaseStringEquals("a"))
                            .verify())
                .assertFailure()
                .assertMessageContains(StringFieldCheck.ERROR_DOC_TITLE, "String field caseInsensitiveString");
    }

    private static final class IncorrectIgnoreCaseStringEquals {

        private final String caseInsensitiveString;

        @SuppressWarnings("unused")
        public IncorrectIgnoreCaseStringEquals(String caseInsensitiveString) {
            this.caseInsensitiveString = caseInsensitiveString;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectIgnoreCaseStringEquals)) {
                return false;
            }
            IncorrectIgnoreCaseStringEquals other = (IncorrectIgnoreCaseStringEquals) obj;
            return caseInsensitiveString == null
                    ? other.caseInsensitiveString == null
                    : caseInsensitiveString.equalsIgnoreCase(other.caseInsensitiveString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(caseInsensitiveString);
        }
    }

    private static final class CorrectIgnoreCaseStringEquals {

        private final String caseInsensitiveString;

        @SuppressWarnings("unused")
        public CorrectIgnoreCaseStringEquals(String caseInsensitiveString) {
            this.caseInsensitiveString = caseInsensitiveString;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CorrectIgnoreCaseStringEquals)) {
                return false;
            }
            CorrectIgnoreCaseStringEquals other = (CorrectIgnoreCaseStringEquals) obj;
            return caseInsensitiveString == null
                    ? other.caseInsensitiveString == null
                    : caseInsensitiveString.equalsIgnoreCase(other.caseInsensitiveString);
        }

        @Override
        public int hashCode() {
            return Objects
                    .hash(caseInsensitiveString == null ? "" : caseInsensitiveString.toUpperCase(Locale.getDefault()));
        }
    }

    private static final class IncorrectCachedIgnoreCaseStringEquals {

        private final String caseInsensitiveString;
        private final int cachedHashCode;

        public IncorrectCachedIgnoreCaseStringEquals(String caseInsensitiveString) {
            this.caseInsensitiveString = caseInsensitiveString;
            this.cachedHashCode = calcHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IncorrectCachedIgnoreCaseStringEquals)) {
                return false;
            }
            IncorrectCachedIgnoreCaseStringEquals other = (IncorrectCachedIgnoreCaseStringEquals) obj;
            return caseInsensitiveString == null
                    ? other.caseInsensitiveString == null
                    : caseInsensitiveString.equalsIgnoreCase(other.caseInsensitiveString);
        }

        @Override
        public int hashCode() {
            return cachedHashCode;
        }

        private int calcHashCode() {
            return Objects.hash(caseInsensitiveString);
        }
    }
}
