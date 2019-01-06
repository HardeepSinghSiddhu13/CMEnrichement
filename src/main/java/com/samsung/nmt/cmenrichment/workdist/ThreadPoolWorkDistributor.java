package com.samsung.nmt.cmenrichment.workdist;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.correlator.Correlator;
import com.samsung.nmt.cmenrichment.qualifiers.ConfigQ;
import com.samsung.nmt.cmenrichment.utils.JsonParser;
import com.samsung.platform.domain.kafka.EventList;

/**
 * Provide implementation of
 * com.samsung.nmt.cmenrichment.workdist.WorkDistributor using spring thread
 * pool.
 *
 */
@Component
public class ThreadPoolWorkDistributor implements WorkDistributor {

    private static Logger logger = LoggerFactory.getLogger(ThreadPoolWorkDistributor.class);

    @Autowired
    @ConfigQ
    private Correlator configCorrelator;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private AppProperties appProperties;

    @Override
    public void distribute(List<EventList> eventListMessages, RequestType requestType) {

        switch (requestType) {
        case CONFIG:
            submitTask(eventListMessages, configCorrelator, RequestType.CONFIG);
            break;

        default:
            break;
        }

    }

    private void submitTask(List<EventList> eventListMessages, Correlator correlator, RequestType requestType) {

        eventListMessages.forEach((eventListMsg) -> {
            taskExecutor.execute(() -> {
                try {
                    correlator.correlate(eventListMsg);
                } catch (Exception e) {
                    logger.error("Error while correalting data, request type : " + requestType + ", eventList : "
                            + JsonParser.toJsonString(eventListMsg), e);
                }
            });
        });
    }

    @Override
    public boolean isBusy() {
        return taskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity() < appProperties
                .getWorkerThreadQueueThreshold();
    }

    @Override
    public boolean stopGraceFully() {
        return false;
    }

}
