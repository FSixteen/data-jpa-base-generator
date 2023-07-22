open module io.github.fsixteen.base.domain {

    requires static java.persistence;
    requires static java.validation;
    requires static io.swagger.v3.oas.annotations;
    requires static io.swagger.annotations;
    requires static org.hibernate.orm.core;
    requires static com.fasterxml.jackson.annotation;
    requires static io.github.fsixteen.base.annotations;
    requires static io.github.fsixteen.base.plugins;
    requires static io.github.fsixteen.base.entities;

    exports io.github.fsixteen.base.domain.configurations.entities;
    exports io.github.fsixteen.base.domain.configurations.query;
    exports io.github.fsixteen.base.domain.dictionaries.entities;
    exports io.github.fsixteen.base.domain.dictionaries.query;
    exports io.github.fsixteen.base.domain.environments.entities;
    exports io.github.fsixteen.base.domain.environments.query;

}