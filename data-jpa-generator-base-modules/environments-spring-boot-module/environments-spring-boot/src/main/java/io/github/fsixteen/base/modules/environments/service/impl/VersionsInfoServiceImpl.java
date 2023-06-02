package io.github.fsixteen.base.modules.environments.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.github.fsixteen.base.modules.environments.dao.VersionsInfoDao;
import io.github.fsixteen.base.modules.environments.service.VersionsInfoService;
import io.github.fsixteen.data.jpa.generator.constants.DeleteType;

/**
 * ServiceImpl: 系统配置信息-模块版本信息.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Service
public class VersionsInfoServiceImpl implements VersionsInfoService {

    @Autowired
    private VersionsInfoDao dao;

    @Override
    public VersionsInfoDao getDao() {
        return this.dao;
    }

    @Override
    public DeleteType deleteType() {
        return DeleteType.HARD;
    }

}
