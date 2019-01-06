package com.samsung.nmt.cmenrichment.client;

import com.samsung.nmt.cmenrichment.workdist.RequestType;

/**
 * Define Generic client interface to fetch collector data(from kafak bus using
 * webservice).
 *
 */
public interface Client {
    /**
     * This method fetch collector data repetitively( in loop).
     */
    void startPoller();

    RequestType type();
}
