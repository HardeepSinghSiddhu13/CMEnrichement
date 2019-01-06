package com.samsung.nmt.cmenrichment.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.qualifiers.NwEMSQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwStatusQ;

@Repository
@NwEMSQ
public class NwEMSRepoImpl implements NwEMSRepo<String>, CacheableRepo {

    private static final Logger logger = LoggerFactory.getLogger(NwEMSRepoImpl.class);

    @Autowired
    private Cache<String, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private Integer activeStausId;

    @Autowired
    @NwStatusQ
    private StaticMetadataRepo<String> nwStatusRepo;

    @Autowired
    private AppProperties appProperties;

    private static final String TABLE = " NETWORK_EMS ";
    /*private static final String INSERT = "insert into " + TABLE
            + " (name, status_id, ip_address, description) values (?, ?, ?, ?)";*/
    /*private static final String INSERT = "insert into " + TABLE
            + " (name) values (?)";*/
    private static final String UPDATE_ACTIVE = "UPDATE " + TABLE
            + " SET STATUS_ID = ? WHERE EMS_ID = ? ";
    private static final String GET_ID = "SELECT EMS_ID FROM " + TABLE + " WHERE NAME = ?";
    private static final String GET_ALL = "SELECT EMS_ID, NAME FROM " + TABLE;

    @Override
    @Transactional(readOnly = true)
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL, (rs) -> {
            cache.add(rs.getString(2), rs.getInt(1));
        });

        activeStausId = nwStatusRepo.getId(appProperties.getStatusActiveValue());

        logger.info("Nw EMS cache : " + cache);
        logger.info("id of active status : " + activeStausId);

    }

    @Override
    public Integer getId(String nwEMSName) {
        Integer id = cache.get(nwEMSName);
        if (id == null) {
            id = getIdFromDB(nwEMSName);
        }
        return id;
    }

    @Override
    @Transactional
    public int updateStatusToActive(Integer emsId) {
        return jdbcTemplate.update(UPDATE_ACTIVE, (pstmt) -> {
            pstmt.setInt(1, activeStausId);
            pstmt.setInt(2, emsId);
        });
    }

    @Transactional(readOnly = true)
    private Integer getIdFromDB(String emsName) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, emsName);
        cache.add(emsName, id);
        return id;
    }
}
