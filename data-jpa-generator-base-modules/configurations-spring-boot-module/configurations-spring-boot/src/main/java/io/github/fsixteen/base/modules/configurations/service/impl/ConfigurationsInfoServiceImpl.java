package io.github.fsixteen.base.modules.configurations.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.github.fsixteen.base.modules.configurations.dao.ConfigurationsInfoDao;
import io.github.fsixteen.base.modules.configurations.service.ConfigurationsInfoService;
import io.github.fsixteen.data.jpa.generator.constants.DeleteType;

/**
 * ServiceImpl: 系统配置信息-系统参数.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Service
public class ConfigurationsInfoServiceImpl implements ConfigurationsInfoService {

    @Autowired
    private ConfigurationsInfoDao dao;

    @Override
    public ConfigurationsInfoDao getDao() {
        return this.dao;
    }

    @Override
    public DeleteType deleteType() {
        return DeleteType.HARD;
    }

}
