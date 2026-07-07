package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import java.lang.annotation.Annotation;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.github.fsixteen.data.jpa.base.generator.plugins.compiled.CompiledAnnotationSpec;

/**
 * 面向 compiled 主链路的谓词 provider SPI.
 *
 * <p>
 * 每个实现负责把某一类注解对应的 {@link CompiledAnnotationSpec} 落成为最终的 JPA
 * {@link Predicate}. 内建注解通过内建 provider 实现, 业务扩展注解也应实现该接口接入.
 * </p>
 *
 * <p>
 * 该接口已经是当前解释器唯一的运行期注解执行扩展点；
 * 不再存在旧 builder/plugin 风格的执行 SPI.
 * </p>
 *
 * @author FSixteen
 * @since 1.0.3
 */
public interface CompiledPredicateProvider {

    /**
     * 返回当前 provider 负责处理的注解类型.
     *
     * @return 注解类型
     */
    Class<? extends Annotation> annotationType();

    /**
     * 基于编译后的注解规格直接创建最终谓词.
     *
     * @param spec  注解的 compiled 规格
     * @param args  原始请求参数对象
     * @param root  当前查询根实体
     * @param query 当前查询对象
     * @param cb    CriteriaBuilder
     * @return 构建后的谓词
     */
    Predicate create(CompiledAnnotationSpec<? extends Annotation> spec, Object args, Root<?> root, AbstractQuery<?> query, CriteriaBuilder cb);

}
