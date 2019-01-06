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
import com.samsung.nmt.cmenrichment.dto.HwElement;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.qualifiers.HwElementQ;
import com.samsung.nmt.cmenrichment.utils.JsonParser;

@Component
@HwElementQ
public class HwElementRepoImpl implements MetadataRepo<String> {

    private static final String TABLE = " INVENTORY_HARDWARE ";

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    @Autowired
    private Cache<String, Integer> cache;

    private static final String INSERT = "INSERT INTO " + TABLE
            + " (ELEMENT_ID, CIRCLE_ID, UNIT_TYPE, UNIT_ID,UNIT_SIDE,PROPERTIES, CREATEDTIMESTAMP, MODIFIEDTIMESTAMP) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHwElementsInBatch(List<HwElement> hwElements) {
        return jdbcTemplate.batchUpdate(INSERT, hwElements, (pstmt, hwElementType) -> {
            pstmt.setInt(1, hwElementType.getElementId());
            pstmt.setInt(2, hwElementType.getCircleId());
            pstmt.setString(3, hwElementType.getUnitType());
            pstmt.setInt(4, hwElementType.getUnitId());
            pstmt.setString(5, hwElementType.getUnitSide());
            pstmt.setString(6, hwElementType.getProperties().toString());
            pstmt.setTimestamp(7, new Timestamp(hwElementType.getCreatedTimeStamp().getTimeInMillis()));
            pstmt.setTimestamp(8, new Timestamp(hwElementType.getModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String UPDATE_ALL_EXCEPT_NAME_BY_ID = "UPDATE " + TABLE
            + " SET PROPERTIES = ?,  MODIFIEDTIMESTAMP = ? WHERE ELEMENT_ID = ? AND UNIT_TYPE = ? AND UNIT_ID = ?";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] updateAllInBatch(List<HwElement> hwElements) {
        return jdbcTemplate.batchUpdate(UPDATE_ALL_EXCEPT_NAME_BY_ID, hwElements, (pstmt, hwElementType) -> {
            pstmt.setString(1, hwElementType.getProperties().toString());
            pstmt.setTimestamp(2, new Timestamp(hwElementType.getModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(3, hwElementType.getElementId());
            pstmt.setString(4, hwElementType.getUnitType());
            pstmt.setInt(5, hwElementType.getUnitId());
        });
    }

    private static final String GET_BY_ELEMENT_NAME_P_1 = "SELECT HWR_ID,CIRCLE_ID,UNIT_TYPE, UNIT_ID,UNIT_SIDE, PROPERTIES, CREATEDTIMESTAMP, MODIFIEDTIMESTAMP,ELEMENT_ID FROM "
            + TABLE + "  WHERE ELEMENT_ID IN ( ";
    private static final String GET_BY_ELEMENT_NAME_P_2 = " ) AND UNIT_TYPE IN ( ";
    private static final String GET_BY_ELEMENT_NAME_P_3 = " ) AND UNIT_ID IN ( ";
    private static final String GET_BY_ELEMENT_NAME_P_4 = " )";

    public Map<String, HwElement> getAllData(String heNames, String unitTypes, String unitIds) {
        StringBuilder queryBuilder = new StringBuilder(GET_BY_ELEMENT_NAME_P_1);
        queryBuilder.append(heNames);
        queryBuilder.append(GET_BY_ELEMENT_NAME_P_2);
        queryBuilder.append(unitTypes);
        queryBuilder.append(GET_BY_ELEMENT_NAME_P_3);
        queryBuilder.append(unitIds);
        queryBuilder.append(GET_BY_ELEMENT_NAME_P_4);
        Map<String, HwElement> map = new HashMap<>();
        jdbcTemplate.retriveMultipleRows(queryBuilder.toString(), (rs) -> {
            HwElement hwElement = new HwElement();
            hwElement.setHwrId(rs.getInt(1));
            hwElement.setCircleId(rs.getInt(2));

            hwElement.setUnitType(rs.getString(3));
            hwElement.setUnitId(rs.getInt(4));
            hwElement.setUnitSide(rs.getString(5));

            ObjectNode ObjectNode = JsonParser.fromJsonStr(rs.getString(6));
            hwElement.setProperties(ObjectNode);

            Calendar createdTimeStamp = Calendar.getInstance();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(7).getTime());
            hwElement.setCreatedTimeStamp(createdTimeStamp);

            Calendar lastModifiedTimeStamp = Calendar.getInstance();
            lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(8).getTime());
            hwElement.setModifiedTimeStamp(lastModifiedTimeStamp);
            hwElement.setElementId(rs.getInt(9));

            map.put(rs.getInt(9) + "|" + hwElement.getUnitType() + "|" + hwElement.getUnitId(), hwElement);
        });

        return map;
    }

    private static final String UPDATE_PROPERTIES = "UPDATE " + TABLE
            + " SET PROPERTIES = ? , MODIFIEDTIMESTAMP = ? WHERE ELEMENT_ID = ? AND UNIT_TYPE = ? AND UNIT_ID = ?";

    @Transactional(propagation = Propagation.REQUIRED)
    public int updateProperties(HwElement hwElement) {
        return jdbcTemplate.update(UPDATE_PROPERTIES, (pstmt) -> {
            pstmt.setString(1, hwElement.getProperties().toString());
            pstmt.setTimestamp(2, new Timestamp(hwElement.getModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(3, hwElement.getElementId());
            pstmt.setString(4, hwElement.getUnitType());
            pstmt.setInt(5, hwElement.getUnitId());
        });
    }

    private static final String INSERT_HISTORY = "INSERT INTO INVENTORY_HW_HISTORY "
            + " (HWR_ID,ELEMENT_ID,UNIT_TYPE,UNIT_ID,UNIT_SIDE,PROPERTIES,MODIFIEDTIMESTAMP) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?) ";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHistoryInBatch(List<HwElement> hwElements) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY, hwElements, (pstmt, hwElementType) -> {
            pstmt.setInt(1, hwElementType.getHwrId());
            pstmt.setInt(2, hwElementType.getElementId());

            pstmt.setString(3, hwElementType.getUnitType());
            pstmt.setInt(4, hwElementType.getUnitId());
            pstmt.setString(5, hwElementType.getUnitSide());

            pstmt.setString(6, hwElementType.getProperties().toString());
            pstmt.setTimestamp(7, new Timestamp(hwElementType.getModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String DELETE = "DELETE FROM " + TABLE
            + " WHERE ELEMENT_ID = ? AND UNIT_TYPE = ? AND UNIT_ID = ?";

    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] deleteHwElementsInBatch(List<HwElement> hwElements) {
        return jdbcTemplate.batchUpdate(DELETE, hwElements, (pstmt, hwElementType) -> {
            pstmt.setInt(1, hwElementType.getElementId());
            pstmt.setString(2, hwElementType.getUnitType());
            pstmt.setInt(3, hwElementType.getUnitId());
        });
    }

    public List<Long> addHistoryAndGetIdsInBatch(List<HistoryData<HwElement>> hwElementHistory) {
        return jdbcTemplate.batchInsertAndGetLongIds(INSERT_HISTORY, hwElementHistory, (pstmt, history) -> {
            HwElement hwElement = history.getHistoryData();
            pstmt.setInt(1, hwElement.getHwrId());
            pstmt.setInt(2, hwElement.getElementId());
            pstmt.setString(3, hwElement.getUnitType());
            pstmt.setInt(4, hwElement.getUnitId());
            pstmt.setString(5, hwElement.getUnitSide());
            pstmt.setString(6, hwElement.getProperties().toString());
            pstmt.setTimestamp(7, new Timestamp(hwElement.getModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String INSERT_HISTORY_MATRIC = "INSERT INTO INVENTORY_HW_HIST_METRIC "
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
