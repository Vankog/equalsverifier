package nl.jqno.equalsverifier.internal.reflection.vintage;

import static nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factories.Factories.values;
import static org.junit.jupiter.api.Assertions.*;

import java.text.AttributedString;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.EmptyValueProvider;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.Point3D;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.StaticFinalContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class InPlaceObjectAccessorScramblingTest {

    private static final LinkedHashSet<TypeTag> EMPTY_TYPE_STACK = new LinkedHashSet<>();
    private Objenesis objenesis;
    private VintageValueProvider valueProviderTest;

    @BeforeEach
    public void setup() {
        FactoryCache factoryCache = JavaApiPrefabValues.build();
        factoryCache.put(Point.class, values(new Point(1, 2), new Point(2, 3), new Point(1, 2)));
        objenesis = new ObjenesisStd();
        valueProviderTest =
            new VintageValueProvider(EmptyValueProvider.INSTANCE, factoryCache, objenesis);
    }

    @Test
    public void scrambleReturnsThis() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        ObjectAccessor<Object> actual = doScramble(copy);
        assertSame(copy, actual.get());
    }

    @Test
    public void scramble() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        assertTrue(original.equals(copy));
        doScramble(copy);
        assertFalse(original.equals(copy));
    }

    @Test
    public void deepScramble() {
        Point3D modified = new Point3D(2, 3, 4);
        Point3D reference = copy(modified);

        doScramble(modified);

        assertFalse(modified.equals(reference));
        modified.z = 4;
        assertFalse(modified.equals(reference));
    }

    @SuppressWarnings("static-access")
    @Test
    public void scrambleStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        int originalInt = StaticFinalContainer.CONST;
        Object originalObject = StaticFinalContainer.OBJECT;

        doScramble(foo);

        assertEquals(originalInt, foo.CONST);
        assertEquals(originalObject, foo.OBJECT);
    }

    @Test
    public void scrambleString() {
        StringContainer foo = new StringContainer();
        String before = foo.s;
        doScramble(foo);
        assertFalse(before.equals(foo.s));
    }

    @Test
    public void privateFinalStringCannotBeScrambled() {
        FinalAssignedStringContainer foo = new FinalAssignedStringContainer();
        String before = foo.s;

        doScramble(foo);

        assertEquals(before, foo.s);
    }

    @Test
    public void scramblePrivateFinalPoint() {
        FinalAssignedPointContainer foo = new FinalAssignedPointContainer();
        Point before = foo.p;

        assertTrue(before.equals(foo.p));
        doScramble(foo);
        assertFalse(before.equals(foo.p));
    }

    @Test
    public void scrambleNestedGenerics() {
        GenericContainerContainer foo = new GenericContainerContainer();

        assertTrue(foo.strings.ts.isEmpty());
        assertTrue(foo.points.ts.isEmpty());

        doScramble(foo);

        assertFalse(foo.strings.ts.isEmpty());
        assertEquals(String.class, foo.strings.ts.get(0).getClass());
        assertFalse(foo.points.ts.isEmpty());
        assertEquals(Point.class, foo.points.ts.get(0).getClass());
    }

    @Test
    @DisabledForJreRange(max = JRE.JAVA_11)
    public void scrambleSutInaccessible() {
        AttributedString as = new AttributedString("x");

        ExpectedException
            .when(() -> doScramble(as))
            // InaccessibleObjectException, but it's not available in Java 8
            .assertThrows(RuntimeException.class)
            .assertMessageContains("accessible: module", "does not \"opens");
    }

    @Test
    @DisabledForJreRange(max = JRE.JAVA_11)
    public void scrambleFieldInaccessible() {
        InaccessibleContainer ic = new InaccessibleContainer(new AttributedString("x"));

        ExpectedException.when(() -> doScramble(ic)).assertThrows(ModuleException.class);
    }

    @SuppressWarnings("unchecked")
    private <T> InPlaceObjectAccessor<T> create(T object) {
        return new InPlaceObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T copy(T object) {
        return create(object).copy(objenesis);
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(valueProviderTest, TypeTag.NULL, EMPTY_TYPE_STACK);
    }

    static final class StringContainer {

        private String s = "x";
    }

    static final class FinalAssignedStringContainer {

        private final String s = "x";
    }

    static final class FinalAssignedPointContainer {

        private final Point p = new Point(2, 3);
    }

    static final class GenericContainerContainer {

        private final GenericContainer<String> strings = new GenericContainer<>(
            new ArrayList<String>()
        );
        private final GenericContainer<Point> points = new GenericContainer<>(
            new ArrayList<Point>()
        );
    }

    static final class GenericContainer<T> {

        private List<T> ts;

        public GenericContainer(List<T> ts) {
            this.ts = ts;
        }
    }

    @SuppressWarnings("unused")
    static final class InaccessibleContainer {

        private AttributedString as;

        public InaccessibleContainer(AttributedString as) {
            this.as = as;
        }
    }
}
