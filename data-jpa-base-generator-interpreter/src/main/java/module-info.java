module io.github.fsixteen.base.plugins {

    requires static org.slf4j;
    requires static java.desktop;
    requires static java.persistence;
    requires static io.github.fsixteen.base.annotations;

    exports io.github.fsixteen.data.jpa.base.generator.plugins;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.cache;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.collections;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.constant;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.descriptors;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.exceptions;
    exports io.github.fsixteen.data.jpa.base.generator.plugins.utils;

}