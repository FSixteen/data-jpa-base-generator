module io.github.fsixteen.base.service {

    requires static org.slf4j;
    requires static java.persistence;
    requires static java.validation;
    requires static java.transaction;
    requires static org.apache.tomcat.embed.core;
    requires static io.swagger.v3.oas.annotations;
    requires static io.swagger.annotations;
    requires static spring.context;
    requires static spring.beans;
    requires static spring.core;
    requires static spring.data.commons;
    requires static spring.data.jpa;
    requires static spring.web;
    requires static org.hibernate.orm.core;
    requires static com.fasterxml.jackson.annotation;
    requires static io.github.fsixteen.common.response.structure;
    requires static io.github.fsixteen.base.annotations;
    requires static io.github.fsixteen.base.plugins;

    exports io.github.fsixteen.data.jpa.generator.base.controller;
    exports io.github.fsixteen.data.jpa.generator.base.entities;
    exports io.github.fsixteen.data.jpa.generator.base.groups;
    exports io.github.fsixteen.data.jpa.generator.base.jpa;
    exports io.github.fsixteen.data.jpa.generator.base.query;
    exports io.github.fsixteen.data.jpa.generator.base.service;
    exports io.github.fsixteen.data.jpa.generator.beans;
    exports io.github.fsixteen.data.jpa.generator.beans.groups;
    exports io.github.fsixteen.data.jpa.generator.constants;
    exports io.github.fsixteen.data.jpa.generator.exception;
    exports io.github.fsixteen.data.jpa.generator.rules;
    exports io.github.fsixteen.data.jpa.generator.utils;

}