module io.github.fsixteen.base.modules.environments {

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
    requires static io.github.fsixteen.tableinit;
    requires static io.github.fsixteen.base.annotations;
    requires static io.github.fsixteen.base.plugins;
    requires static io.github.fsixteen.base.service;
    requires static io.github.fsixteen.base.domain;

    exports io.github.fsixteen.base.modules.environments.controller;
    exports io.github.fsixteen.base.modules.environments.dao;
    exports io.github.fsixteen.base.modules.environments.service;
    exports io.github.fsixteen.base.modules.environments.service.impl;

}