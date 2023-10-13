open module io.github.fsixteen.base.service {

    requires java.base;
    requires transitive org.slf4j;
    requires transitive io.swagger.annotations;
    requires transitive io.swagger.v3.oas.annotations;
    requires transitive java.persistence;
    requires transitive java.validation;
    requires transitive java.transaction;
    requires transitive org.apache.tomcat.embed.core;

    requires transitive spring.context;
    requires transitive spring.beans;
    requires transitive spring.core;
    requires transitive spring.data.commons;
    requires transitive spring.data.jpa;
    requires transitive spring.web;
    requires transitive org.hibernate.orm.core;
    requires transitive com.fasterxml.jackson.annotation;

    requires transitive io.github.fsixteen.common.response.structure;
    requires transitive io.github.fsixteen.base.annotations;
    requires transitive io.github.fsixteen.base.plugins;
    requires transitive io.github.fsixteen.base.entities;

    exports io.github.fsixteen.data.jpa.base.generator.config;
    exports io.github.fsixteen.data.jpa.base.generator.constants;
    exports io.github.fsixteen.data.jpa.base.generator.exception;
    exports io.github.fsixteen.data.jpa.base.generator.jpa;
    exports io.github.fsixteen.data.jpa.base.generator.service;
    exports io.github.fsixteen.data.jpa.base.generator.utils;

}