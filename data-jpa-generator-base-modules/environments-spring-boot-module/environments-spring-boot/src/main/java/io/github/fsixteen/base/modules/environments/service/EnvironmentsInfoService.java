package io.github.fsixteen.base.modules.environments.service;

import io.github.fsixteen.base.domain.environments.entities.EnvironmentsInfo;
import io.github.fsixteen.base.domain.environments.query.EnvironmentsInfoInsertQuery;
import io.github.fsixteen.base.domain.environments.query.EnvironmentsInfoSelectQuery;
import io.github.fsixteen.base.domain.environments.query.EnvironmentsInfoUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统配置信息-系统环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface EnvironmentsInfoService extends
    BaseService<EnvironmentsInfo, Integer, EnvironmentsInfoInsertQuery, IntegerPrimaryKeyEntity, EnvironmentsInfoUpdateQuery, EnvironmentsInfoSelectQuery> {
}
