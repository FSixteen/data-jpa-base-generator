open module io.github.fsixteen.base.entities {

    requires java.base;
    requires transitive io.swagger.annotations;
    requires transitive io.swagger.v3.oas.annotations;

    requires transitive java.validation;
    requires transitive java.persistence;

    requires transitive spring.context;
    requires transitive org.hibernate.orm.core;
    requires transitive com.fasterxml.jackson.annotation;

    requires transitive io.github.fsixteen.common.utils;

    exports io.github.fsixteen.data.jpa.base.generator.beans.groups;
    exports io.github.fsixteen.data.jpa.base.generator.entities;
    exports io.github.fsixteen.data.jpa.base.generator.groups;
    exports io.github.fsixteen.data.jpa.base.generator.query;
    exports io.github.fsixteen.data.jpa.base.generator.rules;

}