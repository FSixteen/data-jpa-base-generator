package io.github.fsixteen.base.modules.configurations.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsInfo;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsInfoInsertQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsInfoSelectQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsInfoUpdateQuery;
import io.github.fsixteen.base.modules.configurations.service.ConfigurationsInfoService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统配置信息-系统参数.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@RestController
@RequestMapping("/api/common/config/info")
@Tag(name = "Fsn-Configurations-Info", description = ConfigurationsInfo.TABLE_DESC)
@Api(tags = { ConfigurationsInfo.TABLE_DESC })
public class ConfigurationsInfoController implements BaseController<ConfigurationsInfoService, ConfigurationsInfo, Integer, ConfigurationsInfoInsertQuery,
    IntegerPrimaryKeyEntity, ConfigurationsInfoUpdateQuery, ConfigurationsInfoSelectQuery> {

    @Autowired
    private ConfigurationsInfoService service;

    @Override
    public ConfigurationsInfoService getService() {
        return this.service;
    }

}