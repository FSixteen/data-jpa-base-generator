package io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.fsixteen.data.jpa.base.generator.annotations.PredicateRole;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;
import io.github.fsixteen.data.jpa.base.generator.annotations.constant.ExprType;

class LengthAnnotationContractTest {

    @Test
    void shouldKeepLengthShortcutCanonicalAndHighlyConfigurable() throws Exception {
        Length length = LengthModel.class.getDeclaredField("nameLength").getAnnotation(Length.class);

        assertExactMethods(Length.class, "op", "left", "right", "extra", "options");
        assertNoLegacyMethods(Length.class);
        assertNotDeprecatedShortcut(Length.class);
        assertEquals(CompareOp.EQ, Length.class.getAnnotation(Compare.class).op());
        assertEquals(ExprType.FUNCTION, length.left().type());
        assertEquals("length", length.left().function().name());
        assertEquals(Integer.class, length.left().function().type());
        assertEquals(1, length.left().function().args().length);
        assertEquals(ExprType.PATH, length.left().function().args()[0].type());
        assertEquals(ExprType.VALUE, length.right().type());
        assertEquals(0, length.extra().length);
    }

    @Test
    void shouldPointDirectlyAtCanonicalMetaAnnotations() {
        assertDirectCustomMetaAnnotations(Length.class, PredicateRole.class, Compare.class);
    }

    private static void assertNoLegacyMethods(final Class<? extends Annotation> annotationType) {
        List<String> legacyMethods = Arrays.asList("scope", "groups", "field", "fieldType", "fieldLiteral", "fieldFunction", "fieldProcessor", "valueType",
            "valueLiteral", "valueFunction", "valueFunctions", "valueProcessor", "required", "not", "ignoreNull", "ignoreEmpty", "ignoreBlank", "trim");
        Arrays.stream(annotationType.getDeclaredMethods()).filter(method -> legacyMethods.contains(method.getName())).findAny().ifPresent(method -> {
            throw new AssertionError(annotationType.getSimpleName() + "." + method.getName() + "() should be removed from pure canonical shortcuts");
        });
    }

    @SafeVarargs
    private static void assertDirectCustomMetaAnnotations(final Class<? extends Annotation> annotationType,
        final Class<? extends Annotation>... expectedMetaTypes) {
        List<String> actual = Arrays.stream(annotationType.getAnnotations()).map(Annotation::annotationType)
            .filter(it -> !isJdkMetaAnnotation(it) && !Deprecated.class.equals(it)).map(Class::getSimpleName).sorted().toList();
        List<String> expected = new ArrayList<String>();
        Arrays.stream(expectedMetaTypes).map(Class::getSimpleName).forEach(expected::add);
        expected.sort(String::compareTo);
        assertEquals(expected, actual, () -> annotationType.getSimpleName() + " should point directly at canonical meta annotations");
    }

    private static void assertMethod(final Class<? extends Annotation> annotationType, final String methodName) {
        Method method = Arrays.stream(annotationType.getDeclaredMethods()).filter(it -> methodName.equals(it.getName())).findFirst()
            .orElseThrow(() -> new AssertionError(annotationType.getSimpleName() + "." + methodName + "() is missing"));
        assertTrue(null != method, () -> annotationType.getSimpleName() + "." + methodName + "() is missing");
    }

    private static void assertExactMethods(final Class<? extends Annotation> annotationType, final String... expectedMethodNames) {
        List<String> actual = Arrays.stream(annotationType.getDeclaredMethods()).map(Method::getName).sorted().toList();
        List<String> expected = new ArrayList<String>(Arrays.asList(expectedMethodNames));
        expected.sort(String::compareTo);
        assertEquals(expected, actual, () -> annotationType.getSimpleName() + " declared methods changed unexpectedly");
        expected.forEach(method -> assertMethod(annotationType, method));
    }

    private static void assertNotDeprecatedShortcut(final Class<? extends Annotation> annotationType) {
        assertTrue(!annotationType.isAnnotationPresent(Deprecated.class), () -> annotationType.getSimpleName() + " should not be deprecated");
        Arrays.stream(annotationType.getDeclaredClasses()).filter(Class::isAnnotation)
            .forEach(innerType -> assertTrue(!innerType.isAnnotationPresent(Deprecated.class),
                () -> annotationType.getSimpleName() + "." + innerType.getSimpleName() + " should not be deprecated"));
    }

    private static boolean isJdkMetaAnnotation(final Class<? extends Annotation> annotationType) {
        return null != annotationType.getPackage() && "java.lang.annotation".equals(annotationType.getPackage().getName());
    }

    @SuppressWarnings("unused")
    private static final class LengthModel {

        @Length
        private Integer nameLength;

    }

}
