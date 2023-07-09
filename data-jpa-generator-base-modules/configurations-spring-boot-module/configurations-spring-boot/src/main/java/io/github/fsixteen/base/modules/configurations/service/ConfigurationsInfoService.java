package io.github.fsixteen.base.modules.configurations.service;

import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsInfo;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsInfoInsertQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsInfoSelectQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsInfoUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统配置信息-系统参数.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface ConfigurationsInfoService extends BaseService<ConfigurationsInfo, Integer, ConfigurationsInfoInsertQuery, IntegerPrimaryKeyEntity,
    ConfigurationsInfoUpdateQuery, ConfigurationsInfoSelectQuery> {
}
