package com.samsung.nmt.cmenrichment.repo;

import java.util.List;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

public interface SpringJdbcTemplate {

    void retriveMultipleRows(String query, RowCallbackHandler rowCallbackHandler);

    void retriveMultipleRows(String query, Object[] args, RowCallbackHandler rowCallbackHandler);

    <D> Integer insertAndGetId(String query, D parameter, final SetParameter<D> setPatameter);

    <T> T queryForObject(String query, Class<T> requiredType, Object... args);

    <D> int update(String query, final PreparedStatementSetter preparedStatementSetter);

    <T> T queryForObject(String query, RowMapper<T> rowMapper, Object... args);

    <D> long insertAndGetLongId(String query, D parameter, final SetParameter<D> setPatameter);

    int insert(String query, PreparedStatementSetter pss);

    <D> int[][] batchUpdate(String query, List<D> batchData,
            ParameterizedPreparedStatementSetter<D> parameterizedPreparedStatementSetter);

    <T> T execute(ConnectionCallback<T> connectionCallback);

    <D, R> R execute(BatchConnectionCallBack<D, R> batchConnectionCallBack);

    <D, R> R execute(LongBatchConnectionCallBack<D, R> batchConnectionCallBack);

    <D> List<Long> batchInsertAndGetLongIds(String query, List<D> dataBatch, SetParameter<D> setParameter);

    <D> List<Integer> batchInsertAndGetIntIds(String query, List<D> dataBatch, SetParameter<D> setParameter);

    /* <D> InsertAndGetId<D> insertAndGetId(D objectToBeInserted);
    
     RetrieveMultipleRow retrieveMultipleRow();
    
     <T> RetrieveSingleField<T> retrieveSingleField(Class<T> c);*/
}
