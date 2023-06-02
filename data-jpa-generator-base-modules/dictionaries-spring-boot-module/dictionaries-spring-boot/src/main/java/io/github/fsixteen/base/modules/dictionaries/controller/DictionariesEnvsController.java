package io.github.fsixteen.base.modules.dictionaries.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesEnvs;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesEnvsInsertQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesEnvsSelectQuery;
import io.github.fsixteen.base.domain.dictionaries.query.DictionariesEnvsUpdateQuery;
import io.github.fsixteen.base.modules.dictionaries.service.DictionariesEnvsService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统字典信息-系统字典环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Controller
@RequestMapping("/api/common/dict/envs/info")
@Tag(name = "Fsn-Dictionaries-Environments-Info", description = DictionariesEnvs.TABLE_DESC)
@Api(tags = { DictionariesEnvs.TABLE_DESC })
public class DictionariesEnvsController implements BaseController<DictionariesEnvsService, DictionariesEnvs, Integer, DictionariesEnvsInsertQuery,
    DictionariesEnvsUpdateQuery, IntegerPrimaryKeyEntity, DictionariesEnvsSelectQuery> {

    @Autowired
    private DictionariesEnvsService service;

    @Override
    public DictionariesEnvsService getService() {
        return this.service;
    }

}