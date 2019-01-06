package com.samsung.nmt.cmenrichment.repo;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SetParameter<D> {
    void set(PreparedStatement pstmt, D d) throws SQLException;
}
