package com.samsung.nmt.cmenrichment.repo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.dto.HistoryData;
import com.samsung.nmt.cmenrichment.dto.MatricHistory;
import com.samsung.nmt.cmenrichment.dto.NwElement;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementQ;
import com.samsung.nmt.cmenrichment.utils.DateTimeUtil;
import com.samsung.nmt.cmenrichment.utils.JsonParser;

@Repository
@NwElementQ
public class NwElementRepoImpl implements MetadataRepo<String>, NwElementRepo, CacheableRepo {

    private static final Logger logger = LoggerFactory.getLogger(NwElementRepoImpl.class);

    private static final String TABLE = " NETWORK_ELEMENT ";

    @Autowired
    private Cache<String, Integer> cache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    //insert methods----------------------------------------------------
    private static final String INSERT = "INSERT INTO " + TABLE + " (NEID, TYPE_ID, NAME, STATUS_ID, VENDOR, VERSION, "
            + "  EMS_ID, PROPERTIES, CREATEDTIMESTAMP,  LASTMODIFIEDTIMESTAMP, LOCATION_ID) "
            + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?) ";

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = DuplicateKeyException.class)
    public Integer addAndGetId(NwElement nwElement) {
        Integer id = jdbcTemplate.insertAndGetId(INSERT, nwElement, (pstmt, nwElementType) -> {
            pstmt.setInt(1, nwElementType.getNeId());
            pstmt.setInt(2, nwElementType.getTypeId());
            pstmt.setString(3, nwElementType.getName());
            pstmt.setInt(4, nwElementType.getStatusId());
            pstmt.setString(5, nwElementType.getVendor());
            pstmt.setString(6, nwElementType.getVersion());
            pstmt.setInt(7, nwElementType.getEmsId());
            pstmt.setString(8, JsonParser.fromJsonNode(nwElementType.getProperties()));
            pstmt.setTimestamp(9, new Timestamp(nwElementType.getCreatedTimeStamp().getTimeInMillis()));
            pstmt.setTimestamp(10, new Timestamp(nwElementType.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(11, nwElementType.getLocationId());

        });

        cache.add(nwElement.getName(), id);
        return id;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, Integer> addAndGetIdsInBatch(List<NwElement> nwElements) {

        return jdbcTemplate.execute(new BatchConnectionCallBack<NwElement, Map<String, Integer>>(
                INSERT,
                nwElements,
                //set parameters in prepare statements
                (pstmt, nwElementType) -> {
                    pstmt.setInt(1, nwElementType.getNeId());
                    pstmt.setInt(2, nwElementType.getTypeId());
                    pstmt.setString(3, nwElementType.getName());
                    pstmt.setInt(4, nwElementType.getStatusId());
                    pstmt.setString(5, nwElementType.getVendor());
                    pstmt.setString(6, nwElementType.getVersion());
                    pstmt.setInt(7, nwElementType.getEmsId());
                    pstmt.setString(8, JsonParser.fromJsonNode(nwElementType.getProperties()));
                    pstmt.setTimestamp(9, new Timestamp(nwElementType.getCreatedTimeStamp().getTimeInMillis()));
                    pstmt.setTimestamp(10, new Timestamp(nwElementType.getLastModifiedTimeStamp().getTimeInMillis()));
                    pstmt.setInt(11, nwElementType.getLocationId());
                },
                //set primary key in output structure for inserted raw
                (nwElementType, map, i) -> {
                    map.put(nwElementType.getName(), i);
                    cache.add(nwElementType.getName(), i);
                },
                () -> {
                    //initialize output db
                    return new HashMap<>();
                }

        ));
    }

    @Override
    public Integer addIfAbsentAndGetId(String elementName) {

        Integer elementId = cache.get(elementName);
        if (elementId == null) {
            elementId = insertOrGetFromDB(elementName);
        }
        return elementId;
    }

    private static final String INSERT_ELE_NAME = "INSERT INTO " + TABLE
            + " (NAME, CREATEDTIMESTAMP)"
            + " VALUES(?, ?)";

    @Transactional(propagation = Propagation.REQUIRED)
    private Integer insertOrGetFromDB(String elementName) {
        Integer id = null;
        try {
            insertAndGetId(elementName);
        } catch (DuplicateKeyException e) {
            id = getId(elementName);
        }
        return id;

    }

    @Transactional
    public Integer insertAndGetId(String elementName) {
        return jdbcTemplate.insertAndGetId(INSERT_ELE_NAME, elementName, (pstmt, elementNameType) -> {
            pstmt.setString(1, elementNameType);
            pstmt.setTimestamp(2, new Timestamp(DateTimeUtil.getInstance().currTime().getTimeInMillis()));
        });
    }

    //update methods----------------------------------------------------
    private static final String UPDATE_ALL_EXCEPT_NAME_BY_ID = "UPDATE " + TABLE
            + " SET NEID = ?, TYPE_ID = ?, STATUS_ID = ?, VENDOR = ?, VERSION = ?, " +
            " EMS_ID = ?, PROPERTIES = ?,  LASTMODIFIEDTIMESTAMP = ?, LOCATION_ID = ?  WHERE ELEMENT_ID = ?";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateAllExceptNameUsingId(NwElement nwElement) {
        return jdbcTemplate.update(UPDATE_ALL_EXCEPT_NAME_BY_ID, (pstmt) -> {
            pstmt.setInt(1, nwElement.getNeId());
            pstmt.setInt(2, nwElement.getTypeId());
            pstmt.setInt(3, nwElement.getStatusId());
            pstmt.setString(4, nwElement.getVendor());
            pstmt.setString(5, nwElement.getVersion());
            pstmt.setInt(6, nwElement.getEmsId());
            pstmt.setString(7, JsonParser.fromJsonNode(nwElement.getProperties()));
            pstmt.setTimestamp(8, new Timestamp(nwElement.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(9, nwElement.getLocationId());
            pstmt.setInt(10, nwElement.getElementId());
        });
    }

    private static final String UPDATE = "UPDATE " + TABLE
            + " SET STATUS_ID = ?, VENDOR = ?, VERSION = ?, " +
            "  PROPERTIES = ?,  LASTMODIFIEDTIMESTAMP = ?  WHERE ELEMENT_ID = ?";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] updateInBatch(List<NwElement> nwElements) {
        return jdbcTemplate.batchUpdate(UPDATE, nwElements, (pstmt, nwElementType) -> {
            pstmt.setInt(1, nwElementType.getStatusId());
            pstmt.setString(2, nwElementType.getVendor());
            pstmt.setString(3, nwElementType.getVersion());
            pstmt.setString(4, JsonParser.fromJsonNode(nwElementType.getProperties()));
            pstmt.setTimestamp(5, new Timestamp(nwElementType.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(6, nwElementType.getElementId());
        });
    }

    private static final String UPDATE_PROPERTIES = "UPDATE " + TABLE
            + " SET PROPERTIES = ? WHERE ELEMENT_ID = ?";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateProperties(NwElement nwElement) {
        return jdbcTemplate.update(UPDATE_PROPERTIES, (pstmt) -> {
            pstmt.setString(1, nwElement.getProperties().toString());
            pstmt.setTimestamp(2, new Timestamp(nwElement.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(3, nwElement.getElementId());
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] updatePropertiesInBatch(List<NwElement> nwElements) {
        return jdbcTemplate.batchUpdate(UPDATE_PROPERTIES, nwElements, (pstmt, nwElementType) -> {
            pstmt.setString(1, nwElementType.getProperties().toString());
            pstmt.setTimestamp(2, new Timestamp(nwElementType.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(3, nwElementType.getElementId());
        });
    }

    private static final String UPDATE_ALL_MUTABLES = "UPDATE " + TABLE
            + " SET STATUS_ID = ?, VENDOR = ?, VERSION = ?, " +
            " PROPERTIES = ?,  LASTMODIFIEDTIMESTAMP = ? WHERE ELEMENT_ID = ?";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateAllMutuables(NwElement nwElement) {
        return jdbcTemplate.update(UPDATE_ALL_MUTABLES, (pstmt) -> {
            pstmt.setInt(1, nwElement.getStatusId());
            pstmt.setString(2, nwElement.getVendor());
            pstmt.setString(3, nwElement.getVersion());
            pstmt.setString(4, JsonParser.fromJsonNode(nwElement.getProperties()));
            pstmt.setTimestamp(5, new Timestamp(nwElement.getLastModifiedTimeStamp().getTimeInMillis()));
            pstmt.setInt(6, nwElement.getElementId());
        });
    }

    //select methods----------------------------------------------------

    private static final String GET_ALL_ID_NAME = "SELECT ELEMENT_ID, NAME FROM " + TABLE;

    @Override
    @Transactional
    public void initializeCache() {
        jdbcTemplate.retriveMultipleRows(GET_ALL_ID_NAME, (rs) -> {
            cache.add(rs.getString(2), rs.getInt(1));
        });

        logger.info("Nw Element cache : " + cache);
    }

    private static final String GET_BY_ELEMENT_ID = "SELECT NEID, TYPE_ID, NAME, STATUS_ID, VENDOR, VERSION, "
            + "  EMS_ID, PROPERTIES, CREATEDTIMESTAMP,  LASTMODIFIEDTIMESTAMP, LOCATION_ID  FROM  " + TABLE
            + "WHERE ELEMENT_ID = ?";

    @Override
    @Transactional(readOnly = true)
    public NwElement getNwElement(Integer elementId) {
        return jdbcTemplate.queryForObject(GET_BY_ELEMENT_ID, (rs, num) -> {
            NwElement nwElement = new NwElement();
            nwElement.setNeId(rs.getInt(1));
            nwElement.setTypeId(rs.getInt(2));
            nwElement.setName(rs.getString(3));
            nwElement.setStatusId(rs.getInt(4));
            nwElement.setVendor(rs.getString(5));
            nwElement.setVersion(rs.getString(6));
            nwElement.setEmsId(rs.getInt(7));

            ObjectNode ObjectNode = JsonParser.fromJsonStr(rs.getString(8));
            nwElement.setProperties(ObjectNode);

            Calendar createdTimeStamp = DateTimeUtil.getInstance().currTime();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(9).getTime());
            nwElement.setCreatedTimeStamp(createdTimeStamp);

            Calendar lastModifiedTimeStamp = DateTimeUtil.getInstance().currTime();
            lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(10).getTime());
            nwElement.setLastModifiedTimeStamp(lastModifiedTimeStamp);

            nwElement.setLocationId(rs.getInt(11));
            return nwElement;
        }, elementId);
    }

    private static final String GET_BY_NAME = " SELECT NEID, TYPE_ID, ELEMENT_ID, STATUS_ID, VENDOR, VERSION, "
            + "  EMS_ID, PROPERTIES, CREATEDTIMESTAMP,  LASTMODIFIEDTIMESTAMP, LOCATION_ID  FROM "
            + TABLE + " WHERE NAME = ?";

    @Override
    @Transactional(readOnly = true)
    public NwElement getNwElement(String neName) {
        return jdbcTemplate.queryForObject(GET_BY_NAME, (rs, num) -> {
            NwElement nwElement = new NwElement();
            nwElement.setNeId(rs.getInt(1));
            nwElement.setTypeId(rs.getInt(2));
            nwElement.setElementId(rs.getInt(3));
            nwElement.setStatusId(rs.getInt(4));
            nwElement.setVendor(rs.getString(5));
            nwElement.setVersion(rs.getString(6));
            nwElement.setEmsId(rs.getInt(7));

            ObjectNode ObjectNode = JsonParser.fromJsonStr(rs.getString(8));
            nwElement.setProperties(ObjectNode);

            Calendar createdTimeStamp = DateTimeUtil.getInstance().currTime();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(9).getTime());
            nwElement.setCreatedTimeStamp(createdTimeStamp);

            Calendar lastModifiedTimeStamp = DateTimeUtil.getInstance().currTime();
            lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(10).getTime());
            nwElement.setLastModifiedTimeStamp(lastModifiedTimeStamp);

            nwElement.setLocationId(rs.getInt(11));
            return nwElement;
        }, neName);
    }

    private static final String GET_BY_ELEMENT_NAME_P_1 = "SELECT NEID, TYPE_ID, NAME, STATUS_ID, VENDOR, VERSION, "
            + "  EMS_ID, PROPERTIES, CREATEDTIMESTAMP,  LASTMODIFIEDTIMESTAMP, LOCATION_ID, ELEMENT_ID FROM "
            + TABLE + "  WHERE NAME IN ( ";
    private static final String GET_BY_ELEMENT_NAME_P_2 = " )";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, NwElement> getAllByNames(String neNamesInClause) {
        StringBuilder queryBuilder = new StringBuilder(GET_BY_ELEMENT_NAME_P_1);
        queryBuilder.append(neNamesInClause);
        queryBuilder.append(GET_BY_ELEMENT_NAME_P_2);

        Map<String, NwElement> map = new HashMap<>();
        jdbcTemplate.retriveMultipleRows(queryBuilder.toString(), (rs) -> {
            NwElement nwElement = new NwElement();
            nwElement.setNeId(rs.getInt(1));
            nwElement.setTypeId(rs.getInt(2));
            nwElement.setName(rs.getString(3));
            nwElement.setStatusId(rs.getInt(4));
            nwElement.setVendor(rs.getString(5));
            nwElement.setVersion(rs.getString(6));
            nwElement.setEmsId(rs.getInt(7));

            String properties = rs.getString(8);
            if (properties != null) {
                ObjectNode ObjectNode = JsonParser.fromJsonStr(properties);
                nwElement.setProperties(ObjectNode);
            }

            Calendar createdTimeStamp = DateTimeUtil.getInstance().currTime();
            createdTimeStamp.setTimeInMillis(rs.getTimestamp(9).getTime());
            nwElement.setCreatedTimeStamp(createdTimeStamp);

            Timestamp modifiedTimeStamp = rs.getTimestamp(10);
            if (modifiedTimeStamp != null) {
                Calendar lastModifiedTimeStamp = DateTimeUtil.getInstance().currTime();
                lastModifiedTimeStamp.setTimeInMillis(rs.getTimestamp(10).getTime());
                nwElement.setLastModifiedTimeStamp(lastModifiedTimeStamp);
            }

            nwElement.setLocationId(rs.getInt(11));
            nwElement.setElementId(rs.getInt(12));

            map.put(nwElement.getName(), nwElement);
        });

        return map;
    }

    private static final String GET_ID = "SELECT ELEMENT_ID FROM " + TABLE + " WHERE NAME = ?";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer getId(String neName) {
        Integer id = jdbcTemplate.queryForObject(GET_ID, Integer.class, neName);
        cache.add(neName, id);
        return id;
    }

    @Override
    public Integer getIdFromCache(String neName) {
        return cache.get(neName);
    }

    //delete methods----------------------------------------------------
    private static final String DELETE = "DELETE FROM " + TABLE + " WHERE ELEMENT_ID = ? ";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] deleteNwElementsInBatch(List<NwElement> nwElements) {
        return jdbcTemplate.batchUpdate(DELETE, nwElements, (pstmt, nwElementType) -> {
            pstmt.setLong(1, nwElementType.getElementId());
        });
    }

    //history methods----------------------------------------------------
    private static final String INSERT_HISTORY = "INSERT INTO NETWORK_ELEMENT_HISTORY"
            + " (ELEMENT_ID, NEID, TYPE_ID, NAME, STATUS_ID, VENDOR, VERSION, "
            + " EMS_ID, PROPERTIES,  MODIFIEDTIMESTAMP)"
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer addHistoryAndGetId(NwElement nwElement) {
        return jdbcTemplate.insertAndGetId(INSERT_HISTORY, nwElement, (pstmt, nwElementType) -> {
            pstmt.setInt(1, nwElementType.getElementId());
            pstmt.setInt(2, nwElementType.getNeId());
            pstmt.setInt(3, nwElementType.getTypeId());
            pstmt.setString(4, nwElementType.getName());
            pstmt.setInt(5, nwElementType.getStatusId());
            pstmt.setString(6, nwElementType.getVendor());
            pstmt.setString(7, nwElementType.getVersion());
            pstmt.setInt(8, nwElementType.getEmsId());
            pstmt.setString(9, nwElementType.getProperties().toString());
            pstmt.setTimestamp(10, new Timestamp(nwElementType.getLastModifiedTimeStamp().getTimeInMillis()));

        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHistoryInBatch(List<NwElement> nwElements) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY, nwElements, (pstmt, nwElementType) -> {
            pstmt.setInt(1, nwElementType.getElementId());
            pstmt.setInt(2, nwElementType.getNeId());
            pstmt.setInt(3, nwElementType.getTypeId());
            pstmt.setString(4, nwElementType.getName());
            pstmt.setInt(5, nwElementType.getStatusId());
            pstmt.setString(6, nwElementType.getVendor());
            pstmt.setString(7, nwElementType.getVersion());
            pstmt.setInt(8, nwElementType.getEmsId());
            pstmt.setString(9, nwElementType.getProperties().toString());
            pstmt.setTimestamp(10, new Timestamp(nwElementType.getLastModifiedTimeStamp().getTimeInMillis()));
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Long> addHistoryAndGetIdsInBatch(List<HistoryData<NwElement>> nwElementHistory) {
        return jdbcTemplate.batchInsertAndGetLongIds(INSERT_HISTORY, nwElementHistory, (pstmt, history) -> {
            NwElement nwElement = history.getHistoryData();
            pstmt.setInt(1, nwElement.getElementId());
            pstmt.setInt(2, nwElement.getNeId());
            pstmt.setInt(3, nwElement.getTypeId());
            pstmt.setString(4, nwElement.getName());
            pstmt.setInt(5, nwElement.getStatusId());
            pstmt.setString(6, nwElement.getVendor());
            pstmt.setString(7, nwElement.getVersion());
            pstmt.setInt(8, nwElement.getEmsId());
            pstmt.setString(9, nwElement.getProperties().toString());
            pstmt.setTimestamp(10, new Timestamp(nwElement.getLastModifiedTimeStamp().getTimeInMillis()));
        });
    }

    private static final String INSERT_HISTORY_MATRIC = "INSERT INTO NETWORK_ELE_HIST_METRIC "
            + " (HISTORY_ID, PROPERTY_METADATA_ID)"
            + " VALUES(?, ?)";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHistoryMatric(int historyId, List<Integer> metadataIds) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY_MATRIC, metadataIds, (ps, metadataId) -> {
            ps.setInt(1, historyId);
            ps.setInt(2, metadataId);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int[][] addHistoryMatricInBatch(List<MatricHistory> matricHistories) {
        return jdbcTemplate.batchUpdate(INSERT_HISTORY_MATRIC, matricHistories, (pstmt, matricHistoryType) -> {
            pstmt.setLong(1, matricHistoryType.getHistoryId());
            pstmt.setInt(2, matricHistoryType.getPropMetadataId());
        });
    }

}
