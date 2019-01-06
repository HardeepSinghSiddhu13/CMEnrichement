package com.samsung.nmt.cmenrichment.repo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.SwElement;
import com.samsung.nmt.cmenrichment.qualifiers.SwElementQ;
import com.samsung.nmt.cmenrichment.utils.JsonParser;

@Component
@SwElementQ
public class SwElementRepoImpl implements MetadataRepo<String> {

    private static final String TABLE = " inventory_software ";

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    @Autowired
    private Cache<String, Integer> cache;

    private static final String INSERT = "INSERT INTO " + TABLE
            + " (ELEMENT_ID, CIRCLE_ID, PROPERTIES, CREATEDTIMESTAMP, MODIFIEDTIMESTAMP) "
            + " VALUES(?, ?, ?, ?, ?) ";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addSwElementsInBatch(List<SwElement> swElements) {
        return jdbcTemplate.batchUpdate(INSERT, swElements, (pstmt, swElementType) -> {
            pstmt.setInt(1, swElementType.getElementId());
            pstmt.setInt(2, swElementType.getCircleId());
            pstmt.setString(3, swElementType.getProperties().toString());
            pstmt.setTimestamp(4, new Timestamp(swElementType.getCreatedTimeStamp().getTimeInMillis()));
            pstmt.setTimestamp(5, new Timestamp(swElementType.getModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String UPDATE_ALL_EXCEPT_NAME_BY_ID = "UPDATE " + TABLE
            + " SET PROPERTIES = ?,  MODIFIEDTIMESTAMP = ? WHERE ELEMENT_ID = ?";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] updateAllInBatch(List<SwElement> swElements) {
        return jdbcTemplate.batchUpdate(UPDATE_ALL_EXCEPT_NAME_BY_ID, swElements, (pstmt, swElementType) -> {
            pstmt.setString(1, swElementType.getProperties().toString());
            pstmt.setTimestamp(2, new Timestamp(swElementType.getModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(3, swElementType.getElementId());
        });
    }

    private static final String GET_BY_ELEMENT_NAME_P_1 = "SELECT SWR_ID, CIRCLE_ID, PROPERTIES, CREATEDTIMESTAMP,  MODIFIEDTIMESTAMP , ELEMENT_ID FROM "
            + TABLE + "  WHERE ELEMENT_ID IN ( ";
    private static final String GET_BY_ELEMENT_NAME_P_2 = " )";

    public Map<Integer, SwElement> getAllByElementID(String elementIds) {
        StringBuilder queryBuilder = new StringBuilder(GET_BY_ELEMENT_NAME_P_1);
        queryBuilder.append(elementIds);
        queryBuilder.append(GET_BY_ELEMENT_NAME_P_2);

        Map<Integer, SwElement> map = new HashMap<>();
        jdbcTemplate.retriveMultipleRows(queryBuilder.toString(), (rs) -> {
            SwElement swElement = new SwElement();
            swElement.setSwrId(rs.getInt(1));
            swElement.setCircleId(rs.getInt(2));

            ObjectNode ObjectNode = JsonParser.fromJsonStr(rs.getString(3));
            swElement.setProperties(ObjectNode);

            Calendar createdTimeStamp = Calendar.getInstance();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(4).getTime());
            swElement.setCreatedTimeStamp(createdTimeStamp);

            Calendar lastModifiedTimeStamp = Calendar.getInstance();
            lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(5).getTime());
            swElement.setModifiedTimeStamp(lastModifiedTimeStamp);
            swElement.setElementId(rs.getInt(6));
            map.put(rs.getInt(6), swElement);
        });

        return map;
    }

    private static final String UPDATE_PROPERTIES = "UPDATE " + TABLE
            + " SET PROPERTIES = ? , MODIFIEDTIMESTAMP = ? WHERE ELEMENT_ID = ?";

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateProperties(SwElement swElement) {
        return jdbcTemplate.update(UPDATE_PROPERTIES, (pstmt) -> {
            pstmt.setString(1, swElement.getProperties().toString());
            pstmt.setTimestamp(2, new Timestamp(swElement.getModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(3, swElement.getElementId());
        });
    }

    private static final String INSERT_HISTORY = "INSERT INTO INVENTORY_SW_HISTORY "
            + " (SWR_ID,ELEMENT_ID, PROPERTIES,  MODIFIEDTIMESTAMP) "
            + " VALUES (?, ?, ?, ?) ";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHistoryInBatch(List<SwElement> swElements) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY, swElements, (pstmt, swElementType) -> {
            pstmt.setInt(1, swElementType.getSwrId());
            pstmt.setInt(2, swElementType.getElementId());
            pstmt.setString(3, swElementType.getProperties().toString());
            pstmt.setTimestamp(4, new Timestamp(swElementType.getModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String DELETE = "DELETE FROM " + TABLE + " WHERE ELEMENT_ID = ? ";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] deleteSwElementsInBatch(List<SwElement> swElements) {
        return jdbcTemplate.batchUpdate(DELETE, swElements, (pstmt, swElementType) -> {
            pstmt.setInt(1, swElementType.getElementId());
        });
    }

    public List<Long> addHistoryAndGetIdsInBatch(List<HistoryData<SwElement>> swElementHistory) {
        return jdbcTemplate.batchInsertAndGetLongIds(INSERT_HISTORY, swElementHistory, (pstmt, history) -> {
            SwElement swElement = history.getHistoryData();
            pstmt.setInt(1, swElement.getSwrId());
            pstmt.setInt(2, swElement.getElementId());
            pstmt.setString(3, swElement.getProperties().toString());
            pstmt.setTimestamp(4, new Timestamp(swElement.getModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String INSERT_HISTORY_MATRIC = "INSERT INTO INVENTORY_SW_HIST_METRIC "
            + " (HISTORY_ID, PROPERTY_METADATA_ID)"
            + " VALUES(?, ?)";

    public int[][] addHistoryMatricInBatch(List<MatricHistory> matricHistories) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY_MATRIC, matricHistories, (pstmt, matricHistoryType) -> {
            pstmt.setLong(1, matricHistoryType.getHistoryId());
            pstmt.setInt(2, matricHistoryType.getPropMetadataId());
        });
    }

    @Override
    public Integer addIfAbsentAndGetId(String elementName) {
        Integer elementId = cache.get(elementName);
        if (elementId == null) {
            elementId = insertOrGetFromDB(elementName);
        }
        return elementId;
    }

    private static final String INSERT_ELE_NAME = "INSERT INTO NETWORK_ELEMENT "
            + " (NAME, CREATEDTIMESTAMP)"
            + " VALUES(?, ?)";

    @Transactional(propagation = Propagation.REQUIRED)
    private synchronized Integer insertOrGetFromDB(String elementName) {
        Integer id = null;
        try {
            id = jdbcTemplate.insertAndGetId(INSERT_ELE_NAME, elementName, (pstmt, elementNameType) -> {
                pstmt.setString(1, elementNameType);
                pstmt.setTimestamp(2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            });
        } catch (DuplicateKeyException e) {
            id = getId(elementName);
        }
        return id;

    }

    private static final String GET_ID = "SELECT ELEMENT_ID FROM NETWORK_ELEMENT WHERE NAME = ?";

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer getId(String neName) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, neName);
        cache.add(neName, id);
        return id;
    }
}
