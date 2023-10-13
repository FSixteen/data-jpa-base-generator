open module io.github.fsixteen.base.controller {

    requires java.base;
    requires transitive io.github.fsixteen.base.service;

    exports io.github.fsixteen.data.jpa.base.generator.controller;

}