package io.github.fsixteen.base.modules.configurations.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.github.fsixteen.base.modules.configurations.dao.ConfigurationsKeyDao;
import io.github.fsixteen.base.modules.configurations.service.ConfigurationsKeyService;
import io.github.fsixteen.data.jpa.generator.constants.DeleteType;

/**
 * ServiceImpl: 系统配置信息-系统参数编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Service
public class ConfigurationsKeyServiceImpl implements ConfigurationsKeyService {

    @Autowired
    private ConfigurationsKeyDao dao;

    @Override
    public ConfigurationsKeyDao getDao() {
        return this.dao;
    }

    @Override
    public DeleteType deleteType() {
        return DeleteType.HARD;
    }

}
