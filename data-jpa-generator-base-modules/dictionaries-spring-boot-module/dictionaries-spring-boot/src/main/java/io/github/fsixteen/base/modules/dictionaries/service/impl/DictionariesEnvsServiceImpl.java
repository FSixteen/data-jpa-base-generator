package io.github.fsixteen.base.modules.dictionaries.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.github.fsixteen.base.modules.dictionaries.dao.DictionariesEnvsDao;
import io.github.fsixteen.base.modules.dictionaries.service.DictionariesEnvsService;
import io.github.fsixteen.data.jpa.generator.constants.DeleteType;

/**
 * ServiceImpl: 系统字典信息-系统字典环境编码.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Lazy
@Service
public class DictionariesEnvsServiceImpl implements DictionariesEnvsService {

    @Autowired
    private DictionariesEnvsDao dao;

    @Override
    public DictionariesEnvsDao getDao() {
        return this.dao;
    }

    @Override
    public DeleteType deleteType() {
        return DeleteType.HARD;
    }

}
