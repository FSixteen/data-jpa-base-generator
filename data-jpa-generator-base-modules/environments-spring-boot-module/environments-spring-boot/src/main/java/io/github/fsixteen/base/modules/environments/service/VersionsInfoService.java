package io.github.fsixteen.base.modules.environments.service;

import io.github.fsixteen.base.domain.environments.entities.VersionsInfo;
import io.github.fsixteen.base.domain.environments.query.VersionsInfoInsertQuery;
import io.github.fsixteen.base.domain.environments.query.VersionsInfoSelectQuery;
import io.github.fsixteen.base.domain.environments.query.VersionsInfoUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface VersionsInfoService
    extends BaseService<VersionsInfo, Integer, VersionsInfoInsertQuery, IntegerPrimaryKeyEntity, VersionsInfoUpdateQuery, VersionsInfoSelectQuery> {
}
