/***/
open module io.github.fsixteen.base.plugins {

    requires java.base;

    /*
     * compiled 注解规格与内省异常路径依赖 java.beans.*，
     * 该内容存在于 java.desktop 模块中。
     */
    requires transitive java.desktop;

    requires org.slf4j;

    requires transitive java.persistence;

    requires transitive io.github.fsixteen.base.annotations;

    uses io.github.fsixteen.data.jpa.base.generator.plugins.spi.CompiledPredicateProvider;

    uses io.github.fsixteen.data.jpa.base.generator.plugins.spi.PredicateExpressionTemplateProvider;

    uses io.github.fsixteen.data.jpa.base.generator.plugins.spi.RegisteredPredicateTemplateSpiProvider;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.cache;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.collections;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.compiled;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.codecs;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.compiler;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.constant;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.expression;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.exceptions;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.registry;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.spi;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.support;

    exports io.github.fsixteen.data.jpa.base.generator.plugins.utils;

}
