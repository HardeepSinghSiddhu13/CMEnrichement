package com.samsung.nmt.cmenrichment.processor;

import java.util.List;
import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.RequestHeader;

/**
 * Define interface to process raw data received from collector in batches.
 *
 * @param <R>
 *            processed data
 */
public interface BatchProcessor<R> {
    /**
     *
     * @param header
     *            - request header received from collector
     * @param events
     *            - batch raw data
     * @return processed data
     */
    BatchProcessedData<R> process(RequestHeader requestHeader, List<Event> events);
}
