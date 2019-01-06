package com.samsung.nmt.cmenrichment.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.client.Action;
import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.nmt.cmenrichment.utils.JsonParser;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.EventProperties;
import com.samsung.platform.domain.kafka.Identifier;

@Component
public class JsonMergerServiceImpl implements JsonMergerService {

    @Autowired
    private AppProperties appProperties;

    @Override
    public MergedData merge(ObjectNode dbJson,
            Set<String> columnAttrs, Set<String> jcbAttrKeys, List<Event> events) {

        ObjectNode mergedJson = dbJson.deepCopy();
        List<String> updatedAttrs = new LinkedList<>();
        boolean isOnlyPropUpdated = true;

        for (Event event : events) {
            Action action = Action.getAction(event.getIdentifier().getAction());
            ObjectNode modifiedProps = event.getProperties().getAdditionalFields();

            switch (action) {
            case UPDATE:
            case ADD: {
                Iterator<Map.Entry<String, JsonNode>> it = modifiedProps.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    if (columnAttrs.contains(entry.getKey())) {
                        isOnlyPropUpdated = false;
                    }
                    if (jcbAttrKeys.contains(entry.getKey())) {
                        JsonNode source = dbJson.get(appProperties.getSourceKey());
                        if (source != null && source.asText().equals(appProperties.getSourceCollectorValue())) {
                            mergedJson.put(entry.getKey(), entry.getValue().asText());
                            updatedAttrs.add(entry.getKey());
                        }
                    } else {
                        mergedJson.put(entry.getKey(), entry.getValue().asText());
                        updatedAttrs.add(entry.getKey());
                    }
                }
                break;
            }

            case DELETE: {
                Iterator<String> it = modifiedProps.fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    if (columnAttrs.contains(key)) {
                        isOnlyPropUpdated = false;
                    }

                    if (jcbAttrKeys.contains(key)) {
                        JsonNode source = dbJson.get(appProperties.getSourceKey());
                        if (source != null && source.asText().equals(appProperties.getSourceCollectorValue())) {
                            mergedJson.remove(key);
                            updatedAttrs.add(key);
                        }
                    } else {
                        mergedJson.remove(key);
                        updatedAttrs.add(key);
                    }

                }
                break;
            }
            }
        }

        return new MergedData(isOnlyPropUpdated, mergedJson, updatedAttrs);
    }

    @Override
    public MergedData merge(ObjectNode dbJson,
            Set<String> columnAttrs, List<Event> events) {

        ObjectNode mergedJson = dbJson.deepCopy();
        boolean isOnlyPropUpdated = true;
        List<String> updatedAttrs = new LinkedList<>();

        for (Event event : events) {
            Action action = Action.getAction(event.getIdentifier().getAction());
            ObjectNode modifiedProps = event.getProperties().getAdditionalFields();

            switch (action) {
            case UPDATE:
            case ADD: {
                Iterator<Map.Entry<String, JsonNode>> it = modifiedProps.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    if (columnAttrs.contains(entry.getKey())) {
                        isOnlyPropUpdated = false;
                    }
                    mergedJson.put(entry.getKey(), entry.getValue().asText());
                    updatedAttrs.add(entry.getKey());
                }
                break;
            }

            case DELETE: {
                Iterator<String> it = modifiedProps.fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    if (columnAttrs.contains(key)) {
                        isOnlyPropUpdated = false;
                    }
                    mergedJson.remove(key);
                    updatedAttrs.add(key);
                }
                break;
            }
            }
        }

        return new MergedData(isOnlyPropUpdated, mergedJson, updatedAttrs);
    }

    @Override
    public MergedData merge(ObjectNode dbJson, List<Event> events) {
        ObjectNode mergedJson = dbJson.deepCopy();
        List<String> updatedAttrs = new LinkedList<>();

        for (Event event : events) {
            Action action = Action.getAction(event.getIdentifier().getAction());
            ObjectNode modifiedProps = event.getProperties().getAdditionalFields();

            switch (action) {
            case UPDATE:
            case ADD: {
                Iterator<Map.Entry<String, JsonNode>> it = modifiedProps.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    mergedJson.put(entry.getKey(), entry.getValue().asText());
                    updatedAttrs.add(entry.getKey());
                }
                break;
            }

            case DELETE: {
                Iterator<String> it = modifiedProps.fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    mergedJson.remove(key);
                    updatedAttrs.add(key);
                }
                break;
            }
            }
        }
        return new MergedData(true, mergedJson, updatedAttrs);
    }

    public static void main(String[] args) {
        JsonMergerServiceImpl impl = new JsonMergerServiceImpl();

        ObjectNode collectorJson = JsonParser.createObjectNode();
        collectorJson.put("K1", "V1-1");
        collectorJson.put("K2", "V2-2");
        collectorJson.put("K3", "V3-3");
        collectorJson.put("K7", "V7");

        ObjectNode dbJson = JsonParser.createObjectNode();
        dbJson.put("K1", "V1");
        dbJson.put("K2", "V2");
        dbJson.put("K3", "V3");
        dbJson.put("K4", "V4");
        dbJson.put("K5", "V5");
        dbJson.put("K6", "V6");

        Set<String> columnAttrs = new HashSet<>();
        columnAttrs.add("K4");
        columnAttrs.add("K5");

        Set<String> imutubleAttrs = new HashSet<>();
        imutubleAttrs.add("K1");
        //imutubleAttrs.add("K3");

        Event event = new Event();
        Identifier identifier = new Identifier();
        identifier.setAction("DELETE");
        event.setIdentifier(identifier);
        EventProperties eventProperties = new EventProperties();
        eventProperties.setAdditionalFields(collectorJson);
        event.setProperties(eventProperties);

        /*ObjectNode objectNode = impl.merge(dbJson, Arrays.asList(event));
        System.out.println(objectNode.toString());*/

        MergedData mergedData = impl.merge(dbJson, columnAttrs, Arrays.asList(event));
        System.out.println(mergedData);

    }

}