package io.github.fsixteen.base.modules.dictionaries.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesInfo;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesInfoInsertQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesInfoSelectQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesInfoUpdateQuery;
import io.github.fsixteen.base.modules.dictionaries.service.DictionariesInfoService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统字典信息-系统字典.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@RestController
@RequestMapping("/api/common/dict/info")
@Tag(name = "Fsn-Dictionaries-Info", description = DictionariesInfo.TABLE_DESC)
@Api(tags = { DictionariesInfo.TABLE_DESC })
public class DictionariesInfoController implements BaseController<DictionariesInfoService, DictionariesInfo, Integer, DictionariesInfoInsertQuery,
    DictionariesInfoUpdateQuery, IntegerPrimaryKeyEntity, DictionariesInfoSelectQuery> {

    @Autowired
    private DictionariesInfoService service;

    @Override
    public DictionariesInfoService getService() {
        return this.service;
    }

}