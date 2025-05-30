package nl.jqno.equalsverifier.internal.instantiation.vintage.factories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;

import nl.jqno.equalsverifier.internal.exceptions.ReflectionException;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;

/**
 * Abstract implementation of {@link PrefabValueFactory} that provides helper functions for dealing with generics.
 */
@SuppressWarnings("NonApiType") // LinkedHashSet is needed for its stack properties.
public abstract class AbstractGenericFactory<T> implements PrefabValueFactory<T> {

    public static final TypeTag OBJECT_TYPE_TAG = new TypeTag(Object.class);

    protected LinkedHashSet<TypeTag> cloneWith(LinkedHashSet<TypeTag> typeStack, TypeTag tag) {
        @SuppressWarnings("unchecked")
        LinkedHashSet<TypeTag> clone = (LinkedHashSet<TypeTag>) typeStack.clone();
        clone.add(tag);
        return clone;
    }

    protected TypeTag copyGenericTypesInto(Class<?> type, TypeTag source) {
        var genericTypes = source.genericTypes();
        return new TypeTag(type, genericTypes.toArray(new TypeTag[genericTypes.size()]));
    }

    protected TypeTag determineAndCacheActualTypeTag(
            int n,
            TypeTag tag,
            VintageValueProvider valueProvider,
            LinkedHashSet<TypeTag> typeStack) {
        return determineAndCacheActualTypeTag(n, tag, valueProvider, typeStack, null);
    }

    protected TypeTag determineAndCacheActualTypeTag(
            int n,
            TypeTag tag,
            VintageValueProvider valueProvider,
            LinkedHashSet<TypeTag> typeStack,
            Class<?> bottomType) {
        TypeTag result = determineActualTypeTagFor(n, tag);
        if (bottomType != null && result.getType().equals(Object.class)) {
            result = new TypeTag(bottomType);
        }
        valueProvider.realizeCacheFor(result, typeStack);
        return result;
    }

    protected TypeTag determineActualTypeTagFor(int n, TypeTag typeTag) {
        List<TypeTag> genericTypes = typeTag.genericTypes();
        if (genericTypes.size() <= n) {
            return OBJECT_TYPE_TAG;
        }

        return genericTypes.get(n);
    }

    protected void invoke(Class<?> type, Object receiver, String methodName, Class<?>[] classes, Object[] values) {
        try {
            Method method = type.getMethod(methodName, classes);
            // Not necessary in the common case, but required for
            // https://bugs.java.com/view_bug.do?bug_id=6924232.
            method.setAccessible(true);
            method.invoke(receiver, values);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }
}
