package com.samsung.nmt.cmenrichment;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CMEnrichmentApplication {

//    private static Logger logger = LoggerFactory.getLogger(CMEnrichmentApplication.class);

    public static void main(String[] args) {

        logger.info("Booting CMEnrichmentApplication...");

        ConfigurableApplicationContext configurableApplicationContext = SpringApplication
                .run(CMEnrichmentApplication.class, args);

        AppStartupManager appStartupManager = configurableApplicationContext.getBean(AppStartupManager.class);
        appStartupManager.start();
    }
}
