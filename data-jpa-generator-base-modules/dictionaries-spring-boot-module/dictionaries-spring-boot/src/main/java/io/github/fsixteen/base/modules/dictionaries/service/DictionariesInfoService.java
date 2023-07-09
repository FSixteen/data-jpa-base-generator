package io.github.fsixteen.base.modules.dictionaries.service;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesInfo;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesInfoInsertQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesInfoSelectQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesInfoUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统字典信息-系统字典.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface DictionariesInfoService extends
    BaseService<DictionariesInfo, Integer, DictionariesInfoInsertQuery, IntegerPrimaryKeyEntity, DictionariesInfoUpdateQuery, DictionariesInfoSelectQuery> {
}
