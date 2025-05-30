package nl.jqno.equalsverifier.internal.instantiation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;

import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class InstanceCreatorTest {

    @Test
    void instantiate() throws NoSuchFieldException {
        ClassProbe<SomeClass> probe = ClassProbe.of(SomeClass.class);
        Objenesis objenesis = new ObjenesisStd();
        var sut = new InstanceCreator<InstanceCreatorTest.SomeClass>(probe, objenesis);

        Field x = SomeClass.class.getDeclaredField("x");
        Field z = SomeClass.class.getDeclaredField("z");
        var values = Map.<Field, Object>of(x, 42, z, "42");

        SomeClass actual = sut.instantiate(values);

        assertThat(actual.x).isEqualTo(42);
        assertThat(actual.y).isEqualTo(0);
        assertThat(actual.z).isEqualTo("42");
    }

    @Test
    void copy() throws NoSuchFieldException {
        ClassProbe<SomeSubClass> probe = ClassProbe.of(SomeSubClass.class);
        Objenesis objenesis = new ObjenesisStd();
        var sut = new InstanceCreator<InstanceCreatorTest.SomeSubClass>(probe, objenesis);

        SomeClass original = new SomeClass(42, 1337, "yeah");
        SomeSubClass copy = sut.copy(original);

        assertThat(copy.x).isEqualTo(original.x);
        assertThat(copy.y).isEqualTo(original.y);
        assertThat(copy.z).isEqualTo(original.z);
        assertThat(copy.a).isEqualTo(0);
    }

    static class SomeClass {

        final int x;
        final int y;
        final String z;

        public SomeClass(int x, int y, String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    static class SomeSubClass extends SomeClass {

        final int a;

        public SomeSubClass(int x, int y, String z, int a) {
            super(x, y, z);
            this.a = a;
        }
    }
}
