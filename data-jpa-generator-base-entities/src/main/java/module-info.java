module io.github.fsixteen.base.entities {

    requires transitive java.logging;
    requires transitive java.persistence;
    requires transitive java.validation;
    requires transitive io.swagger.v3.oas.annotations;
    requires transitive io.swagger.annotations;
    requires transitive spring.context;
    requires transitive org.hibernate.orm.core;
    requires transitive com.fasterxml.jackson.annotation;

    exports io.github.fsixteen.common.utils;
    exports io.github.fsixteen.data.jpa.generator.base.entities;
    exports io.github.fsixteen.data.jpa.generator.base.groups;
    exports io.github.fsixteen.data.jpa.generator.base.query;
    exports io.github.fsixteen.data.jpa.generator.beans.groups;
    exports io.github.fsixteen.data.jpa.generator.rules;

}