package io.github.fsixteen.base.modules.environments.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.fsixteen.base.domain.environments.entities.VersionsInfo;
import io.github.fsixteen.base.domain.environments.query.VersionsInfoInsertQuery;
import io.github.fsixteen.base.domain.environments.query.VersionsInfoSelectQuery;
import io.github.fsixteen.base.domain.environments.query.VersionsInfoUpdateQuery;
import io.github.fsixteen.base.modules.environments.service.VersionsInfoService;
import io.github.fsixteen.data.jpa.generator.base.controller.BaseController;
import io.github.fsixteen.data.jpa.generator.base.query.IntegerPrimaryKeyEntity;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@RestController
@RequestMapping("/api/common/versions/info")
@Tag(name = "Fsn-Versions-Info", description = VersionsInfo.TABLE_DESC)
@Api(tags = { VersionsInfo.TABLE_DESC })
public class VersionsInfoController implements BaseController<VersionsInfoService, VersionsInfo, Integer, VersionsInfoInsertQuery, VersionsInfoUpdateQuery,
    IntegerPrimaryKeyEntity, VersionsInfoSelectQuery> {

    @Autowired
    private VersionsInfoService service;

    @Override
    public VersionsInfoService getService() {
        return this.service;
    }

}