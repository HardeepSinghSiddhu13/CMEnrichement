package com.samsung.nmt.cmenrichment.correlator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.HwElement;
import com.samsung.nmt.cmenrichment.processor.BatchProcessor;
import com.samsung.nmt.cmenrichment.qualifiers.HardwareQ;
import com.samsung.nmt.cmenrichment.qualifiers.HwElementQ;
import com.samsung.nmt.cmenrichment.storage.BatchStorageManager;
import com.samsung.platform.domain.kafka.EventList;

@Component
@HardwareQ
public class HardwareCorrelator implements Correlator {

    private static final Logger logger = LoggerFactory.getLogger(ConfigCorrelator.class);

    @Autowired
    @HwElementQ
    BatchProcessor<HwElement> hwElementProcessor;

    @Autowired
    @HwElementQ
    BatchStorageManager<HwElement> hwElementStoreManager;

    @Override
    public void correlate(EventList eventList) {

        BatchProcessedData<HwElement> hwElementProcessedData = hwElementProcessor.process(
                eventList.getRequestheader(), eventList.getEventList());

        if (hwElementProcessedData != null)
            hwElementStoreManager.cudProcessedData(hwElementProcessedData);

    }
}
