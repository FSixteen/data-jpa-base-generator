package io.github.fsixteen.base.modules.dictionaries.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesEnvs;
import io.github.fsixteen.data.jpa.generator.base.jpa.BaseDao;

/**
 * DAO: 系统字典信息-系统字典环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Repository
public interface DictionariesEnvsDao extends BaseDao<DictionariesEnvs, Integer> {
}
