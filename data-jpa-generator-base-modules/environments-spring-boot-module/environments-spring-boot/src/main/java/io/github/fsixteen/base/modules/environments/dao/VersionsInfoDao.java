package io.github.fsixteen.base.modules.environments.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import io.github.fsixteen.base.domain.environments.entities.VersionsInfo;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;

/**
 * DAO: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Repository
public interface VersionsInfoDao extends BaseDao<VersionsInfo, Integer> {
}
