package io.github.fsixteen.base.modules.configurations.service;

import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsKey;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsKeyInsertQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsKeySelectQuery;
import io.github.fsixteen.base.domain.configurations.query.ConfigurationsKeyUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface ConfigurationsKeyService extends
    BaseService<ConfigurationsKey, Integer, ConfigurationsKeyInsertQuery, IntegerPrimaryKeyEntity, ConfigurationsKeyUpdateQuery, ConfigurationsKeySelectQuery> {
}
