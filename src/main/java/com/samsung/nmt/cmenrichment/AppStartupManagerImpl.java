package com.samsung.nmt.cmenrichment;

import java.util.Map;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import com.samsung.nmt.cmenrichment.client.Client;
import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.repo.CacheableRepo;
import com.samsung.nmt.cmenrichment.workdist.RequestType;
import com.samsung.nmt.cmenrichment.workdist.WorkDistributor;

@Component
public class AppStartupManagerImpl implements AppStartupManager {

 //   private static Logger logger = LoggerFactory.getLogger(AppStartupManagerImpl.class);

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @Autowired
    private WorkDistributor workDistributor;

    @Autowired
    private AppProperties appProperties;

    @Override
    public void start() {

   //     logger.info("Initializing Cacheable Repositories...");
        initCacheRepositories();

      //  logger.info("Initializing clients...");
        initClients();
    }

    private void initCacheRepositories() {
        Map<String, CacheableRepo> cacheableRepoBeansMap = configurableApplicationContext
                .getBeansOfType(CacheableRepo.class);
        cacheableRepoBeansMap.entrySet().forEach((entry) -> {
            String cachebleRepoBeanName = entry.getKey();
            CacheableRepo cacheableRepo = entry.getValue();
            cacheableRepo.initializeCache();
       //     logger.info("Cache Initialized : " + cachebleRepoBeanName);
        });
    }

    private void initClients() {
        Map<String, Client> clientBeansMap = configurableApplicationContext.getBeansOfType(Client.class);
        clientBeansMap.entrySet().forEach((entry) -> {
            Client client = entry.getValue();
            RequestType requestType = client.type();
            switch (requestType) {
            case CONFIG: {
                startPolling(client, requestType, appProperties.getConfigClientCount());
                break;
            }

            case FW: {
                startPolling(client, requestType, appProperties.getFwClientCount());
                break;
            }

            case SW: {
                startPolling(client, requestType, appProperties.getSwClientCount());
                break;
            }

            case HW: {
                startPolling(client, requestType, appProperties.getHwClientCount());
                break;
            }

            }

        });
    }

    private void startPolling(Client client, RequestType requestType, int count) {
        for (int i = 1; i <= count; i++) {
            client.startPoller();
          //  logger.info("Client polling started for " + requestType + ", # : " + i);
        }
    }

    @Override
    public void stop() {

    }

}
