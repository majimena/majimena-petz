package io.petical.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcParameterUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import rx.Observable;
import rx.Observer;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by todoken on 2016/05/22.
 */
public class ObservableItemWriter<E, T extends Observable<E>> implements ItemWriter<T>, InitializingBean {

    protected static final Logger logger = LoggerFactory.getLogger(ObservableItemWriter.class);

    private NamedParameterJdbcOperations namedParameterJdbcTemplate;

    private ItemPreparedStatementSetter<E> itemPreparedStatementSetter;

    private ItemSqlParameterSourceProvider<E> itemSqlParameterSourceProvider;

    private String sql;

    private boolean assertUpdates = true;

    private int parameterCount;

    private boolean usingNamedParameters;

    public void setAssertUpdates(boolean assertUpdates) {
        this.assertUpdates = assertUpdates;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setItemPreparedStatementSetter(ItemPreparedStatementSetter<E> preparedStatementSetter) {
        this.itemPreparedStatementSetter = preparedStatementSetter;
    }

    public void setItemSqlParameterSourceProvider(ItemSqlParameterSourceProvider<E> itemSqlParameterSourceProvider) {
        this.itemSqlParameterSourceProvider = itemSqlParameterSourceProvider;
    }

    public void setDataSource(DataSource dataSource) {
        if (this.namedParameterJdbcTemplate == null) {
            this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        }
    }

    public void setJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.namedParameterJdbcTemplate, "A DataSource or a NamedParameterJdbcTemplate is required.");
        Assert.notNull(this.sql, "An SQL statement is required.");

        ArrayList namedParameters = new ArrayList();
        this.parameterCount = JdbcParameterUtils.countParameterPlaceholders(this.sql, namedParameters);
        if (namedParameters.size() > 0) {
            if (this.parameterCount != namedParameters.size()) {
                throw new InvalidDataAccessApiUsageException("You can\'t use both named parameters and classic \"?\" placeholders: " + this.sql);
            }
            this.usingNamedParameters = true;
        }

        if (!this.usingNamedParameters) {
            Assert.notNull(this.itemPreparedStatementSetter, "Using SQL statement with \'?\' placeholders requires an ItemPreparedStatementSetter");
        }
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing batch with " + items.size() + " items.");
        }

        items.stream()
                .forEach(o -> o.subscribe(observer()));
    }

    protected Observer<E> observer() {
        return new Observer<E>() {
            @Override
            public void onNext(E item) {
                namedParameterJdbcTemplate.getJdbcOperations().execute(sql, (CallableStatementCallback<int[]>) ps -> {
                    ObservableItemWriter.this.itemPreparedStatementSetter.setValues(item, ps);
                    ps.addBatch();
                    return ps.executeBatch();
                });
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
            }
        };
    }
}
