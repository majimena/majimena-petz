package org.majimena.petical.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * データソースのコンフィグレーション.
 */
@Slf4j
@Configuration
public class DataSourceConfiguration {

    @Value("${spring.datasource.datasourceClassName:com.mysql.jdbc.jdbc2.optional.MysqlDataSource}")
    private String dataSourceClassName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.connectionTestQuery:select 1}")
    private String connectionTestQuery;
    @Value("${spring.datasource.connectionTimeout:30000}")
    private Long connectionTimeout;
    @Value("${spring.datasource.maximumPoolSize:20}")
    private Integer maximumPoolSize;
    @Value("${spring.datasource.maxLifetime:1800000}")
    private Long maxLifetime;
    @Value("${spring.datasource.minimumIdle:2}")
    private Integer minimumIdle;
    @Value("${spring.datasource.useServerPrepStmts:true}")
    private String useServerPrepStmts;
    @Value("${spring.datasource.cachePrepStmts:true}")
    private String cachePrepStmts;
    @Value("${spring.datasource.prepStmtCacheSize:250}")
    private Integer prepStmtCacheSize;
    @Value("${spring.datasource.prepStmtCacheSqlLimit:2048}")
    private Integer prepStmtCacheSqlLimit;

    @Bean(name = "dataSourceSpied", destroyMethod = "shutdown")
    public DataSource internalDataSource() {
        log.debug("Configuring Datasource");

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSourceClassName);
        config.addDataSourceProperty("url", url);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
        config.setAutoCommit(false);
        config.setConnectionTestQuery(connectionTestQuery);
        config.setConnectionTimeout(connectionTimeout);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMaxLifetime(maxLifetime);
        config.setMinimumIdle(minimumIdle);

        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        if ("com.mysql.jdbc.jdbc2.optional.MysqlDataSource".equals(dataSourceClassName)) {
            config.addDataSourceProperty("useServerPrepStmts", useServerPrepStmts);
            config.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
            config.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        }

        return new HikariDataSource(config);
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        return new DataSourceSpy(internalDataSource());
    }
}
