package com.samsung.nmt.cmenrichment.correlator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.SwElement;
import com.samsung.nmt.cmenrichment.processor.BatchProcessor;
import com.samsung.nmt.cmenrichment.qualifiers.SoftwareQ;
import com.samsung.nmt.cmenrichment.qualifiers.SwElementQ;
import com.samsung.nmt.cmenrichment.storage.BatchStorageManager;
import com.samsung.platform.domain.kafka.EventList;

@Component
@SoftwareQ
public class SoftwareCorrelator implements Correlator {

    private static final Logger logger = LoggerFactory.getLogger(ConfigCorrelator.class);

    @Autowired
    @SwElementQ
    BatchProcessor<SwElement> swElementProcessor;

    @Autowired
    @SwElementQ
    BatchStorageManager<SwElement> swElementStoreManager;

    @Override
    public void correlate(EventList eventList) {

        BatchProcessedData<SwElement> swElementProcessedData = swElementProcessor.process(
                eventList.getRequestheader(),
                eventList.getEventList());

        if (swElementProcessedData != null)
            swElementStoreManager.cudProcessedData(swElementProcessedData);

    }
}
