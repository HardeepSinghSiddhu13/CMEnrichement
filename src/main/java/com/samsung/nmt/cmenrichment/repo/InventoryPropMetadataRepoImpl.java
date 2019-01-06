package com.samsung.nmt.cmenrichment.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.dto.InventoryPropMetadata;
import com.samsung.nmt.cmenrichment.qualifiers.InventoryPropMetadataQ;

@Service
@InventoryPropMetadataQ
public class InventoryPropMetadataRepoImpl implements MetadataRepo<InventoryPropMetadata>, CacheableRepo {

    @Autowired
    private Cache<InventoryPropMetadata, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String TABLE = " INVENTORY_METADATA_PROPERTY ";
    private static final String INSERT = "INSERT INTO " + TABLE
            + " (TYPE_ID, NAME) VALUES (?, ?)";
    private static final String GET_ID = "SELECT PROPERTY_METADATA_ID FROM " + TABLE
            + " WHERE TYPE_ID = ? AND NAME = ?";
    private static final String GET_ALL = "SELECT PROPERTY_METADATA_ID, TYPE_ID, NAME NAME FROM " + TABLE;

    @Override
    @Transactional(readOnly = true)
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL, (rs) -> {
            cache.add(new InventoryPropMetadata(rs.getInt(2), rs.getString(3)), rs.getInt(1));
        });
    }

    @Override
    public Integer addIfAbsentAndGetId(InventoryPropMetadata swPropMetadata) {
        Integer id = cache.get(swPropMetadata);
        if (id == null) {
            id = insertOrGetFromDB(swPropMetadata);
        }
        return id;
    }

    private synchronized Integer insertOrGetFromDB(InventoryPropMetadata swPropMetadata) {
        Integer id = null;
        try {
            id = insertAndGetId(swPropMetadata);
            cache.add(swPropMetadata, id);
        } catch (DuplicateKeyException e) {
            id = getId(swPropMetadata);
        }
        return id;

    }

    @Transactional
    public Integer insertAndGetId(InventoryPropMetadata swPropMetadata) {
        return jdbcTemplate.insertAndGetId(INSERT, swPropMetadata, (pstmt, swPropMetadataType) -> {
            pstmt.setInt(1, swPropMetadataType.getTypeId());
            pstmt.setString(2, swPropMetadataType.getName());
        });
    }

    @Transactional(readOnly = true)
    private Integer getId(InventoryPropMetadata swPropMetadata) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, swPropMetadata.getTypeId(),
                swPropMetadata.getName());
        cache.add(swPropMetadata, id);
        return id;
    }

}
