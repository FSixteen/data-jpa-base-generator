package io.github.fsixteen.base.modules.configurations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsKey;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsKeyInsertQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsKeySelectQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsKeyUpdateQuery;
import io.github.fsixteen.base.modules.configurations.service.ConfigurationsKeyService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Controller
@RequestMapping("/api/common/config/key/info")
@Tag(name = "Fsn-Configurations-Key-Info", description = ConfigurationsKey.TABLE_DESC)
@Api(tags = { ConfigurationsKey.TABLE_DESC })
public class ConfigurationsKeyController implements BaseController<ConfigurationsKeyService, ConfigurationsKey, Integer, ConfigurationsKeyInsertQuery,
    ConfigurationsKeyUpdateQuery, IntegerPrimaryKeyEntity, ConfigurationsKeySelectQuery> {

    @Autowired
    private ConfigurationsKeyService service;

    @Override
    public ConfigurationsKeyService getService() {
        return this.service;
    }

}