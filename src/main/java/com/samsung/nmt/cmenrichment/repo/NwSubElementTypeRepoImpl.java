package com.samsung.nmt.cmenrichment.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.cache.Cache;

@Repository
public class NwSubElementTypeRepoImpl implements NwSubElementTypeRepo, CacheableRepo {

    private static final Logger logger = LoggerFactory.getLogger(NwSubElementTypeRepoImpl.class);

    @Autowired
    private Cache<String, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String TABLE = " NETWORK_SUBELEMENT_TYPE ";
    private static final String INSERT = "INSERT INTO " + TABLE + " (TYPE_NAME) VALUES(?)";
    private static final String GET_ID = "SELECT TYPE_ID FROM " + TABLE + " WHERE TYPE_NAME = ?";
    private static final String GET_ALL = "SELECT TYPE_ID, TYPE_NAME FROM " + TABLE;
    /*private static final String INSERT_MAPPING = "INSERT INTO "
            + " NETWORK_TYPE_SUBELEMENT_TYPE_MAPPING (NETWORK_TYPE_ID, SUBELEMENT_TYPE_ID) VALUES (?,?)";*/

    @Override
    @Transactional(readOnly = true)
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL, (rs) -> {
            cache.add(rs.getString(2), rs.getInt(1));
        });

        logger.info("Nw Sub Element Type cache : " + cache);
    }

    @Override
    public Integer addIfAbsentAndGetId(String typeName, Integer nwelementTypeId) {
        Integer id = cache.get(typeName);
        if (id == null) {
            id = insertOrGetFromDB(typeName, nwelementTypeId);

        }
        return id;
    }

    private synchronized Integer insertOrGetFromDB(String typeName, Integer nwelementTypeId) {
        Integer id = null;
        try {
            id = insertAndGetId(typeName);
            //insertMapping(nwelementTypeId, id);
            cache.add(typeName, id);
        } catch (DuplicateKeyException e) {
            id = getId(typeName);
        }
        return id;

    }

    /*@Transactional(noRollbackFor = DuplicateKeyException.class)
    private Integer insertMapping(Integer nwelementTypeId, Integer nwSubElementTypeId) {
        return jdbcTemplate.insert(INSERT_MAPPING,
                (pstmt) -> {
                    pstmt.setInt(1, nwelementTypeId);
                    pstmt.setInt(2, nwSubElementTypeId);
                });
    }*/

    @Transactional(noRollbackFor = DuplicateKeyException.class)
    private Integer insertAndGetId(String typeName) {
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
