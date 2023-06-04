module io.github.fsixteen.base.service {

    requires transitive org.slf4j;
    requires transitive java.persistence;
    requires transitive java.validation;
    requires transitive java.transaction;
    requires transitive org.apache.tomcat.embed.core;
    requires transitive io.swagger.v3.oas.annotations;
    requires transitive io.swagger.annotations;
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

    exports io.github.fsixteen.data.jpa.generator.base.controller;
    exports io.github.fsixteen.data.jpa.generator.base.jpa;
    exports io.github.fsixteen.data.jpa.generator.base.service;
    exports io.github.fsixteen.data.jpa.generator.constants;
    exports io.github.fsixteen.data.jpa.generator.exception;
    exports io.github.fsixteen.data.jpa.generator.utils;

}