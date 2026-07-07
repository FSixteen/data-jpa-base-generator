package io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;

import java.io.Serializable;

import javax.persistence.criteria.Predicate;

/**
 * `Cases` 分支命中后的谓词处理器.
 *
 * @author FSixteen
 * @since 1.0.3
 */
@FunctionalInterface
public interface PredicateProcessor extends Serializable {

    Predicate create(PredicateProcessorContext context);

}
