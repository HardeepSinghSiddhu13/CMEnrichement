package com.samsung.nmt.cmenrichment.correlator;

import com.samsung.platform.domain.kafka.EventList;

/**
 * Implementation of this class will correlate the data for particular sub
 * domain.
 *
 */
public interface Correlator {
    /**
     * This method will process the data using
     * {@link com.samsung.nmt.cmenrichment.processor.BatchProcessor} and store the
     * data in db using
     * {@link com.samsung.nmt.cmenrichment.storage.BatchStorageManager}.
     *
     * @param eventList
     *            - json data from collector
     */
    void correlate(EventList eventList);
}
