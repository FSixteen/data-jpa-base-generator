module io.github.fsixteen.base.autoconfigure {

    requires transitive spring.core;
    requires transitive spring.beans;
    requires transitive spring.context;
    requires transitive spring.data.jpa;
    requires transitive spring.boot.autoconfigure;

    exports io.github.fsixteen.base.autoconfigure.common;
    exports io.github.fsixteen.base.autoconfigure.configurations;
    exports io.github.fsixteen.base.autoconfigure.dictionaries;
    exports io.github.fsixteen.base.autoconfigure.environments;

}