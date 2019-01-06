package com.samsung.nmt.cmenrichment.service;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.platform.domain.kafka.Event;

public interface JsonMergerService {

    MergedData merge(ObjectNode dbJson,
            Set<String> columnAttrs, Set<String> imutubleAttrs, List<Event> events);

    MergedData merge(ObjectNode dbJson, List<Event> events);

    MergedData merge(ObjectNode dbJson,
            Set<String> columnAttrs, List<Event> events);
}
