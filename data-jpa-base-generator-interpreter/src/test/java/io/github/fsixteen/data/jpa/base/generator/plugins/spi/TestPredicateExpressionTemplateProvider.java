package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import java.util.Arrays;

import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.PredicateExpressionTemplate;

public class TestPredicateExpressionTemplateProvider implements PredicateExpressionTemplateProvider {

    @Override
    public String name() {
        return "spi.ignoreCaseName";
    }

    @Override
    public PredicateExpressionTemplate template() {
        return new PredicateExpressionTemplate() {

            @Override
            public io.github.fsixteen.data.jpa.base.generator.plugins.expression.PredicateExpression create() {
                return FunctionExpression.of("lower", String.class, Arrays.asList(PathExpression.of("name")));
            }

        };
    }

}
