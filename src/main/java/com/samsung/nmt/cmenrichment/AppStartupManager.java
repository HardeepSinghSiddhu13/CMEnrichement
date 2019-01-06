package com.samsung.nmt.cmenrichment;

/**
 * Implementation of this class will handle all the startup and application
 * shutdown task.
 *
 */
public interface AppStartupManager {
    /**
     * This method will initialize all the clients, cache and other required startup
     * task.
     */
    void start();

    void stop();
}
