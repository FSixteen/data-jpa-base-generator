package io.github.fsixteen.base.modules.configurations.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsKey;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;

/**
 * DAO: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Repository
public interface ConfigurationsKeyDao extends BaseDao<ConfigurationsKey, Integer> {
}
