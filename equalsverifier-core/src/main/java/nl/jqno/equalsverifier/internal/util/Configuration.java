package nl.jqno.equalsverifier.internal.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.*;

public final class Configuration<T> {

    private static final Function<String, String> DEFAULT_FIELDNAME_TO_GETTER_CONVERTER = fn ->
        "get" + Character.toUpperCase(fn.charAt(0)) + fn.substring(1);

    private final Class<T> type;
    private final Set<String> nonnullFields;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;
    private final boolean hasRedefinedSuperclass;
    private final Class<? extends T> redefinedSubclass;
    private final boolean usingGetClass;
    private final EnumSet<Warning> warningsToSuppress;
    private final Function<String, String> fieldnameToGetter;

    private final TypeTag typeTag;
    private final AnnotationCache annotationCache;
    private final Set<String> ignoredFields;

    private final List<T> equalExamples;
    private final List<T> unequalExamples;

    // CHECKSTYLE OFF: ParameterNumber
    private Configuration(
        Class<T> type,
        TypeTag typeTag,
        Set<String> ignoredFields,
        Set<String> nonnullFields,
        AnnotationCache annotationCache,
        CachedHashCodeInitializer<T> cachedHashCodeInitializer,
        boolean hasRedefinedSuperclass,
        Class<? extends T> redefinedSubclass,
        boolean usingGetClass,
        EnumSet<Warning> warningsToSuppress,
        Function<String, String> fieldnameToGetter,
        List<T> equalExamples,
        List<T> unequalExamples
    ) {
        this.type = type;
        this.typeTag = typeTag;
        this.ignoredFields = ignoredFields;
        this.nonnullFields = nonnullFields;
        this.annotationCache = annotationCache;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
        this.hasRedefinedSuperclass = hasRedefinedSuperclass;
        this.redefinedSubclass = redefinedSubclass;
        this.usingGetClass = usingGetClass;
        this.warningsToSuppress = warningsToSuppress;
        this.fieldnameToGetter = fieldnameToGetter;
        this.equalExamples = equalExamples;
        this.unequalExamples = unequalExamples;
    }

    public static <T> Configuration<T> build(
        Class<T> type,
        Set<String> excludedFields,
        Set<String> includedFields,
        Set<String> nonnullFields,
        CachedHashCodeInitializer<T> cachedHashCodeInitializer,
        boolean hasRedefinedSuperclass,
        Class<? extends T> redefinedSubclass,
        boolean usingGetClass,
        EnumSet<Warning> warningsToSuppress,
        Function<String, String> fieldnameToGetter,
        Set<String> ignoredAnnotationClassNames,
        Set<String> actualFields,
        List<T> equalExamples,
        List<T> unequalExamples,
        PrefabValues prefabValues
    ) {
        TypeTag typeTag = new TypeTag(type);
        ClassAccessor<T> classAccessor = ClassAccessor.of(type, prefabValues);
        AnnotationCache annotationCache = buildAnnotationCache(type, ignoredAnnotationClassNames);
        Set<String> ignoredFields = determineIgnoredFields(
            type,
            annotationCache,
            warningsToSuppress,
            excludedFields,
            includedFields,
            actualFields
        );
        Function<String, String> converter = fieldnameToGetter != null
            ? fieldnameToGetter
            : DEFAULT_FIELDNAME_TO_GETTER_CONVERTER;
        List<T> unequals = ensureUnequalExamples(typeTag, classAccessor, unequalExamples);

        return new Configuration<>(
            type,
            typeTag,
            ignoredFields,
            nonnullFields,
            annotationCache,
            cachedHashCodeInitializer,
            hasRedefinedSuperclass,
            redefinedSubclass,
            usingGetClass,
            warningsToSuppress,
            converter,
            equalExamples,
            unequals
        );
    }

    // CHECKSTYLE ON: ParameterNumber

    private static <T> AnnotationCache buildAnnotationCache(
        Class<T> type,
        Set<String> ignoredAnnotationClassNames
    ) {
        AnnotationCacheBuilder acb = new AnnotationCacheBuilder(
            SupportedAnnotations.values(),
            ignoredAnnotationClassNames
        );
        AnnotationCache cache = new AnnotationCache();
        acb.build(type, cache);
        return cache;
    }

    private static <T> Set<String> determineIgnoredFields(
        Class<T> type,
        AnnotationCache annotationCache,
        EnumSet<Warning> warningsToSuppress,
        Set<String> excludedFields,
        Set<String> includedFields,
        Set<String> actualFields
    ) {
        BiFunction<String, Annotation, Boolean> fieldHas = (f, a) ->
            annotationCache.hasFieldAnnotation(type, f, a);

        if (annotationCache.hasClassAnnotation(type, SupportedAnnotations.NATURALID)) {
            return actualFields
                .stream()
                .filter(f -> !fieldHas.apply(f, SupportedAnnotations.NATURALID))
                .collect(Collectors.toSet());
        }

        if (
            annotationCache.hasClassAnnotation(type, SupportedAnnotations.ID) &&
            !warningsToSuppress.contains(Warning.SURROGATE_OR_BUSINESS_KEY)
        ) {
            if (warningsToSuppress.contains(Warning.SURROGATE_KEY)) {
                return actualFields
                    .stream()
                    .filter(f -> !fieldHas.apply(f, SupportedAnnotations.ID))
                    .collect(Collectors.toSet());
            } else {
                Set<String> ignored = actualFields
                    .stream()
                    .filter(f -> fieldHas.apply(f, SupportedAnnotations.ID))
                    .collect(Collectors.toSet());
                ignored.addAll(
                    determineAnnotationlessIgnoredFields(
                        excludedFields,
                        includedFields,
                        actualFields
                    )
                );
                return ignored;
            }
        }
        return determineAnnotationlessIgnoredFields(excludedFields, includedFields, actualFields);
    }

    private static Set<String> determineAnnotationlessIgnoredFields(
        Set<String> excludedFields,
        Set<String> includedFields,
        Set<String> actualFields
    ) {
        if (!includedFields.isEmpty()) {
            return actualFields
                .stream()
                .filter(f -> !includedFields.contains(f))
                .collect(Collectors.toSet());
        }
        return excludedFields;
    }

    private static <T> List<T> ensureUnequalExamples(
        TypeTag typeTag,
        ClassAccessor<T> classAccessor,
        List<T> examples
    ) {
        if (examples.size() > 0) {
            return examples;
        }

        List<T> result = new ArrayList<>();
        result.add(classAccessor.getRedObject(typeTag));
        result.add(classAccessor.getBlueObject(typeTag));
        return result;
    }

    public Class<T> getType() {
        return type;
    }

    public Set<String> getNonnullFields() {
        return Collections.unmodifiableSet(nonnullFields);
    }

    public CachedHashCodeInitializer<T> getCachedHashCodeInitializer() {
        return cachedHashCodeInitializer;
    }

    public boolean hasRedefinedSuperclass() {
        return hasRedefinedSuperclass;
    }

    public Class<? extends T> getRedefinedSubclass() {
        return redefinedSubclass;
    }

    public boolean isUsingGetClass() {
        return usingGetClass;
    }

    public EnumSet<Warning> getWarningsToSuppress() {
        return EnumSet.copyOf(warningsToSuppress);
    }

    public Function<String, String> getFieldnameToGetter() {
        return fieldnameToGetter;
    }

    public List<T> getEqualExamples() {
        return Collections.unmodifiableList(equalExamples);
    }

    public List<T> getUnequalExamples() {
        return Collections.unmodifiableList(unequalExamples);
    }

    public TypeTag getTypeTag() {
        return typeTag;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "A cache is inherently mutable.")
    public AnnotationCache getAnnotationCache() {
        return annotationCache;
    }

    public Set<String> getIgnoredFields() {
        return Collections.unmodifiableSet(ignoredFields);
    }
}
