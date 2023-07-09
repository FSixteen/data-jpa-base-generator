package io.github.fsixteen.base.modules.environments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.fsixteen.base.domain.environments.entities.EnvironmentsInfo;
import io.github.fsixteen.base.domain.environments.query.EnvironmentsInfoInsertQuery;
import io.github.fsixteen.base.domain.environments.query.EnvironmentsInfoSelectQuery;
import io.github.fsixteen.base.domain.environments.query.EnvironmentsInfoUpdateQuery;
import io.github.fsixteen.base.modules.environments.service.EnvironmentsInfoService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统配置信息-系统环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@RestController
@RequestMapping("/api/common/envs/info")
@Tag(name = "Fsn-Environments-Info", description = EnvironmentsInfo.TABLE_DESC)
@Api(tags = { EnvironmentsInfo.TABLE_DESC })
public class EnvironmentsInfoController implements BaseController<EnvironmentsInfoService, EnvironmentsInfo, Integer, EnvironmentsInfoInsertQuery,
    IntegerPrimaryKeyEntity, EnvironmentsInfoUpdateQuery, EnvironmentsInfoSelectQuery> {

    @Autowired
    private EnvironmentsInfoService service;

    @Override
    public EnvironmentsInfoService getService() {
        return this.service;
    }

}