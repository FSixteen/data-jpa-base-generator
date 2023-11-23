/***/
open module io.github.fsixteen.base.plugins {

    requires java.base;
    /*
     * AnnotationDescriptor 及 IntrospectionRuntimeException 中使用了 java.beans.*,
     * 该内容存在于 java.desktop 模块中.
     */
    requires java.desktop;
    requires transitive org.slf4j;
    requires transitive java.persistence;
    requires transitive io.github.fsixteen.base.annotations;

    exports io.github.fsixteen.data.jpa.base.generator.plugins;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.cache;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.collections;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.constant;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.descriptors;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.exceptions;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.utils;

}