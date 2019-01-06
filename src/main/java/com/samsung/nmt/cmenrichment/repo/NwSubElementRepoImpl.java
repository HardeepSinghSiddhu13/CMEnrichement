package com.samsung.nmt.cmenrichment.repo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.NwSubElKey;
import com.samsung.nmt.cmenrichment.dto.NwSubElement;
import com.samsung.nmt.cmenrichment.utils.DateTimeUtil;
import com.samsung.nmt.cmenrichment.utils.JsonParser;

@Repository
public class NwSubElementRepoImpl implements NwSubElementRepo {

    private static final Logger logger = LoggerFactory.getLogger(NwSubElementRepoImpl.class);

    /*@Autowired
    private NwSubEleKeyGenerator nwSubEleKeyGenerator;*/

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String TABLE = " NETWORK_SUBELEMENT ";

    //insert methods------------------------------------------------
    private static final String INSERT = "INSERT INTO " + TABLE
            + " (ELEMENT_ID, NAME, TYPE_ID,  PROPERTIES, CREATEDTIMESTAMP, LASTMODIFIEDTIMESTAMP, LOCATION_ID) "
            + " VALUES (?, ?, ?,  ?, ?, ?, ?) ";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addNwElements(List<NwSubElement> nwSubElements) {
        return jdbcTemplate.batchUpdate(INSERT, nwSubElements, (pstmt, nwSubElementType) -> {
            pstmt.setInt(1, nwSubElementType.getElementId());
            pstmt.setString(2, nwSubElementType.getName());
            pstmt.setInt(3, nwSubElementType.getTypeId());
            pstmt.setString(4, JsonParser.fromJsonNode(nwSubElementType.getProperties()));
            pstmt.setTimestamp(5, new Timestamp(nwSubElementType.getCreatedTimeStamp().getTimeInMillis()));
            pstmt.setTimestamp(6, new Timestamp(nwSubElementType.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(7, nwSubElementType.getLocationId());
        });
    }

    //update methods------------------------------------------------
    private static final String UPDATE_PROPERTIES = "UPDATE " + TABLE
            + " SET PROPERTIES =? , LASTMODIFIEDTIMESTAMP = ? "
            + " WHERE SUBELEMENT_ID = ?";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] updateNwElementProperties(List<NwSubElement> nwSubElements) {
        return jdbcTemplate.batchUpdate(UPDATE_PROPERTIES, nwSubElements, (pstmt, nwSubElementType) -> {
            pstmt.setString(1, JsonParser.fromJsonNode(nwSubElementType.getProperties()));
            pstmt.setTimestamp(2, new Timestamp(nwSubElementType.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setLong(3, nwSubElementType.getSubElementId());
        });
    }

    //delete methods------------------------------------------------
    private static final String DELETE = "DELETE FROM " + TABLE + " WHERE SUBELEMENT_ID = ? ";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] deleteNwSubElements(List<NwSubElement> nwSubElements) {
        return jdbcTemplate.batchUpdate(DELETE, nwSubElements, (pstmt, nwSubElementType) -> {
            pstmt.setLong(1, nwSubElementType.getSubElementId());
        });
    }

    //select methods------------------------------------------------
    /* private static final String GET_BY_TYPE_ELEMENT_P_1 = " select element_id, name, type_id,  properties,"
            + " createdtimestamp, lastmodifiedtimestamp, subelement_id from "
            + TABLE + " where element_id in (";
    
    private static final String GET_BY_TYPE_ELEMENT_P_2 = " ) and type_id in ( ";
    
    private static final String GET_BY_TYPE_ELEMENT_P_3 = " )";
    
    @Override
    public Map<NwSubElKey, NwSubElement> getBytypeIdAndElementId(String elementIds,
            String typeIds) {
    
        StringBuilder builder = new StringBuilder(GET_BY_TYPE_ELEMENT_P_1);
        builder.append(elementIds);
        builder.append(GET_BY_TYPE_ELEMENT_P_2);
        builder.append(typeIds);
        builder.append(GET_BY_TYPE_ELEMENT_P_3);
    
        Map<NwSubElKey, NwSubElement> nwSubElUniqueIdsMap = new HashMap<>();
    
        jdbcTemplate.retriveMultipleRows(builder.toString(), (rs) -> {
            NwSubElement nwSubElement = new NwSubElement();
            nwSubElement.setElementId(rs.getInt(1));
            nwSubElement.setName(rs.getString(2));
            nwSubElement.setTypeId(rs.getInt(3));
            ObjectNode proerties = JsonParser.fromJsonStr(rs.getString(4));
            nwSubElement.setProperties(proerties);
    
            Calendar createdTimeStamp = CalendarUtil.getInstance().currTime();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(5).getTime());
            nwSubElement.setCreatedTimeStamp(createdTimeStamp);
    
            Calendar lastModifiedTimeStamp = CalendarUtil.getInstance().currTime();
            lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(6).getTime());
            nwSubElement.setLastModifiedTimeStamp(lastModifiedTimeStamp);
            nwSubElement.setSubElementId(rs.getLong(7));
    
            NwSubElKey nwSubElKey = nwSubEleKeyGenerator.generateKey(nwSubElement.getElementId(),
                    nwSubElement.getTypeId(),
                    nwSubElement.getProperties());
            nwSubElUniqueIdsMap.put(nwSubElKey, nwSubElement);
    
        });
        return nwSubElUniqueIdsMap;
    }*/

    //select methods------------------------------------------------
    private static final String GET_BY_TYPE_ELEMENT_NAME_P_1 = " SELECT ELEMENT_ID, NAME, TYPE_ID,  PROPERTIES,"
            + " CREATEDTIMESTAMP, LASTMODIFIEDTIMESTAMP, SUBELEMENT_ID FROM "
            + TABLE + " WHERE LOCATION_ID IN (";

    private static final String GET_BY_TYPE_ELEMENT_NAME_P_2 = " ) AND ELEMENT_ID IN ( ";

    //private static final String GET_BY_TYPE_ELEMENT_NAME_P_3 = " )";

    private static final String GET_BY_TYPE_ELEMENT_NAME_P_3 = " ) AND TYPE_ID IN (";

    private static final String GET_BY_TYPE_ELEMENT_NAME_P_4 = " ) AND NAME IN (";

    private static final String GET_BY_TYPE_ELEMENT_NAME_P_5 = " )";

    @Override
    public Map<NwSubElKey, NwSubElement> getBytypeIdAndElementIdAndName(String elementIds,
            String typeIds, String identNames, String locationIds) {

        StringBuilder builder = new StringBuilder(GET_BY_TYPE_ELEMENT_NAME_P_1);
        builder.append(locationIds);
        builder.append(GET_BY_TYPE_ELEMENT_NAME_P_2);
        builder.append(elementIds);
        builder.append(GET_BY_TYPE_ELEMENT_NAME_P_3);
        builder.append(typeIds);
        builder.append(GET_BY_TYPE_ELEMENT_NAME_P_4);
        builder.append(identNames);
        builder.append(GET_BY_TYPE_ELEMENT_NAME_P_5);

        if (logger.isDebugEnabled()) {
            logger.debug("query : " + builder.toString());
        }

        Map<NwSubElKey, NwSubElement> nwSubElUniqueIdsMap = new HashMap<>();

        jdbcTemplate.retriveMultipleRows(builder.toString(), (rs) -> {
            NwSubElement nwSubElement = new NwSubElement();
            nwSubElement.setElementId(rs.getInt(1));
            nwSubElement.setName(rs.getString(2));
            nwSubElement.setTypeId(rs.getInt(3));
            ObjectNode proerties = JsonParser.fromJsonStr(rs.getString(4));
            nwSubElement.setProperties(proerties);

            Calendar createdTimeStamp = DateTimeUtil.getInstance().currTime();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(5).getTime());
            nwSubElement.setCreatedTimeStamp(createdTimeStamp);

            Calendar lastModifiedTimeStamp = DateTimeUtil.getInstance().currTime();
            lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(6).getTime());
            nwSubElement.setLastModifiedTimeStamp(lastModifiedTimeStamp);
            nwSubElement.setSubElementId(rs.getLong(7));

            nwSubElUniqueIdsMap.put(
                    NwSubElKey.createKey(nwSubElement.getElementId(), nwSubElement.getTypeId(), nwSubElement.getName()),
                    nwSubElement);

        });
        return nwSubElUniqueIdsMap;
    }

    //history methods------------------------------------------------
    private static final String INSERT_HISTORY = "INSERT INTO NETWORK_SUBELEMENT_HISTORY "
            + " (SUBELEMENT_ID, PROPERTIES,  MODIFIEDTIMESTAMP) "
            + " VALUES (?, ?, ?) ";

    @Override
    public List<Long> addHistoryAndGetIds(List<HistoryData<NwSubElement>> nwSubElementHistory) {
        return jdbcTemplate.batchInsertAndGetLongIds(INSERT_HISTORY, nwSubElementHistory, (pstmt, history) -> {
            NwSubElement nwSubElement = history.getHistoryData();
            pstmt.setLong(1, nwSubElement.getSubElementId());
            pstmt.setString(2, JsonParser.fromJsonNode(nwSubElement.getProperties()));
            pstmt.setTimestamp(3, new Timestamp(nwSubElement.getLastModifiedTimeStamp().getTimeInMillis()));
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHistory(List<NwSubElement> nwSubElements) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY, nwSubElements, (pstmt, nwSubElementType) -> {
            pstmt.setLong(1, nwSubElementType.getSubElementId());
            pstmt.setString(2, JsonParser.fromJsonNode(nwSubElementType.getProperties()));
            pstmt.setTimestamp(3, new Timestamp(nwSubElementType.getLastModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String INSERT_HISTORY_MATRIC = "INSERT INTO NETWORK_SUBELE_HIST_METRIC "
            + " (HISTORY_ID, PROPERTY_METADATA_ID) "
            + " VALUES (?, ?) ";

    @Override
    public int[][] addHistoryMatric(List<MatricHistory> matricHistories) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY_MATRIC, matricHistories, (pstmt, matricHistoryType) -> {
            pstmt.setLong(1, matricHistoryType.getHistoryId());
            pstmt.setInt(2, matricHistoryType.getPropMetadataId());
        });
    }

    /*public List<NwSubElement> getBytypeIdAndElementId(List<Integer> elementIds, List<Integer> typeIds) {
    
    StringBuilder builder = new StringBuilder(GET_BY_TYPE_ELEMENT_P_1);
    builder.append(StringUtils.join(elementIds.iterator(), ','));
    builder = new StringBuilder(GET_BY_TYPE_ELEMENT_P_2);
    builder.append(StringUtils.join(typeIds.iterator(), ','));
    builder = new StringBuilder(GET_BY_TYPE_ELEMENT_P_3);
    
    List<NwSubElement> nwSubElements = new ArrayList<>();
    jdbcTemplate.retriveMultipleRows(builder.toString(), (rs) -> {
        NwSubElement nwSubElement = new NwSubElement();
        nwSubElement.setElementId(rs.getInt(1));
        nwSubElement.setName(rs.getString(2));
        nwSubElement.setTypeId(rs.getInt(3));
        nwSubElement.setProperties(JsonParser.fromJsonStr(rs.getString(4)));
    
        Calendar createdTimeStamp = CalendarUtil.getInstance().currTime();
        createdTimeStamp.setTimeInMillis(rs.getTimestamp(5).getTime());
        nwSubElement.setCreatedTimeStamp(createdTimeStamp);
    
        Calendar lastModifiedTimeStamp = CalendarUtil.getInstance().currTime();
        lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(6).getTime());
        nwSubElement.setLastModifiedTimeStamp(lastModifiedTimeStamp);
    
        nwSubElements.add(nwSubElement);
    });
    
    return nwSubElements;
    }*/

}
