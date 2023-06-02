package io.github.fsixteen.base.modules.configurations.init;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsInfo;
import io.github.fsixteen.base.domain.configurations.entities.ConfigurationsKey;
import io.github.fsixteen.base.modules.configurations.utils.Version;
import io.github.fsixteen.table.init.SQLExecutor;

/**
 * SQL Executor, Init database tables.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
@Component("io.github.fsixteen.base.modules.configurations.init.InitDatabaseTables")
@DependsOn(value = { "io.github.fsixteen.base.modules.environments.init.InitDatabaseTables" })
@DependsOnDatabaseInitialization
public class InitDatabaseTables extends SQLExecutor {

    static {
        Version.logVersion();
    }

    @PostConstruct
    @Transactional
    private void init() throws IOException, SQLException {
        this.initSQL((productName, productVersion) -> readSQLFile(productName, productVersion),
            String.format("%s & %s", ConfigurationsKey.TABLE_NAME, ConfigurationsInfo.TABLE_NAME));
    }

    private InputStream readSQLFile(String productName, String productVersion) {
        return InitDatabaseTables.class.getResourceAsStream(String.format("%s-v1.0.1.sql", productName.toLowerCase()));
    }

}
