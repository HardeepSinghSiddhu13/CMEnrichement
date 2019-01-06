package com.samsung.nmt.cmenrichment.client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.utils.JsonParser;
import com.samsung.nmt.cmenrichment.workdist.RequestType;
import com.samsung.nmt.cmenrichment.workdist.WorkDistributor;
import com.samsung.platform.domain.kafka.EventList;
import com.samsung.platform.domain.kafka.RequestHeader;

import reactor.core.publisher.Mono;

/**
 * Provide Implementation of {@link com.samsung.nmt.cmenrichment.client.Client}
 * using spring web client.
 */
public class SpringWebClient implements Client {

//    private Logger logger = LoggerFactory.getLogger(SpringWebClient.class);

    @Autowired
    private WorkDistributor workDistributor;

    @Autowired
    private WebClient webClient;

    @Autowired
    private AppProperties appProperties;

    private String subDomain;
    private RequestType requestType;

    public SpringWebClient(String subDomain, RequestType requestType) {
        this.subDomain = subDomain;
        this.requestType = requestType;
    }

    @Override
    public void startPoller() {

//        logger.info("initializing client for sub domain : " + subDomain);

        EventList eventList = new EventList();
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setVersion(appProperties.getClientVersion());
        requestHeader.setDomain(appProperties.getClientDomain());
        requestHeader.setSubDomain(subDomain);
        eventList.setRequestheader(requestHeader);

        ParameterizedTypeReference<List<EventList>> parameterizedTypeReference = new ParameterizedTypeReference<List<EventList>>() {
        };
        AtomicInteger retryCount = new AtomicInteger(0);

        Mono<List<EventList>> monoKafkaResponse = webClient.post()
                .uri(appProperties.getClientURL())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(appProperties.getClientSubDomainKeyName(), subDomain)
                .body(BodyInserters.fromObject(Arrays.asList(eventList)))
                .retrieve()
                .bodyToMono(parameterizedTypeReference);

        monoKafkaResponse
                .doOnError((e) -> logger.error("Errow while fetchind data : subDomain : " + subDomain, e))
                .retry((t) -> true)
                .repeat(() -> {

                    //check in infinite loop whether worker is busy or empty response threshold reached
                    while (true) {
                        boolean isWorkerBusy = workDistributor.isBusy();
                        if (isWorkerBusy || isEmptyResponseThresholdReached(retryCount)) {

                            // If any of above condition satisfied then suspend polling
                            //for configured time period

                            if (isWorkerBusy) {
                                logger.info("worker is busy.. suspend polling");
                                suspendPolling(appProperties.getClientWaitTime());
                            } else {
                                logger.info("empty response threshold reached.. suspend polling");
                                suspendPolling(appProperties.getClientEmptyResponseRetryWaitTime());
                                retryCount.set(0);
                            }

                        } else {
                            break;
                        }
                    }
                    return true;
                })
                .subscribe((eventListMessages) -> {
                    logger.info("requestType :  " + requestType);

                    try {
                        if (eventListMessages != null && eventListMessages.size() > 0) {
                            workDistributor.distribute(eventListMessages, requestType);
                            retryCount.set(0);
                        } else {
                            int count = retryCount.incrementAndGet();
                            if (logger.isDebugEnabled()) {
                                logger.debug("Empty/null response received - incrementing retry counter : " + count);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error while submitting messages to work distributor,  eventListMessages : "
                                + JsonParser.toJsonString(eventListMessages), e);
                    }
                });

    }

    public boolean isEmptyResponseThresholdReached(AtomicInteger retryCount) {
        return retryCount.get() > appProperties.getClientEmptyResponseRetry();
    }

    private void suspendPolling(long waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException ex) {
 //           logger.error("Errow while fetchind data : subDomain : " + subDomain, ex);
        }
    }

    @Override
    public RequestType type() {
        return requestType;
    }

}
