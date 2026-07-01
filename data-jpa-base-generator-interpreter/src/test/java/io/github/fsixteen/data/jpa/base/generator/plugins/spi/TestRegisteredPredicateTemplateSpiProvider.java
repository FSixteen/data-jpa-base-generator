package io.github.fsixteen.data.jpa.base.generator.plugins.spi;

import java.util.Arrays;

import io.github.fsixteen.data.jpa.base.generator.plugins.constant.ComparableType;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.FunctionExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.expression.PathExpression;
import io.github.fsixteen.data.jpa.base.generator.plugins.registry.RegisteredPredicateTemplate;

public class TestRegisteredPredicateTemplateSpiProvider implements RegisteredPredicateTemplateSpiProvider {

    @Override
    public String name() {
        return "spi.ignoreCaseEqual";
    }

    @Override
    public RegisteredPredicateTemplate create() {
        return RegisteredPredicateTemplate.of(FunctionExpression.of("lower", String.class, Arrays.asList(PathExpression.of("name"))),
            FunctionExpression.of("lower", String.class, Arrays.asList(PathExpression.of("keyword"))), ComparableType.EQ);
    }

}
