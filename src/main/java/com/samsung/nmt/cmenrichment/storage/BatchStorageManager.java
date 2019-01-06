package com.samsung.nmt.cmenrichment.storage;

import com.samsung.nmt.cmenrichment.dto.BatchProcessedData;

/**
 * @param <D>
 *            type of domain for which data to be processed
 */
public interface BatchStorageManager<D> {

    /**
     * @param batchProcessedData
     *            - data processed by implementation of
     *            {@link com.samsung.nmt.cmenrichment.processor.BatchProcessor}
     */
    void cudProcessedData(BatchProcessedData<D> batchProcessedData);
}
