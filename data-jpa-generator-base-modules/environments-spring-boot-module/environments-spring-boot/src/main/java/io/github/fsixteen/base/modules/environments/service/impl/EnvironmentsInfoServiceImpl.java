package io.github.fsixteen.base.modules.environments.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.github.fsixteen.base.modules.environments.dao.EnvironmentsInfoDao;
import io.github.fsixteen.base.modules.environments.service.EnvironmentsInfoService;
import io.github.fsixteen.data.jpa.generator.constants.DeleteType;

/**
 * ServiceImpl: 系统配置信息-系统环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Service
public class EnvironmentsInfoServiceImpl implements EnvironmentsInfoService {

    @Autowired
    private EnvironmentsInfoDao dao;

    @Override
    public EnvironmentsInfoDao getDao() {
        return this.dao;
    }

    @Override
    public DeleteType deleteType() {
        return DeleteType.HARD;
    }

}
