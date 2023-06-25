package io.github.fsixteen.base.modules.environments.init;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.github.fsixteen.base.domain.environments.entities.EnvironmentsInfo;
import io.github.fsixteen.base.domain.environments.entities.VersionsInfo;
import io.github.fsixteen.base.modules.environments.utils.Version;
import io.github.fsixteen.table.init.SQLExecutor;

/**
 * SQL Executor, Init database tables.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Configuration(value = "io.github.fsixteen.base.modules.environments.init.InitDatabaseTables", proxyBeanMethods = false)
public class InitDatabaseTables extends SQLExecutor {

    static {
        Version.logVersion();
    }

    @PostConstruct
    @Transactional
    private void init() throws IOException, SQLException {
        this.initSQL((productName, productVersion) -> readSQLFile(productName, productVersion),
            String.format("%s & %s", VersionsInfo.TABLE_NAME, EnvironmentsInfo.TABLE_NAME));
    }

    private InputStream readSQLFile(String productName, String productVersion) {
        return InitDatabaseTables.class.getResourceAsStream(String.format("%s-v1.0.1.sql", productName.toLowerCase()));
    }

}
