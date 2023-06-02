module io.github.fsixteen.base.annotations {

    requires static java.persistence;

    exports io.github.fsixteen.data.jpa.base.generator.annotations;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.constant;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.plugins;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.plugins.utils;

}