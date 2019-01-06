package com.samsung.nmt.cmenrichment.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import com.samsung.nmt.cmenrichment.constants.Constants;

public class LongBatchConnectionCallBack<D, R> implements ConnectionCallback<R> {

    public LongBatchConnectionCallBack(String query, List<D> dataBatch, SetParameter<D> setParameter,
            SetBatchLongAI<D, R> setBatchAI, Supplier<R> init) {
        this.query = query;
        this.dataBatch = dataBatch;
        this.setParameter = setParameter;
        this.setBatchAI = setBatchAI;
        this.init = init;
    }

    private int k = 0;
    private PreparedStatement pstmt;
    private R t;
    private int i = 0;
    private List<D> dataBatch;
    private String query;
    private SetParameter<D> setParameter;
    private SetBatchLongAI<D, R> setBatchAI;
    private Supplier<R> init;

    @Override
    public R doInConnection(Connection con) throws SQLException, DataAccessException {

        t = init.get();

        pstmt = con.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS);

        for (; i < dataBatch.size(); i++) {

            D d = dataBatch.get(i);
            setParameter.set(pstmt, d);
            pstmt.addBatch();

            if ((i % Constants.BATCH_SIZE) == 0 && (i != 0)) {
                executeBatch();

            }
        }

        if (k < i) {
            executeBatch();
        }

        pstmt.close();
        return t;

    }

    private void executeBatch() throws SQLException {
        pstmt.executeBatch();
        ResultSet rs = pstmt.getGeneratedKeys();
        while (rs.next()) {
            D d = dataBatch.get(k);
            long aiKey = rs.getLong(1);
            setBatchAI.set(d, t, aiKey);
            k++;
        }

    }

    /* public static void main(String[] args) {
        List<NwElement> list = new ArrayList<>();
    
        SetParameter<NwElement> setParameter = new SetParameter<NwElement>() {
    
            @Override
            public void set(PreparedStatement pstmt, NwElement d) throws SQLException {
    
            }
        };
    
        SetBatchAI<NwElement, Map<String, Integer>> setBatchAI = new SetBatchAI<NwElement, Map<String, Integer>>() {
    
            @Override
            public void set(NwElement d, Map<String, Integer> t, int i) {
                // TODO Auto-generated method stub
    
            }
        };
    
        Consumer<Map<String, Integer>> consumer = new Consumer<Map<String, Integer>>() {
    
            @Override
            public void accept(Map<String, Integer> t) {
                // TODO Auto-generated method stub
    
            }
        };
    }*/
}
