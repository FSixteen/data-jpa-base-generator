module io.github.fsixteen.base.autoconfigure {

    requires static spring.core;
    requires static spring.beans;
    requires static spring.context;
    requires static spring.data.jpa;
    requires static spring.boot.autoconfigure;

    exports io.github.fsixteen.base.autoconfigure.common;
    exports io.github.fsixteen.base.autoconfigure.configurations;
    exports io.github.fsixteen.base.autoconfigure.dictionaries;
    exports io.github.fsixteen.base.autoconfigure.environments;

}