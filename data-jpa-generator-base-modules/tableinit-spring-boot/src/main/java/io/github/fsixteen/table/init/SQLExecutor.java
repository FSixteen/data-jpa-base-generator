package io.github.fsixteen.table.init;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * SQL Executor.<br>
 * 
 * @author FSixteen
 * @since 1.0.1
 */
public abstract class SQLExecutor implements ExitCodeGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(SQLExecutor.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int getExitCode() {
        return 0;
    }

    /**
     * Execute the SQL file.
     * 
     * @param sqlFileInputStream SQL file input stream.
     * @param tableNames         The tables' name
     * @throws IOException  If an I/O error occurs
     * @throws SQLException If a database access error occurs
     */
    protected void initSQL(final BiFunction<String, String, InputStream> sqlFileInputStream, final String tableNames) throws IOException, SQLException {
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        String url = metaData.getURL();
        String productName = metaData.getDatabaseProductName();
        String productVersion = metaData.getDatabaseProductVersion();
        LOG.info("The current database version information {} {}, url {}", productName, productVersion, url);
        LOG.info("JdbcTemplate initializes all the tables required for the database about {}.", tableNames);
        try (BufferedReader sqlFileBufferedReader = new BufferedReader(new InputStreamReader(sqlFileInputStream.apply(productName, productVersion)))) {
            StringBuilder sql = new StringBuilder(1 << 8);
            String currLine = null;
            while ((currLine = sqlFileBufferedReader.readLine()) != null) {
                sql.append(' ').append(currLine.trim());
                if (';' == sql.charAt(sql.length() - 1)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("DDL(Create Table) or DML(Insert/Update/Delete/Select Table) SQL: %s", sql.toString().trim());
                    }
                    jdbcTemplate.execute(sql.toString());
                    sql.setLength(0);
                }
            }
        }
    }

}