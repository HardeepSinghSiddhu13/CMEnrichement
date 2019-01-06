package com.samsung.nmt.cmenrichment.correlator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.nmt.cmenrichment.dto.NwElement;
import com.samsung.nmt.cmenrichment.dto.NwSubElement;
import com.samsung.nmt.cmenrichment.processor.BatchProcessor;
import com.samsung.nmt.cmenrichment.qualifiers.ConfigQ;
import com.samsung.nmt.cmenrichment.storage.BatchStorageManager;
import com.samsung.platform.domain.kafka.EventList;

@Component
@ConfigQ
public class ConfigCorrelator implements Correlator {

    @Autowired
    BatchProcessor<NwElement> nwElementBatchProcessor;

    @Autowired
    BatchProcessor<NwSubElement> nwSubElementProcessor;

    @Autowired
    BatchStorageManager<NwElement> nwElementBatchStoreManager;

    @Autowired
    BatchStorageManager<NwSubElement> nwSubElementStoreManager;

    @Override
    public void correlate(EventList eventList) {

        BatchProcessedData<NwElement> nwElementProcessedData = nwElementBatchProcessor.process(
                eventList.getRequestheader(),
                eventList.getEventList());

        nwElementBatchStoreManager.cudProcessedData(nwElementProcessedData);

        BatchProcessedData<NwSubElement> nwSubElementProcessedData = nwSubElementProcessor.process(
                eventList.getRequestheader(),
                eventList.getEventList());

        nwSubElementStoreManager.cudProcessedData(nwSubElementProcessedData);

    }

}
