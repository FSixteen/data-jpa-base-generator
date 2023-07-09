package io.github.fsixteen.base.modules.dictionaries.service;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesClassify;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesClassifyInsertQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesClassifySelectQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesClassifyUpdateQuery;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.github.fsixteen.data.jpa.generator.base.service.BaseService;

/**
 * Service: 系统字典信息-系统字典分类编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public interface DictionariesClassifyService extends BaseService<DictionariesClassify, Integer, DictionariesClassifyInsertQuery, IntegerPrimaryKeyEntity,
    DictionariesClassifyUpdateQuery, DictionariesClassifySelectQuery> {
}
