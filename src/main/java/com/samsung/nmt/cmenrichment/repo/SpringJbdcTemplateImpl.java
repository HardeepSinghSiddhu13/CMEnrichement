package com.samsung.nmt.cmenrichment.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;

/**
 * @author CrPatil
 *
 */
@Repository
public class SpringJbdcTemplateImpl implements SpringJdbcTemplate {

    @Autowired
    private JdbcOperations jdbcOperations;

    @Override
    public void retriveMultipleRows(String query, RowCallbackHandler rowCallbackHandler) {
        jdbcOperations.query(query, rowCallbackHandler);
    }

    @Override
    public void retriveMultipleRows(String query, Object[] args, RowCallbackHandler rowCallbackHandler) {
        jdbcOperations.query(query, args, rowCallbackHandler);
    }

    @Override
    @Transactional(noRollbackFor = DuplicateKeyException.class, propagation = Propagation.REQUIRED)
    public <D> Integer insertAndGetId(String query, D parameter, final SetParameter<D> setPatameter) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcOperations.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                setPatameter.set(ps, parameter);
                return ps;
            }
        }, holder);

        int key = holder.getKey().intValue();
        return key;
    }

    @Override
    @Transactional(noRollbackFor = DuplicateKeyException.class, propagation = Propagation.REQUIRED)
    public <D> long insertAndGetLongId(String query, D parameter, final SetParameter<D> setPatameter) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcOperations.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                setPatameter.set(ps, parameter);
                return ps;
            }
        }, holder);

        return holder.getKey().longValue();

    }

    @Override
    @Transactional(noRollbackFor = DuplicateKeyException.class, propagation = Propagation.REQUIRED)
    public int insert(String query, PreparedStatementSetter pss) {
        return jdbcOperations.update(query, pss);

    }

    @Override
    public <D> int update(String query, final PreparedStatementSetter preparedStatementSetter) {
        return jdbcOperations.update(query, preparedStatementSetter);
    }

    @Override
    public <T> T queryForObject(String query, Class<T> requiredType, Object... args) {
        try {
            return jdbcOperations.queryForObject(query, requiredType, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public <T> T queryForObject(String query, RowMapper<T> rowMapper, Object... args) {
        return jdbcOperations.queryForObject(query, args, rowMapper);
    }

    @Override
    public <D> int[][] batchUpdate(String query, List<D> batchData,
            ParameterizedPreparedStatementSetter<D> parameterizedPreparedStatementSetter) {
        return jdbcOperations.batchUpdate(query,
                batchData, com.samsung.nmt.cmenrichment.constants.Constants.BATCH_SIZE,
                parameterizedPreparedStatementSetter);

    }

    @Override
    public <T> T execute(ConnectionCallback<T> connectionCallback) {
        return jdbcOperations.execute(connectionCallback);
    }

    @Override
    public <D, R> R execute(BatchConnectionCallBack<D, R> batchConnectionCallBack) {
        return jdbcOperations.execute(batchConnectionCallBack);
    }

    @Override
    public <D, R> R execute(LongBatchConnectionCallBack<D, R> batchConnectionCallBack) {
        return jdbcOperations.execute(batchConnectionCallBack);
    }

    @Override
    public <D> List<Long> batchInsertAndGetLongIds(String query, List<D> dataBatch, SetParameter<D> setParameter) {
        LongBatchConnectionCallBack<D, List<Long>> callBack = new LongBatchConnectionCallBack<>(query, dataBatch,
                setParameter,
                (d, returnList, aiKey) -> {
                    returnList.add(aiKey);
                },
                () -> {
                    return new ArrayList<>(dataBatch.size());
                });
        return execute(callBack);
    }

    @Override
    public <D> List<Integer> batchInsertAndGetIntIds(String query, List<D> dataBatch, SetParameter<D> setParameter) {
        BatchConnectionCallBack<D, List<Integer>> callBack = new BatchConnectionCallBack<>(query, dataBatch,
                setParameter,
                (d, returnList, aiKey) -> {
                    returnList.add(aiKey);
                },
                () -> {
                    return new ArrayList<>(dataBatch.size());
                });
        return execute(callBack);
    }

}
