package io.github.fsixteen.base.modules.dictionaries.service;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesEnvs;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesEnvsInsertQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesEnvsSelectQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesEnvsUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统字典信息-系统字典环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface DictionariesEnvsService extends
    BaseService<DictionariesEnvs, Integer, DictionariesEnvsInsertQuery, IntegerPrimaryKeyEntity, DictionariesEnvsUpdateQuery, DictionariesEnvsSelectQuery> {
}
