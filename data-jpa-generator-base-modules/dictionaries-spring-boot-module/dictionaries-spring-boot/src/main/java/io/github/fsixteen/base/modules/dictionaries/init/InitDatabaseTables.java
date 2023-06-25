package io.github.fsixteen.base.modules.dictionaries.init;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesClassify;
import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesEnvs;
import io.github.fsixteen.base.domain.dictionaries.entities.DictionariesInfo;
import io.github.fsixteen.base.modules.dictionaries.utils.Version;
import io.github.fsixteen.table.init.SQLExecutor;

/**
 * SQL Executor, Init database tables.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
@Configuration(value = "io.github.fsixteen.base.modules.dictionaries.init.InitDatabaseTables", proxyBeanMethods = false)
@DependsOn(value = { "io.github.fsixteen.base.modules.environments.init.InitDatabaseTables" })
public class InitDatabaseTables extends SQLExecutor {

    static {
        Version.logVersion();
    }

    @PostConstruct
    @Transactional
    private void init() throws IOException, SQLException {
        this.initSQL((productName, productVersion) -> readSQLFile(productName, productVersion),
            String.format("%s & %s & %s", DictionariesEnvs.TABLE_NAME, DictionariesClassify.TABLE_NAME, DictionariesInfo.TABLE_NAME));
    }

    private InputStream readSQLFile(String productName, String productVersion) {
        return InitDatabaseTables.class.getResourceAsStream(String.format("%s-v1.0.1.sql", productName.toLowerCase()));
    }

}
