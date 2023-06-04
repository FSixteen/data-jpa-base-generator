module io.github.fsixteen.base.plugins {

    /* AnnotationDescriptor 及 IntrospectionRuntimeException 中 java.beans 使用. */
    requires static java.desktop;
    requires static org.slf4j;
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