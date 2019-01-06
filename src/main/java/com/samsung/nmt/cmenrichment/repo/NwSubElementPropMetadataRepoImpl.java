package com.samsung.nmt.cmenrichment.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.dto.NwPropMetadata;
import com.samsung.nmt.cmenrichment.qualifiers.NwSubElementPropMetadataQ;

@Repository
@NwSubElementPropMetadataQ
public class NwSubElementPropMetadataRepoImpl implements MetadataRepo<NwPropMetadata>, CacheableRepo {

    private static final Logger logger = LoggerFactory.getLogger(NwSubElementPropMetadataRepoImpl.class);

    @Autowired
    private Cache<NwPropMetadata, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String TABLE = " NETWORK_SUBELEMENT_PROPERTY_METADATA ";
    private static final String INSERT = "INSERT INTO " + TABLE
            + " (TYPE_ID, NAME) VALUES (?, ?)";
    private static final String GET_ID = "SELECT PROPERTY_METADATA_ID FROM " + TABLE
            + " WHERE TYPE_ID = ? AND NAME = ?";
    private static final String GET_ALL = "SELECT PROPERTY_METADATA_ID, TYPE_ID, NAME  FROM " + TABLE;

    @Override
    @Transactional(readOnly = true)
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL, (rs) -> {
            cache.add(new NwPropMetadata(rs.getInt(2), rs.getString(3)), rs.getInt(1));
        });

        logger.info("Nw Sub Element Prop Metadata cache : " + cache);

    }

    @Override
    public Integer addIfAbsentAndGetId(NwPropMetadata nwPropMetadata) {
        Integer id = cache.get(nwPropMetadata);
        if (id == null) {
            id = insertOrGetFromDB(nwPropMetadata);
        }
        return id;
    }

    private Integer insertOrGetFromDB(NwPropMetadata nwPropMetadata) {
        Integer id = null;
        try {
            id = insertAndGetId(nwPropMetadata);
            cache.add(nwPropMetadata, id);
        } catch (DuplicateKeyException e) {
            id = getId(nwPropMetadata);
        }
        return id;

    }

    @Transactional(noRollbackFor = DuplicateKeyException.class)
    public Integer insertAndGetId(NwPropMetadata nwPropMetadata) {
        return jdbcTemplate.insertAndGetId(INSERT, nwPropMetadata, (pstmt, nwPropMetadataType) -> {
            pstmt.setInt(1, nwPropMetadataType.getTypeId());
            pstmt.setString(2, nwPropMetadataType.getName());
        });
    }

    @Transactional(readOnly = true)
    private Integer getId(NwPropMetadata nwPropMetadata) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, nwPropMetadata.getTypeId(),
                nwPropMetadata.getName());
        cache.add(nwPropMetadata, id);
        return id;
    }

}
