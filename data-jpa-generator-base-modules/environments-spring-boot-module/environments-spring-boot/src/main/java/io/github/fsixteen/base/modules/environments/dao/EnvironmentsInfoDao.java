package io.github.fsixteen.base.modules.environments.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import io.github.fsixteen.base.domain.environments.entities.EnvironmentsInfo;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;

/**
 * DAO: 系统配置信息-系统环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Repository
public interface EnvironmentsInfoDao extends BaseDao<EnvironmentsInfo, Integer> {
}
