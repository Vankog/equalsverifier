package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static net.bytebuddy.implementation.ExceptionMethod.throwing;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static nl.jqno.equalsverifier.internal.util.Assert.assertTrue;

import java.util.function.Function;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.Instantiator;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.internal.reflection.annotations.SupportedAnnotations;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class JpaLazyGetterFieldCheck<T> implements FieldCheck<T> {

    private final Class<T> type;
    private final ClassAccessor<T> accessor;
    private final PrefabValues prefabValues;
    private final AnnotationCache annotationCache;
    private final Function<String, String> fieldnameToGetter;
    private final TypeTag typeTag;
    private final boolean strictHashcode;

    public JpaLazyGetterFieldCheck(Configuration<T> config) {
        this.type = config.getType();
        this.accessor = config.getClassAccessor();
        this.prefabValues = config.getPrefabValues();
        this.annotationCache = config.getAnnotationCache();
        this.fieldnameToGetter = config.getFieldnameToGetter();
        this.typeTag = config.getTypeTag();
        this.strictHashcode = config.getWarningsToSuppress().contains(Warning.STRICT_HASHCODE);
    }

    @Override
    public void execute(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor
    ) {
        String fieldName = fieldAccessor.getFieldName();
        String getterName = fieldnameToGetter.apply(fieldName);

        if (
            !fieldIsUsed(referenceAccessor, copyAccessor, fieldAccessor, true) ||
            !fieldIsLazy(fieldAccessor)
        ) {
            return;
        }

        assertEntity(fieldName, "equals", getterName, accessor.hasMethod(getterName));
        ClassAccessor<T> subAccessor = throwingGetterAccessor(getterName);

        T red1 = subAccessor.getRedObject(TypeTag.NULL);
        T red2 = subAccessor.getRedObject(TypeTag.NULL);

        boolean equalsExceptionCaught = false;
        try {
            red1.equals(red2);
        } catch (EqualsVerifierInternalBugException e) {
            equalsExceptionCaught = true;
        }
        assertEntity(fieldName, "equals", getterName, equalsExceptionCaught);

        boolean usedInHashcode =
            !strictHashcode || fieldIsUsed(referenceAccessor, copyAccessor, fieldAccessor, false);
        boolean hashCodeExceptionCaught = false;
        try {
            red1.hashCode();
        } catch (EqualsVerifierInternalBugException e) {
            hashCodeExceptionCaught = true;
        }
        assertEntity(fieldName, "hashCode", getterName, hashCodeExceptionCaught || !usedInHashcode);
    }

    private boolean fieldIsUsed(
        ObjectAccessor<T> referenceAccessor,
        ObjectAccessor<T> copyAccessor,
        FieldAccessor fieldAccessor,
        boolean forEquals
    ) {
        T red = referenceAccessor.get();
        T blue = copyAccessor
            .withChangedField(fieldAccessor.getField(), prefabValues, typeTag)
            .get();

        if (forEquals) {
            return !red.equals(blue);
        } else {
            return red.hashCode() != blue.hashCode();
        }
    }

    private boolean fieldIsLazy(FieldAccessor fieldAccessor) {
        return (
            annotationCache.hasFieldAnnotation(
                type,
                fieldAccessor.getFieldName(),
                SupportedAnnotations.JPA_LINKED_FIELD
            ) ||
            annotationCache.hasFieldAnnotation(
                type,
                fieldAccessor.getFieldName(),
                SupportedAnnotations.JPA_LAZY_FIELD
            )
        );
    }

    private ClassAccessor<T> throwingGetterAccessor(String getterName) {
        Class<T> sub = Instantiator.giveDynamicSubclass(
            type,
            getterName,
            builder ->
                builder
                    .method(named(getterName))
                    .intercept(throwing(EqualsVerifierInternalBugException.class))
        );
        return ClassAccessor.of(sub, prefabValues);
    }

    private void assertEntity(
        String fieldName,
        String method,
        String getterName,
        boolean assertion
    ) {
        assertTrue(
            Formatter.of(
                "JPA Entity: direct reference to field %% used in %% instead of getter %%.",
                fieldName,
                method,
                getterName
            ),
            assertion
        );
    }
}
