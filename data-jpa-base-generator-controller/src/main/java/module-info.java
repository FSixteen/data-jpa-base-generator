open module io.github.fsixteen.base.controller {

    requires java.base;

    requires org.slf4j;

    requires spring.aop;

    requires transitive org.aspectj.weaver;

    requires spring.boot.autoconfigure;

    requires spring.security.core;

    requires spring.expression;

    requires transitive io.github.fsixteen.base.service;

    exports io.github.fsixteen.data.jpa.base.generator.aspect;

    exports io.github.fsixteen.data.jpa.base.generator.autoconfigure;

    exports io.github.fsixteen.data.jpa.base.generator.controller;

    exports io.github.fsixteen.data.jpa.base.generator.prepost;

    exports io.github.fsixteen.data.jpa.base.generator.security;

}
