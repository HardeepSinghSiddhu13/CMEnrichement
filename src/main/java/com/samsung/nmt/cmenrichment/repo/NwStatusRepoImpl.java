package com.samsung.nmt.cmenrichment.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.qualifiers.NwStatusQ;

@Repository
@NwStatusQ
public class NwStatusRepoImpl implements StaticMetadataRepo<String>, CacheableRepo {

    private static final Logger logger = LoggerFactory.getLogger(NwEMSRepoImpl.class);

    @Autowired
    private Cache<String, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String TABLE = " NETWORK_STATUS ";
    private static final String GET_ID = "SELECT ID FROM " + TABLE + " WHERE DESCRIPTION = ?";
    private static final String GET_ALL = "SELECT ID, DESCRIPTION FROM " + TABLE;

    @Override
    @Transactional(readOnly = true)
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL, (rs) -> {
            cache.add(rs.getString(2), rs.getInt(1));
        });

        logger.info("Nw Status cache : " + cache);
    }

    @Override
    public Integer getId(String description) {
        Integer id = cache.get(description);
        if (id == null) {
            id = getIdFromDB(description);
        }
        return id;
    }

    @Transactional(readOnly = true)
    private Integer getIdFromDB(String description) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, description);
        cache.add(description, id);
        return id;
    }

}
