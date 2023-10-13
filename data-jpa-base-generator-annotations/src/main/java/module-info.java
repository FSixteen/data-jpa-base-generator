open module io.github.fsixteen.base.annotations {

    requires java.base;
    requires transitive java.persistence;

    exports io.github.fsixteen.data.jpa.base.generator.annotations;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.constant;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.interfaces;
    exports io.github.fsixteen.data.jpa.base.generator.annotations.plugins;

}