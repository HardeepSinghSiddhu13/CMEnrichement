package com.samsung.nmt.cmenrichment.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.nmt.cmenrichment.dto.NwEMS;
import com.samsung.nmt.cmenrichment.dto.NwElement;
import com.samsung.platform.domain.kafka.Event;

public interface NwElementService {
    NwElement createNwElementFromCollectorAddData(Event event, ObjectNode objectNode, NwEMS nwEMS);

    NwElement createNwElementFromCollectorUpdateData(MergedData mergedData, NwElement dbElement);
}
