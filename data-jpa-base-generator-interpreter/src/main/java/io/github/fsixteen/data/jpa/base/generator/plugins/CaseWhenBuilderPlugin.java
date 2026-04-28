package io.github.fsixteen.data.jpa.base.generator.plugins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.DefaultPredicateProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Case;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseThen;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.CaseWhen;
import io.github.fsixteen.data.jpa.base.generator.annotations.plugins.Cases;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.AnnotationDescriptor;
import io.github.fsixteen.data.jpa.base.generator.plugins.descriptors.ComputerDescriptor;

/**
 * TODO :: 规划中.
 * 分支条件(select * from table_name where (xxxxxx)).<br>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public class CaseWhenBuilderPlugin extends AbstractComputerBuilderPlugin<Cases> {

    private static final Logger LOG = LoggerFactory.getLogger(CaseWhenBuilderPlugin.class);

    @Override
    public ComputerDescriptor<Cases> toPredicate(AnnotationDescriptor<Cases> ad, Object obj, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb) {
        try {
            Object fieldValue = this.getFieldValue(ad, obj);
            Cases cases = ad.getAnno();
            for (Case cs : ad.getAnno().value()) {
                if (this.getPredicateByCaseWhen(cs.when()).test(obj)) {
                    return ComputerDescriptor.of(ad, this.getProcessorByCaseWhen(cs.then()).create(ad.getAnno(), cs, cs.when(), cs.then(), obj,
                        cs.field().isEmpty() ? cases.field() : cs.field(), fieldValue, root, query, cb));
                }
            }
            return ComputerDescriptor.of(ad, null);
        } catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private static Map<String, PredicateProcessor> STRING_PROCESSOR_CACHE = new ConcurrentHashMap<>();

    private static Map<Class<?>, PredicateProcessor> CLASS_PROCESSOR_CACHE = new ConcurrentHashMap<>();

    private PredicateProcessor getProcessorByCaseWhen(CaseThen then) {
        try {
            if (then.processorClassName() != null && !then.processorClassName().isEmpty()) {
                return this.getProcessorByClassName(then.processorClassName());
            }
            if (then.processorClass() != null) {
                return this.getProcessorByClass(then.processorClass());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return new DefaultPredicateProcessor();
    }

    private PredicateProcessor getProcessorByClassName(String className) throws ReflectiveOperationException {
        if (STRING_PROCESSOR_CACHE.containsKey(className)) {
            return STRING_PROCESSOR_CACHE.get(className);
        } else {
            synchronized (STRING_PROCESSOR_CACHE) {
                if (STRING_PROCESSOR_CACHE.containsKey(className)) {
                    return STRING_PROCESSOR_CACHE.get(className);
                } else {
                    try {
                        Class<?> testClass = Class.forName(className);
                        this.validatePredicateClass(testClass);
                        PredicateProcessor p = (PredicateProcessor) testClass.getDeclaredConstructor().newInstance();
                        STRING_PROCESSOR_CACHE.put(className, p);
                        return p;
                    } catch (Exception e) {
                        throw new ReflectiveOperationException("PredicateProcessor class not found: " + className, e);
                    }
                }
            }
        }
    }

    private PredicateProcessor getProcessorByClass(Class<?> processorClass) throws ReflectiveOperationException {
        this.validateProcessorClass(processorClass);
        if (CLASS_PROCESSOR_CACHE.containsKey(processorClass)) {
            return CLASS_PROCESSOR_CACHE.get(processorClass);
        } else {
            synchronized (CLASS_PROCESSOR_CACHE) {
                if (CLASS_PROCESSOR_CACHE.containsKey(processorClass)) {
                    return CLASS_PROCESSOR_CACHE.get(processorClass);
                } else {
                    try {
                        PredicateProcessor p = (PredicateProcessor) processorClass.getDeclaredConstructor().newInstance();
                        CLASS_PROCESSOR_CACHE.put(processorClass, p);
                        return p;
                    } catch (Exception e) {
                        throw new ReflectiveOperationException("PredicateProcessor class not found: " + processorClass.getName(), e);
                    }
                }
            }
        }
    }

    private void validateProcessorClass(Class<?> clazz) {
        if (!PredicateProcessor.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                "Class " + clazz.getName() + " does not implement io.github.fsixteen.data.jpa.base.generator.annotations.interfaces.PredicateProcessor");
        }
    }

    private static Map<String, java.util.function.Predicate<Object>> STRING_PREDICATE_CACHE = new ConcurrentHashMap<>();

    private static Map<Class<?>, java.util.function.Predicate<Object>> CLASS_PREDICATE_CACHE = new ConcurrentHashMap<>();

    private Predicate<Object> getPredicateByCaseWhen(CaseWhen when) {
        try {
            if (when.testClassName() != null && !when.testClassName().isEmpty()) {
                return this.getPredicateByClassName(when.testClassName());
            }
            if (when.testClass() != null) {
                return this.getPredicateByClass(when.testClass());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return (k) -> true;
    }

    @SuppressWarnings("unchecked")
    private Predicate<Object> getPredicateByClassName(String className) throws ReflectiveOperationException {
        if (STRING_PREDICATE_CACHE.containsKey(className)) {
            return STRING_PREDICATE_CACHE.get(className);
        } else {
            synchronized (STRING_PREDICATE_CACHE) {
                if (STRING_PREDICATE_CACHE.containsKey(className)) {
                    return STRING_PREDICATE_CACHE.get(className);
                } else {
                    try {
                        Class<?> testClass = Class.forName(className);
                        this.validatePredicateClass(testClass);
                        Predicate<Object> p = (Predicate<Object>) testClass.getDeclaredConstructor().newInstance();
                        STRING_PREDICATE_CACHE.put(className, p);
                        return p;
                    } catch (Exception e) {
                        throw new ReflectiveOperationException("Predicate class not found: " + className, e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Predicate<Object> getPredicateByClass(Class<?> testClass) throws ReflectiveOperationException {
        this.validatePredicateClass(testClass);
        if (CLASS_PREDICATE_CACHE.containsKey(testClass)) {
            return CLASS_PREDICATE_CACHE.get(testClass);
        } else {
            synchronized (CLASS_PREDICATE_CACHE) {
                if (CLASS_PREDICATE_CACHE.containsKey(testClass)) {
                    return CLASS_PREDICATE_CACHE.get(testClass);
                } else {
                    try {
                        Predicate<Object> p = (Predicate<Object>) testClass.getDeclaredConstructor().newInstance();
                        CLASS_PREDICATE_CACHE.put(testClass, p);
                        return p;
                    } catch (Exception e) {
                        throw new ReflectiveOperationException("Predicate class not found: " + testClass.getName(), e);
                    }
                }
            }
        }
    }

    private void validatePredicateClass(Class<?> clazz) {
        if (!Predicate.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " does not implement java.util.function.Predicate");
        }
    }

}
