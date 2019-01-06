package com.samsung.nmt.cmenrichment.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.samsung.nmt.cmenrichment.cache.Cache;
import com.samsung.nmt.cmenrichment.exceptions.ElementDetailsNotFoundException;

@Repository
public class LocationRepoImpl implements LocationRepo, CacheableRepo {

    private static final Logger logger = LoggerFactory.getLogger(LocationRepoImpl.class);

    @Autowired
    private Cache<String, Integer> locationIdCache;

    @Autowired
    private Cache<String, Integer> circleIdCache;

    @Autowired
    private SpringJdbcTemplate jdbcTemplate;

    private static final String GET_ALL_LOCATION_BY_ELEMENT_NAME = "SELECT LOCATION_ID, ELEMENT_NAME FROM LOCATION_ELEMENT";

    private static final String GET_ALL_LOCATION = "SELECT LOCATION_ID, CIRCLE FROM LOCATION_DETAIL";

    @Override
    public synchronized void initializeCache() {

        Map<Integer, List<String>> map = new HashMap<>();
        jdbcTemplate.retriveMultipleRows(GET_ALL_LOCATION_BY_ELEMENT_NAME, (rs) -> {
            Integer locationId = rs.getInt(1);
            String elementName = rs.getString(2).trim();

            locationIdCache.add(elementName, locationId);

            List<String> temp = null;
            if (map.containsKey(locationId)) {
                temp = map.get(locationId);
            } else {
                temp = new ArrayList<>();
                map.put(locationId, temp);
            }
            temp.add(elementName);
        });

        logger.info("location cache : " + locationIdCache);

        StringBuilder builder = new StringBuilder();

        jdbcTemplate.retriveMultipleRows(GET_ALL_LOCATION, (rs) -> {
            Integer locationId = rs.getInt(1);
            Integer circleId = rs.getInt(2);

            if (map.containsKey(locationId)) {
                map.get(locationId).forEach((elementName) -> {
                    circleIdCache.add(elementName, circleId);
                });
            } else {
                builder.append("[ location_id: ");
                builder.append(locationId);
                builder.append(", circle Id");
                builder.append(circleId);
                builder.append("] ");
            }
        });

        if (builder.length() > 0) {
            throw new ElementDetailsNotFoundException(
                    "Element details not found against location for : " + builder.toString());
        }

        logger.info("circle cache : " + circleIdCache);
    }

    @Override
    public Integer getLocationId(String elementName) {
        return locationIdCache.get(elementName);
    }

    @Override
    public Integer getCircleId(String elementName) {
        return circleIdCache.get(elementName);
    }

}
