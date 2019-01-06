package com.samsung.nmt.cmenrichment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import com.samsung.nmt.cmenrichment.client.Client;
import com.samsung.nmt.cmenrichment.client.SpringWebClient;
import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.constants.Constants;
import com.samsung.nmt.cmenrichment.qualifiers.ConfigQ;
import com.samsung.nmt.cmenrichment.workdist.RequestType;

@Configuration
public class CMEnrichmentSpringConfig {

    @Bean
    @DependsOn(value = Constants.APP_PROPERTIES_BEAN_NAME)
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(AppProperties appProperties) {

        //config this pool as pre requirement
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appProperties.getWorkerThreadpoolSize());
        executor.setQueueCapacity(appProperties.getWorkerThreadQueueMaxCapacity());
        executor.setThreadNamePrefix(Constants.WORKER_THREAD_POOL_PREFIX);
        return executor;
    }

    @Bean
    public WebClient webClient(AppProperties appProperties) {
        WebClient.Builder webClientBuilder = WebClient.builder();
        webClientBuilder
                //.baseUrl(ClientConstants.CLIENT_URL)
                .defaultHeader(appProperties.getClientDomainKeyName(), appProperties.getClientDomain())
                .defaultHeader(appProperties.getClientVersionKeyName(), appProperties.getClientVersion())
                .build();

        return webClientBuilder.build();
    }

    @Bean
    @ConfigQ
    public Client configWebClient(AppProperties appProperties) {
        return new SpringWebClient(appProperties.getConfigSubDomain(), RequestType.CONFIG);
    }
}
