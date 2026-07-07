package io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.fsixteen.data.jpa.base.generator.annotations.constant.CompareOp;

final class CompiledAnnotationOperatorGuards {

    private static final String LENGTH_ANNOTATION_NAME = "io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Length";

    private static final Set<CompareOp> LENGTH_ALLOWED_OPS = EnumSet.of(CompareOp.EQ, CompareOp.NE, CompareOp.GT, CompareOp.GTE, CompareOp.LT, CompareOp.LTE,
        CompareOp.BETWEEN, CompareOp.NOT_BETWEEN, CompareOp.IN, CompareOp.NOT_IN);

    private CompiledAnnotationOperatorGuards() {
    }

    static void validate(final Class<? extends Annotation> annotationType, final CompareOp op) {
        if (isLengthAnnotationFamily(annotationType) && !LENGTH_ALLOWED_OPS.contains(op)) {
            throw new IllegalArgumentException("@" + annotationType.getSimpleName() + " does not support CompareOp." + op + "; supported ops: "
                + Arrays.toString(LENGTH_ALLOWED_OPS.toArray(new CompareOp[0])));
        }
    }

    static boolean isLengthAnnotationFamily(final Class<? extends Annotation> annotationType) {
        return isLengthAnnotationFamily(annotationType, new HashSet<Class<?>>());
    }

    private static boolean isLengthAnnotationFamily(final Class<? extends Annotation> annotationType, final Set<Class<?>> visited) {
        if (Objects.isNull(annotationType) || !annotationType.isAnnotation() || !visited.add(annotationType)) {
            return false;
        }
        if (LENGTH_ANNOTATION_NAME.equals(annotationType.getName())) {
            return true;
        }
        for (Annotation metaAnnotation : annotationType.getAnnotations()) {
            Class<? extends Annotation> metaType = metaAnnotation.annotationType();
            if (isJdkMetaAnnotation(metaType)) {
                continue;
            }
            if (isLengthAnnotationFamily(metaType, new HashSet<Class<?>>(visited))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isJdkMetaAnnotation(final Class<? extends Annotation> annotationType) {
        return null != annotationType.getPackage() && "java.lang.annotation".equals(annotationType.getPackage().getName());
    }

}
