package com.samsung.nmt.cmenrichment.constants;

import java.util.TimeZone;

public class Constants {
    public static int BATCH_SIZE = 5;
    public static TimeZone TIME_ZONE = TimeZone.getDefault();
    public static final String WORKER_THREAD_POOL_PREFIX = "cm-worker-";
    public static final String APP_PROPERTIES_BEAN_NAME = "AppProperties";
    public static final String CM_PROPERTIES_SPRING_FILE_PATH = "file:${cm.enrichment.config.home}/cmenrichment.properties";
}
