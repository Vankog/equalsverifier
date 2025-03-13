package nl.jqno.equalsverifier.integration.extra_features;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.EmbeddedId;
import nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.Entity;
import nl.jqno.equalsverifier_testhelpers.annotations.javax.persistence.Id;
import nl.jqno.equalsverifier_testhelpers.annotations.org.hibernate.annotations.NaturalId;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
class JpaIdTest {

    @Test
    void succeed_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithId() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class).verify();
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonReorderedFields.class).verify();
    }

    @Test
    void succeed_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class).suppress(Warning.SURROGATE_KEY).verify();
        EqualsVerifier.forClass(JpaIdSurrogateKeyPersonReorderedFields.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @Test
    void succeed_whenAllFieldsAreUsed_givenIdIsAnnotatedWithIdAndJpaKeyWarningIsSuppressed() {
        EqualsVerifier.forClass(JpaIdValueKeyPerson.class).suppress(Warning.SURROGATE_OR_BUSINESS_KEY).verify();
        EqualsVerifier
                .forClass(JpaIdValueKeyPersonReorderedFields.class)
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
                .verify();
    }

    @Test
    void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdButIdAnnotationIsIgnored() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdBusinessKeyPerson.class)
                            .withIgnoredAnnotations(Id.class)
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use id");
    }

    @Test
    void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(JpaIdSurrogateKeyPerson.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "id is marked @Id",
                    "equals should not use it",
                    "Suppress Warning.SURROGATE_KEY if");
    }

    @Test
    void fail_whenOnlyIdFieldIsUsed_givenIdIsAnnotatedWithId2() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(JpaIdSurrogateKeyPersonReorderedFields.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "equals does not use socialSecurity",
                    "Suppress Warning.SURROGATE_KEY if");
    }

    @Test
    void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdBusinessKeyPerson.class)
                            .suppress(Warning.SURROGATE_KEY)
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "id is marked @Id",
                    "Warning.SURROGATE_KEY",
                    "equals does not use");
    }

    @Test
    void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithIdAndSurrogateKeyWarningIsSuppressed2() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdBusinessKeyPersonReorderedFields.class)
                            .suppress(Warning.SURROGATE_KEY)
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "equals should not use socialSecurity",
                    "Warning.SURROGATE_KEY is suppressed and it is not marked as @Id",
                    "but it does");
    }

    @Test
    void succeed_whenEmbeddedIdIsUsedCorrectly() {
        EqualsVerifier.forClass(JpaEmbeddedIdBusinessKeyPerson.class).verify();
        EqualsVerifier.forClass(JpaEmbeddedIdSurrogateKeyPerson.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @Test
    void fail_whenOnlyEmbeddedIdFieldIsUsed_givenIdIsAnnotatedWithEmbeddedId() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(JpaEmbeddedIdSurrogateKeyPerson.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "id is marked @Id or @EmbeddedId",
                    "equals should not use it",
                    "Suppress Warning.SURROGATE_KEY if");
    }

    @Test
    void fail_whenIdFieldIsNotUsed_givenIdIsAnnotatedWithEmbeddedIdAndSurrogateKeyWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaEmbeddedIdBusinessKeyPerson.class)
                            .suppress(Warning.SURROGATE_KEY)
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "id is marked @Id or @EmbeddedId",
                    "Warning.SURROGATE_KEY",
                    "equals does not use");
    }

    @Test
    void fail_whenEmbeddedIdFieldIsTheOnlyFieldUsed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaEmbeddedIdBusinessKeyPerson.class)
                            .withOnlyTheseFields("id")
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Precondition: you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.",
                    "Suppress Warning.SURROGATE_KEY");
    }

    @Test
    void succeed_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalId() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPerson.class).verify();
    }

    @Test
    void succeed_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdAndNothingIsAnnotatedWithJpaId() {
        EqualsVerifier.forClass(NaturalIdWithoutJpaIdBusinessKeyPerson.class).verify();
    }

    @Test
    void fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButIdAnnotationIsIgnored() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NaturalIdBusinessKeyPerson.class)
                            .withIgnoredAnnotations(NaturalId.class)
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use name");
    }

    @Test
    void succeed_whenIdAndNameFieldsAreNotUsed_givenNameIsIgnored() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonDoesntUseName.class).withIgnoredFields("name").verify();
    }

    @Test
    void succeed_whenIdAndNameFieldsAreNotUsed_givenSocialSecurityAndBirthdateAreOnlyUsed() {
        EqualsVerifier
                .forClass(JpaIdBusinessKeyPersonDoesntUseName.class)
                .withOnlyTheseFields("socialSecurity", "birthdate")
                .verify();
    }

    @Test
    void succeed_whenIdIsPartOfAProperJpaEntity() {
        EqualsVerifier.forClass(JpaIdBusinessKeyPersonEntity.class).verify();
    }

    @Test
    void succeed_whenNaturalIdIsPartOfAProperJpaEntity() {
        EqualsVerifier.forClass(NaturalIdBusinessKeyPersonEntity.class).verify();
    }

    @Test
    void succeed_whenEqualsBehavesLikeVersionedEntity_givenIdIsMarkedWithIdAndWarningVersionedEntityIsSuppressed() {
        EqualsVerifier
                .forClass(JpaIdVersionedEntity.class)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY, Warning.SURROGATE_KEY)
                .verify();
    }

    @Test
    void succeed_whenMethodsAreAnnotatedInsteadOfFields() {
        EqualsVerifier.forClass(MethodAnnotatedBusinessKeyPerson.class).verify();
    }

    @Test
    void fail_whenIdFieldIsTheOnlyFieldUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(JpaIdBusinessKeyPerson.class).withOnlyTheseFields("id").verify())
                .assertFailure()
                .assertMessageContains(
                    "Precondition: you can't use withOnlyTheseFields on a field marked @Id or @EmbeddedId.",
                    "Suppress Warning.SURROGATE_KEY");
    }

    @Test
    void fail_whenOnlySocialSecurityIsUsed_givenSocialSecurityIsAnnotatedWithNaturalIdButSurrogateKeyWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NaturalIdBusinessKeyPerson.class)
                            .suppress(Warning.SURROGATE_KEY)
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Precondition: you can't suppress Warning.SURROGATE_KEY when fields are marked @NaturalId.");
    }

    @Test
    void fail_whenWithOnlyTheseFieldsIsUsed_givenWarningSurrogateKeyIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdSurrogateKeyPerson.class)
                            .withOnlyTheseFields("socialSecurity")
                            .suppress(Warning.SURROGATE_KEY))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.");
    }

    @Test
    void fail_whenWithOnlyTheseFieldsIsUsed_givenWarningSurrogateKeyIsSuppressedInReverse() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdSurrogateKeyPerson.class)
                            .suppress(Warning.SURROGATE_KEY)
                            .withOnlyTheseFields("socialSecurity"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can't use withOnlyTheseFields when Warning.SURROGATE_KEY is suppressed.");
    }

    @Test
    void fail_whenFieldsAreIgnored_givenWarningSurrogateKeyIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdSurrogateKeyPerson.class)
                            .withIgnoredFields("socialSecurity")
                            .suppress(Warning.SURROGATE_KEY))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition: you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.");
    }

    @Test
    void fail_whenFieldsAreIgnored_givenWarningSurrogateKeyIsSuppressedInReverse() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdSurrogateKeyPerson.class)
                            .suppress(Warning.SURROGATE_KEY)
                            .withIgnoredFields("socialSecurity"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition: you can't use withIgnoredFields when Warning.SURROGATE_KEY is suppressed.");
    }

    @Test
    void fail_whenWithOnlyTheseFieldsIsUsed_givenFieldsAreMarkedWithNaturalId() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NaturalIdBusinessKeyPerson.class)
                            .withOnlyTheseFields("socialSecurity")
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Precondition: you can't use withOnlyTheseFields when fields are marked with @NaturalId.");
    }

    @Test
    void fail_whenFieldsAreIgnored_givenFieldsAreMarkedWithNaturalId() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NaturalIdBusinessKeyPerson.class)
                            .withIgnoredFields("socialSecurity")
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Precondition: you can't use withIgnoredFields when fields are marked with @NaturalId.");
    }

    @Test
    void fail_whenWarningVersionedEntityIsSuppressed_givenAFieldIsAnnotatedWithNaturalId() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NaturalIdBusinessKeyPerson.class)
                            .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Precondition: you can't suppress Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY when fields are marked with @NaturalId.");
    }

    @Test
    void fail_whenWarningSurrogateKeyIsSuppressed_givenWarningSurrogateOrBusinessKeyIsAlsoSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(JpaIdBusinessKeyPerson.class)
                            .suppress(Warning.SURROGATE_KEY, Warning.SURROGATE_OR_BUSINESS_KEY))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can't suppress Warning.SURROGATE_KEY when Warning.SURROGATE_OR_BUSINESS_KEY is also suppressed.");
    }

    @Test
    void fail_whenAnIdAnnotationFromAnotherPackageIsUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NonJpaIdBusinessKeyPerson.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields");
    }

    @Test
    void fail_whenANaturalIdAnnotationFromAnotherPackageIsUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NonHibernateNaturalIdBusinessKeyPerson.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields");
    }

    @Test
    void succeed_whenEqualsIsImplementedInSuperclass_givenWarningSurrogateKeyIsSuppressed() {
        EqualsVerifier.forClass(SubclassEntity.class).suppress(Warning.SURROGATE_KEY).verify();
    }

    @Test
    void succeed_whenEqualsUsesIdsAndNonIds_givenWarningSurrogateOrBusinessKeyIsSuppressed() {
        EqualsVerifier
                .forClass(JpaIdDirtyTrackingPerson.class)
                .suppress(Warning.SURROGATE_OR_BUSINESS_KEY)
                .withOnlyTheseFields("id", "version", "isDirty")
                .verify();
    }

    static class JpaIdBusinessKeyPerson {

        @Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPerson)) {
                return false;
            }
            JpaIdBusinessKeyPerson other = (JpaIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class JpaIdBusinessKeyPersonReorderedFields {

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        @Id
        private final UUID id;

        public JpaIdBusinessKeyPersonReorderedFields(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPersonReorderedFields)) {
                return false;
            }
            JpaIdBusinessKeyPersonReorderedFields other = (JpaIdBusinessKeyPersonReorderedFields) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class JpaIdSurrogateKeyPerson {

        @Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdSurrogateKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdSurrogateKeyPerson)) {
                return false;
            }
            JpaIdSurrogateKeyPerson other = (JpaIdSurrogateKeyPerson) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class JpaIdSurrogateKeyPersonReorderedFields {

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        @Id
        private final UUID id;

        public JpaIdSurrogateKeyPersonReorderedFields(
                UUID id,
                String socialSecurity,
                String name,
                LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdSurrogateKeyPersonReorderedFields)) {
                return false;
            }
            JpaIdSurrogateKeyPersonReorderedFields other = (JpaIdSurrogateKeyPersonReorderedFields) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class JpaIdValueKeyPerson {

        @Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdValueKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdValueKeyPerson)) {
                return false;
            }
            JpaIdValueKeyPerson other = (JpaIdValueKeyPerson) obj;
            return Objects.equals(id, other.id)
                    && Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id, socialSecurity, name, birthdate);
        }
    }

    static class JpaIdValueKeyPersonReorderedFields {

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        @Id
        private final UUID id;

        public JpaIdValueKeyPersonReorderedFields(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdValueKeyPersonReorderedFields)) {
                return false;
            }
            JpaIdValueKeyPersonReorderedFields other = (JpaIdValueKeyPersonReorderedFields) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate)
                    && Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate, id);
        }
    }

    static class JpaEmbeddedIdBusinessKeyPerson {

        @EmbeddedId
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaEmbeddedIdBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaEmbeddedIdBusinessKeyPerson)) {
                return false;
            }
            JpaEmbeddedIdBusinessKeyPerson other = (JpaEmbeddedIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class JpaEmbeddedIdSurrogateKeyPerson {

        @EmbeddedId
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaEmbeddedIdSurrogateKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaEmbeddedIdSurrogateKeyPerson)) {
                return false;
            }
            JpaEmbeddedIdSurrogateKeyPerson other = (JpaEmbeddedIdSurrogateKeyPerson) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class NaturalIdBusinessKeyPerson {

        @Id
        private final UUID id;

        @NaturalId
        private final String socialSecurity;

        private final String name;
        private final LocalDate birthdate;

        public NaturalIdBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdBusinessKeyPerson)) {
                return false;
            }
            NaturalIdBusinessKeyPerson other = (NaturalIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static class NaturalIdWithoutJpaIdBusinessKeyPerson {

        private final UUID id;

        @NaturalId
        private final String socialSecurity;

        private final String name;
        private final LocalDate birthdate;

        public NaturalIdWithoutJpaIdBusinessKeyPerson(
                UUID id,
                String socialSecurity,
                String name,
                LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdWithoutJpaIdBusinessKeyPerson)) {
                return false;
            }
            NaturalIdWithoutJpaIdBusinessKeyPerson other = (NaturalIdWithoutJpaIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static class JpaIdBusinessKeyPersonDoesntUseName {

        @Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public JpaIdBusinessKeyPersonDoesntUseName(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPersonDoesntUseName)) {
                return false;
            }
            JpaIdBusinessKeyPersonDoesntUseName other = (JpaIdBusinessKeyPersonDoesntUseName) obj;
            return Objects.equals(socialSecurity, other.socialSecurity) && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, birthdate);
        }
    }

    @Entity
    static class JpaIdBusinessKeyPersonEntity {

        @Id
        private UUID id;

        private String socialSecurity;
        private String name;
        private LocalDate birthdate;

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdBusinessKeyPersonEntity)) {
                return false;
            }
            JpaIdBusinessKeyPersonEntity other = (JpaIdBusinessKeyPersonEntity) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    @Entity
    static class NaturalIdBusinessKeyPersonEntity {

        @Id
        private UUID id;

        @NaturalId
        private String socialSecurity;

        private String name;
        private LocalDate birthdate;

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NaturalIdBusinessKeyPersonEntity)) {
                return false;
            }
            NaturalIdBusinessKeyPersonEntity other = (NaturalIdBusinessKeyPersonEntity) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static class NonJpaIdBusinessKeyPerson {

        @nl.jqno.equalsverifier_testhelpers.annotations.Id
        private final UUID id;

        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public NonJpaIdBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonJpaIdBusinessKeyPerson)) {
                return false;
            }
            NonJpaIdBusinessKeyPerson other = (NonJpaIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity)
                    && Objects.equals(name, other.name)
                    && Objects.equals(birthdate, other.birthdate);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity, name, birthdate);
        }
    }

    static class NonHibernateNaturalIdBusinessKeyPerson {

        @Id
        private final UUID id;

        @nl.jqno.equalsverifier_testhelpers.annotations.NaturalId
        private final String socialSecurity;

        private final String name;
        private final LocalDate birthdate;

        public NonHibernateNaturalIdBusinessKeyPerson(
                UUID id,
                String socialSecurity,
                String name,
                LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NonHibernateNaturalIdBusinessKeyPerson)) {
                return false;
            }
            NonHibernateNaturalIdBusinessKeyPerson other = (NonHibernateNaturalIdBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    public final class JpaIdVersionedEntity {

        @Id
        private final long id;

        private final String s;

        public JpaIdVersionedEntity(long id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdVersionedEntity)) {
                return false;
            }
            JpaIdVersionedEntity other = (JpaIdVersionedEntity) obj;
            if (id == 0L && other.id == 0L) {
                return Objects.equals(s, other.s);
            }
            return id == other.id;
        }

        @Override
        public final int hashCode() {
            return Float.floatToIntBits(id);
        }
    }

    static class MethodAnnotatedBusinessKeyPerson {

        private final UUID id;
        private final String socialSecurity;
        private final String name;
        private final LocalDate birthdate;

        public MethodAnnotatedBusinessKeyPerson(UUID id, String socialSecurity, String name, LocalDate birthdate) {
            this.id = id;
            this.socialSecurity = socialSecurity;
            this.name = name;
            this.birthdate = birthdate;
        }

        @Id
        public UUID getId() {
            return id;
        }

        @NaturalId
        public String getSocialSecurity() {
            return socialSecurity;
        }

        public String getName() {
            return name;
        }

        public LocalDate getBirthdate() {
            return birthdate;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof MethodAnnotatedBusinessKeyPerson)) {
                return false;
            }
            MethodAnnotatedBusinessKeyPerson other = (MethodAnnotatedBusinessKeyPerson) obj;
            return Objects.equals(socialSecurity, other.socialSecurity);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(socialSecurity);
        }
    }

    static class AbstractEntity {

        @Id
        private final UUID id;

        public AbstractEntity(UUID id) {
            this.id = id;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof AbstractEntity)) {
                return false;
            }
            AbstractEntity other = (AbstractEntity) obj;
            return Objects.equals(id, other.id);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }
    }

    static class SubclassEntity extends AbstractEntity {

        private final String name;

        public SubclassEntity(UUID id, String name) {
            super(id);
            this.name = name;
        }
    }

    @Entity
    static class JpaIdDirtyTrackingPerson {

        @Id
        private UUID id;

        private int version;
        private boolean isDirty;

        private String socialSecurity;
        private String name;
        private LocalDate birthdate;

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof JpaIdDirtyTrackingPerson)) {
                return false;
            }
            JpaIdDirtyTrackingPerson other = (JpaIdDirtyTrackingPerson) obj;
            return Objects.equals(id, other.id)
                    && Objects.equals(version, other.version)
                    && Objects.equals(isDirty, other.isDirty);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id, version, isDirty);
        }
    }
}
