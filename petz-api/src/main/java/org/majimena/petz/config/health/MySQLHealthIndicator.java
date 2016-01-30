package org.majimena.petz.config.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * MySQLのヘルスチェッカ.
 */
public class MySQLHealthIndicator extends AbstractHealthIndicator {

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public MySQLHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        String product = getProduct();
        builder.up().withDetail("database", product);

        try {
            builder.withDetail("hello", jdbcTemplate.queryForObject("SELECT 1", Long.class));
        } catch (Exception ex) {
            builder.down(ex);
        }
    }

    private String getProduct() {
        return jdbcTemplate
                .execute((Connection connection) -> connection.getMetaData().getDatabaseProductName());
    }
}
