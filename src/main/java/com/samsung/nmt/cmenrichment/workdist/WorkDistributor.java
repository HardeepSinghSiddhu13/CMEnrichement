package com.samsung.nmt.cmenrichment.workdist;

import java.util.List;

import com.samsung.platform.domain.kafka.EventList;

/**
 * This interface define interface to distribute correlating task to appropriate
 * correlator. Implementation of this interface will provide multi-threading api
 * for parallel processing.
 *
 */
public interface WorkDistributor {
    /**
     * @param eventList
     *            raw data from collector
     * @param requestType
     *            request type
     */
    void distribute(List<EventList> eventList, RequestType requestType);

    /**
     * Check whether threads are busy or not.
     *
     * @return true if thread in thread pool is free otherwise false.
     */
    boolean isBusy();

    boolean stopGraceFully();
}
