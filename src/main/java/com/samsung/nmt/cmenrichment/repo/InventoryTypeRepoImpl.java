package com.samsung.nmt.cmenrichment.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.qualifiers.InventoryTypeQ;

@Service
@InventoryTypeQ
public class InventoryTypeRepoImpl implements MetadataRepo<String>, CacheableRepo {

    @Autowired
    private Cache<String, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String TABLE = " INVENTORY_TYPE ";
    private static final String INSERT = "INSERT INTO " + TABLE + " (TYPE_NAME) VALUES(?)";
    private static final String GET_ID = "SELECT TYPE_ID FROM " + TABLE + " WHERE TYPE_NAME = ?";
    private static final String GET_ALL = "SELECT TYPE_ID, TYPE_NAME FROM " + TABLE;

    @Override
    @Transactional(readOnly = true)
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL, (rs) -> {
            cache.add(rs.getString(2), rs.getInt(1));
        });
    }

    @Override
    public Integer addIfAbsentAndGetId(String typeName) {
        Integer id = cache.get(typeName);
        if (id == null) {
            id = insertOrGetFromDB(typeName);
        }
        return id;
    }

    private synchronized Integer insertOrGetFromDB(String typeName) {
        Integer id = null;
        try {
            id = insertAndGetId(typeName);
            cache.add(typeName, id);
        } catch (DuplicateKeyException e) {
            id = getId(typeName);
        }
        return id;

    }

    @Transactional
    public Integer insertAndGetId(String typeName) {
        return jdbcTemplate.insertAndGetId(INSERT, typeName, (pstmt, typeN) -> {
            pstmt.setString(1, typeN);
        });
    }

    @Transactional(readOnly = true)
    private Integer getId(String typeName) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, typeName);
        cache.add(typeName, id);
        return id;
    }
}
