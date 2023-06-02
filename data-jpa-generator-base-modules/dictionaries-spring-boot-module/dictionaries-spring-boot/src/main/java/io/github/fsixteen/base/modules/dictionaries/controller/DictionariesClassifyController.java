package io.github.fsixteen.base.modules.dictionaries.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesClassify;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesClassifyInsertQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesClassifySelectQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesClassifyUpdateQuery;
import io.github.fsixteen.base.modules.dictionaries.service.DictionariesClassifyService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统字典信息-系统字典分类编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Controller
@RequestMapping("/api/common/dict/classify/info")
@Tag(name = "Fsn-Dictionaries-Classify-Info", description = DictionariesClassify.TABLE_DESC)
@Api(tags = { DictionariesClassify.TABLE_DESC })
public class DictionariesClassifyController implements BaseController<DictionariesClassifyService, DictionariesClassify, Integer,
    DictionariesClassifyInsertQuery, DictionariesClassifyUpdateQuery, IntegerPrimaryKeyEntity, DictionariesClassifySelectQuery> {

    @Autowired
    private DictionariesClassifyService service;

    @Override
    public DictionariesClassifyService getService() {
        return this.service;
    }

}