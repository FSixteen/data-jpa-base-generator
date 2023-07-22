open module io.github.fsixteen.base.modules.configurations {

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
    requires transitive spring.boot.autoconfigure;
    requires transitive org.hibernate.orm.core;
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive io.github.fsixteen.common.response.structure;
    requires transitive io.github.fsixteen.tableinit;
    requires transitive io.github.fsixteen.base.annotations;
    requires transitive io.github.fsixteen.base.plugins;
    requires transitive io.github.fsixteen.base.entities;
    requires transitive io.github.fsixteen.base.service;
    requires transitive io.github.fsixteen.base.domain;
    requires transitive io.github.fsixteen.base.modules.environments;

    exports io.github.fsixteen.base.modules.configurations.controller;
    exports io.github.fsixteen.base.modules.configurations.dao;
    exports io.github.fsixteen.base.modules.configurations.service;
    exports io.github.fsixteen.base.modules.configurations.service.impl;

}