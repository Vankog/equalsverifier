package nl.jqno.equalsverifier.internal.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.reflection.*;

@AnalyzeClasses(packages = "nl.jqno.equalsverifier")
public final class ArchitectureTest {

    @ArchTest
    public static final ArchRule DONT_USE_VINTAGE_REFLECTION_DIRECTLY = noClasses()
        .that()
        .resideInAPackage("nl.jqno.equalsverifier.internal.checkers..")
        .should()
        .accessClassesThat()
        .areAssignableTo(ClassAccessor.class)
        .orShould()
        .accessClassesThat()
        .areAssignableTo(ObjectAccessor.class)
        .orShould()
        .accessClassesThat()
        .areAssignableTo(FieldAccessor.class)
        .orShould()
        .accessClassesThat()
        .areAssignableTo(FieldModifier.class)
        .orShould()
        .accessClassesThat()
        .areAssignableTo(PrefabValues.class);

    @ArchTest
    public static final ArchRule APACHE_COMMONS = noClasses()
        .that()
        .haveNameNotMatching(".*SuperclassTest.*")
        .should()
        .accessClassesThat()
        .resideInAPackage("org.apache.commons..");

    @ArchTest
    public static final ArchRule AWT = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "java.awt.common.."
    );

    @ArchTest
    public static final ArchRule GUAVA = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "com.google.common.."
    );

    @ArchTest
    public static final ArchRule JAVAFX = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "javafx.."
    );

    @ArchTest
    public static final ArchRule JODA = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "org.joda.."
    );

    private static final String FACTORYPROVIDER_PATTERN =
        "nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders..";
    private static final String TEST_CLASS_PATTERN = ".*Test(\\$.*)?$";

    private ArchitectureTest() {
        // Do not instantiate
    }

    private static ArchRule dontAllowImports_outsideFactoryProvidersAndTests_from(
        String packageName
    ) {
        return noClasses()
            .that()
            .resideOutsideOfPackage(FACTORYPROVIDER_PATTERN)
            .and()
            .haveNameNotMatching(TEST_CLASS_PATTERN)
            .should()
            .accessClassesThat()
            .resideInAPackage(packageName);
    }
}
