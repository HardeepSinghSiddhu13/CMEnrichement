package com.samsung.nmt.cmenrichment.constants;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component(Constants.APP_PROPERTIES_BEAN_NAME)
@PropertySource(value = Constants.CM_PROPERTIES_SPRING_FILE_PATH)
@ConfigurationProperties
public class AppProperties implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(AppProperties.class);

    @Value("${cm.enrichment.db.batchsize}")
    public int dbBatchSize;

    @Value("${cm.enrichment.client.url}")
    public String clientURL;

    @Value("${cm.enrichment.client.domain}")
    public String clientDomain;

    @Value("${cm.enrichment.client.version}")
    public String clientVersion;

    @Value("${cm.enrichment.client.wait.time}")
    public long clientWaitTime;

    @Value("${cm.enrichment.client.sub.domain.config}")
    public String configSubDomain;

    @Value("${cm.enrichment.client.sub.domain.sw}")
    public String swSubDomain;

    @Value("${cm.enrichment.client.sub.domain.hw}")
    public String hwSubDomain;

    @Value("${cm.enrichment.client.sub.domain.fw}")
    public String fwSubDomain;

    @Value("${cm.enrichment.nwelement.type}")
    public String nwElementTypeName;

    @Value("${cm.enrichment.nwelement.sys_id}")
    public String nwElementSysIdAttrKey;

    @Value("${cm.enrichment.nwelement.status}")
    public String nwElementStatusAttrKey;

    @Value("${cm.enrichment.nwelement.vendor_name}")
    public String nwElementVendorNameAttrKey;

    @Value("${cm.enrichment.nwelement.sw_version}")
    public String nwElementSoftwareVersionAttrKey;

    @Value("${cm.enrichment.nwelement.location_name}")
    public String nwElementLocationNameAttrKey;

    @Value("${cm.enrichment.nwelement.latitude}")
    public String nwElementLatitudeAttrKey;

    @Value("${cm.enrichment.nwelement.longitude}")
    public String nwElementLongitudeAttrKey;

    @Value("${cm.enrichment.nwelement.identifier}")
    public String nwElementIdentifier;

    @Value("${cm.enrichment.worker.thread.pool.size}")
    public int workerThreadpoolSize;

    @Value("${cm.enrichment.worker.thread.pool.size.using.processor}")
    public boolean workerThreadpoolSizeUsingProcessor;

    @Value("${cm.enrichment.worker.thread.queue.threshold}")
    public int workerThreadQueueThreshold;

    @Value("${cm.enrichment.worker.thread.queue.max.capacity}")
    public int workerThreadQueueMaxCapacity;

    @Value("${cm.enrichment.client.domain.key.name}")
    public String clientDomainKeyName;

    @Value("${cm.enrichment.client.version.key.name}")
    public String clientVersionKeyName;

    @Value("${cm.enrichment.client.sub.domain.key.name}")
    public String clientSubDomainKeyName;

    @Value("${cm.enrichment.client.empty.response.retry}")
    public int clientEmptyResponseRetry;

    @Value("${cm.enrichment.client.empty.response.retry.wait.time}")
    public int clientEmptyResponseRetryWaitTime;

    @Value("${cm.enrichment.nwelement.jcb.keys}")
    public String nwelementJcbKeysStr;

    @Value("${cm.enrichment.source.key}")
    public String sourceKey;

    @Value("${cm.enrichment.source.jcb.value}")
    public String sourceJcbValue;

    @Value("${cm.enrichment.source.collector.value}")
    public String sourceCollectorValue;

    @Value("${cm.enrichment.status.active.value}")
    public String statusActiveValue;

    @Value("${cm.enrichment.timezone}")
    public String timeZoneStr;

    @Value("${cm.enrichment.client.config.count}")
    public int configClientCount;

    @Value("${cm.enrichment.client.sw.count}")
    public int swClientCount;

    @Value("${cm.enrichment.client.hw.count}")
    public int hwClientCount;

    @Value("${cm.enrichment.client.fw.count}")
    public int fwClientCount;

    public Set<String> nwElementColumnAttrs;

    public Set<String> nwelementJcbKeys;

    public TimeZone timeZone;

    public void init() {
        Constants.BATCH_SIZE = dbBatchSize;

        if (workerThreadpoolSizeUsingProcessor) {
            workerThreadpoolSize = Runtime.getRuntime().availableProcessors();
        }

        nwElementColumnAttrs = Stream
                .of(nwElementSysIdAttrKey, nwElementVendorNameAttrKey, nwElementSoftwareVersionAttrKey,
                        nwElementLocationNameAttrKey)
                .collect(Collectors.toSet());

        if (nwelementJcbKeysStr != null && nwelementJcbKeysStr.trim().isEmpty() == false) {
            nwelementJcbKeys = Stream
                    .of(nwelementJcbKeysStr.trim().split(","))
                    .collect(Collectors.toSet());
        } else {
            nwelementJcbKeys = new HashSet<>(0);
        }

        if (timeZoneStr != null && timeZoneStr.isEmpty() == false && timeZoneStr.equalsIgnoreCase("default") == false) {
            timeZone = TimeZone.getTimeZone(timeZoneStr);
        } else {
            timeZone = TimeZone.getDefault();
        }
        Constants.TIME_ZONE = timeZone;

        StringBuilder logBuilder = new StringBuilder("CM Enrichment Properties : ");
        logBuilder.append("Constants[Constants.BATCH_SIZE=");
        logBuilder.append(Constants.BATCH_SIZE);
        logBuilder.append("Constants.TIME_ZONE=");
        logBuilder.append(Constants.TIME_ZONE);
        logBuilder.append("], ");
        logBuilder.append(toString());
        logger.info(logBuilder.toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public int getDbBatchSize() {
        return dbBatchSize;
    }

    public void setDbBatchSize(int dbBatchSize) {
        this.dbBatchSize = dbBatchSize;
    }

    public String getClientURL() {
        return clientURL;
    }

    public void setClientURL(String clientURL) {
        this.clientURL = clientURL;
    }

    public String getClientDomain() {
        return clientDomain;
    }

    public void setClientDomain(String clientDomain) {
        this.clientDomain = clientDomain;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public long getClientWaitTime() {
        return clientWaitTime;
    }

    public void setClientWaitTime(long clientWaitTime) {
        this.clientWaitTime = clientWaitTime;
    }

    public String getConfigSubDomain() {
        return configSubDomain;
    }

    public void setConfigSubDomain(String configSubDomain) {
        this.configSubDomain = configSubDomain;
    }

    public String getSwSubDomain() {
        return swSubDomain;
    }

    public void setSwSubDomain(String swSubDomain) {
        this.swSubDomain = swSubDomain;
    }

    public String getHwSubDomain() {
        return hwSubDomain;
    }

    public void setHwSubDomain(String hwSubDomain) {
        this.hwSubDomain = hwSubDomain;
    }

    public String getFwSubDomain() {
        return fwSubDomain;
    }

    public void setFwSubDomain(String fwSubDomain) {
        this.fwSubDomain = fwSubDomain;
    }

    public int getWorkerThreadpoolSize() {
        return workerThreadpoolSize;
    }

    public void setWorkerThreadpoolSize(int workerThreadpoolSize) {
        this.workerThreadpoolSize = workerThreadpoolSize;
    }

    public boolean isWorkerThreadpoolSizeUsingProcessor() {
        return workerThreadpoolSizeUsingProcessor;
    }

    public int getWorkerThreadQueueThreshold() {
        return workerThreadQueueThreshold;
    }

    public void setWorkerThreadQueueThreshold(int workerThreadQueueThreshold) {
        this.workerThreadQueueThreshold = workerThreadQueueThreshold;
    }

    public int getWorkerThreadQueueMaxCapacity() {
        return workerThreadQueueMaxCapacity;
    }

    public void setWorkerThreadQueueMaxCapacity(int workerThreadQueueMaxCapacity) {
        this.workerThreadQueueMaxCapacity = workerThreadQueueMaxCapacity;
    }

    public void setWorkerThreadpoolSizeUsingProcessor(boolean workerThreadpoolSizeUsingProcessor) {
        this.workerThreadpoolSizeUsingProcessor = workerThreadpoolSizeUsingProcessor;
    }

    public String getClientDomainKeyName() {
        return clientDomainKeyName;
    }

    public void setClientDomainKeyName(String clientDomainKeyName) {
        this.clientDomainKeyName = clientDomainKeyName;
    }

    public String getClientVersionKeyName() {
        return clientVersionKeyName;
    }

    public void setClientVersionKeyName(String clientVersionKeyName) {
        this.clientVersionKeyName = clientVersionKeyName;
    }

    public String getClientSubDomainKeyName() {
        return clientSubDomainKeyName;
    }

    public void setClientSubDomainKeyName(String clientSubDomainKeyName) {
        this.clientSubDomainKeyName = clientSubDomainKeyName;
    }

    public int getClientEmptyResponseRetry() {
        return clientEmptyResponseRetry;
    }

    public void setClientEmptyResponseRetry(int clientEmptyResponseRetry) {
        this.clientEmptyResponseRetry = clientEmptyResponseRetry;
    }

    public int getClientEmptyResponseRetryWaitTime() {
        return clientEmptyResponseRetryWaitTime;
    }

    public void setClientEmptyResponseRetryWaitTime(int clientEmptyResponseRetryWaitTime) {
        this.clientEmptyResponseRetryWaitTime = clientEmptyResponseRetryWaitTime;
    }

    public String getNwElementTypeName() {
        return nwElementTypeName;
    }

    public void setNwElementTypeName(String nwElementTypeName) {
        this.nwElementTypeName = nwElementTypeName;
    }

    public String getNwElementSysIdAttrKey() {
        return nwElementSysIdAttrKey;
    }

    public void setNwElementSysIdAttrKey(String nwElementSysIdAttrKey) {
        this.nwElementSysIdAttrKey = nwElementSysIdAttrKey;
    }

    public String getNwElementStatusAttrKey() {
        return nwElementStatusAttrKey;
    }

    public void setNwElementStatusAttrKey(String nwElementStatusAttrKey) {
        this.nwElementStatusAttrKey = nwElementStatusAttrKey;
    }

    public String getNwElementVendorNameAttrKey() {
        return nwElementVendorNameAttrKey;
    }

    public void setNwElementVendorNameAttrKey(String nwElementVendorNameAttrKey) {
        this.nwElementVendorNameAttrKey = nwElementVendorNameAttrKey;
    }

    public String getNwElementSoftwareVersionAttrKey() {
        return nwElementSoftwareVersionAttrKey;
    }

    public void setNwElementSoftwareVersionAttrKey(String nwElementSoftwareVersionAttrKey) {
        this.nwElementSoftwareVersionAttrKey = nwElementSoftwareVersionAttrKey;
    }

    public String getNwElementLocationNameAttrKey() {
        return nwElementLocationNameAttrKey;
    }

    public void setNwElementLocationNameAttrKey(String nwElementLocationNameAttrKey) {
        this.nwElementLocationNameAttrKey = nwElementLocationNameAttrKey;
    }

    public String getNwElementLatitudeAttrKey() {
        return nwElementLatitudeAttrKey;
    }

    public void setNwElementLatitudeAttrKey(String nwElementLatitudeAttrKey) {
        this.nwElementLatitudeAttrKey = nwElementLatitudeAttrKey;
    }

    public String getNwElementLongitudeAttrKey() {
        return nwElementLongitudeAttrKey;
    }

    public void setNwElementLongitudeAttrKey(String nwElementLongitudeAttrKey) {
        this.nwElementLongitudeAttrKey = nwElementLongitudeAttrKey;
    }

    public String getNwElementIdentifier() {
        return nwElementIdentifier;
    }

    public void setNwElementIdentifier(String nwElementIdentifierAttrKey) {
        nwElementIdentifier = nwElementIdentifierAttrKey;
    }

    public String getNwelementJcbKeysStr() {
        return nwelementJcbKeysStr;
    }

    public void setNwelementJcbKeysStr(String nwelementJcbKeysStr) {
        this.nwelementJcbKeysStr = nwelementJcbKeysStr;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getSourceJcbValue() {
        return sourceJcbValue;
    }

    public void setSourceJcbValue(String sourceJcbValue) {
        this.sourceJcbValue = sourceJcbValue;
    }

    public String getSourceCollectorValue() {
        return sourceCollectorValue;
    }

    public void setSourceCollectorValue(String sourceCollectorValue) {
        this.sourceCollectorValue = sourceCollectorValue;
    }

    public String getStatusActiveValue() {
        return statusActiveValue;
    }

    public void setStatusActiveValue(String statusActiveValue) {
        this.statusActiveValue = statusActiveValue;
    }

    public Set<String> getNwElementColumnAttrs() {
        return nwElementColumnAttrs;
    }

    public void setNwElementColumnAttrs(Set<String> nwElementColumnAttrs) {
        this.nwElementColumnAttrs = nwElementColumnAttrs;
    }

    public Set<String> getNwelementJcbKeys() {
        return nwelementJcbKeys;
    }

    public void setNwelementJcbKeys(Set<String> nwelementJcbKeys) {
        this.nwelementJcbKeys = nwelementJcbKeys;
    }

    public String getTimeZoneStr() {
        return timeZoneStr;
    }

    public void setTimeZoneStr(String timeZoneStr) {
        this.timeZoneStr = timeZoneStr;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public int getConfigClientCount() {
        return configClientCount;
    }

    public void setConfigClientCount(int configClientCount) {
        this.configClientCount = configClientCount;
    }

    public int getSwClientCount() {
        return swClientCount;
    }

    public void setSwClientCount(int swClientCount) {
        this.swClientCount = swClientCount;
    }

    public int getHwClientCount() {
        return hwClientCount;
    }

    public void setHwClientCount(int hwClientCount) {
        this.hwClientCount = hwClientCount;
    }

    public int getFwClientCount() {
        return fwClientCount;
    }

    public void setFwClientCount(int fwClientCount) {
        this.fwClientCount = fwClientCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AppProperties [dbBatchSize=");
        builder.append(dbBatchSize);
        builder.append(", clientURL=");
        builder.append(clientURL);
        builder.append(", clientDomain=");
        builder.append(clientDomain);
        builder.append(", clientVersion=");
        builder.append(clientVersion);
        builder.append(", clientWaitTime=");
        builder.append(clientWaitTime);
        builder.append(", configSubDomain=");
        builder.append(configSubDomain);
        builder.append(", swSubDomain=");
        builder.append(swSubDomain);
        builder.append(", hwSubDomain=");
        builder.append(hwSubDomain);
        builder.append(", fwSubDomain=");
        builder.append(fwSubDomain);
        builder.append(", nwElementTypeName=");
        builder.append(nwElementTypeName);
        builder.append(", nwElementSysIdAttrKey=");
        builder.append(nwElementSysIdAttrKey);
        builder.append(", nwElementStatusAttrKey=");
        builder.append(nwElementStatusAttrKey);
        builder.append(", nwElementVendorNameAttrKey=");
        builder.append(nwElementVendorNameAttrKey);
        builder.append(", nwElementSoftwareVersionAttrKey=");
        builder.append(nwElementSoftwareVersionAttrKey);
        builder.append(", nwElementLocationNameAttrKey=");
        builder.append(nwElementLocationNameAttrKey);
        builder.append(", nwElementLatitudeAttrKey=");
        builder.append(nwElementLatitudeAttrKey);
        builder.append(", nwElementLongitudeAttrKey=");
        builder.append(nwElementLongitudeAttrKey);
        builder.append(", nwElementIdentifier=");
        builder.append(nwElementIdentifier);
        builder.append(", workerThreadpoolSize=");
        builder.append(workerThreadpoolSize);
        builder.append(", workerThreadpoolSizeUsingProcessor=");
        builder.append(workerThreadpoolSizeUsingProcessor);
        builder.append(", workerThreadQueueThreshold=");
        builder.append(workerThreadQueueThreshold);
        builder.append(", workerThreadQueueMaxCapacity=");
        builder.append(workerThreadQueueMaxCapacity);
        builder.append(", clientDomainKeyName=");
        builder.append(clientDomainKeyName);
        builder.append(", clientVersionKeyName=");
        builder.append(clientVersionKeyName);
        builder.append(", clientSubDomainKeyName=");
        builder.append(clientSubDomainKeyName);
        builder.append(", clientEmptyResponseRetry=");
        builder.append(clientEmptyResponseRetry);
        builder.append(", clientEmptyResponseRetryWaitTime=");
        builder.append(clientEmptyResponseRetryWaitTime);
        builder.append(", nwelementJcbKeysStr=");
        builder.append(nwelementJcbKeysStr);
        builder.append(", sourceKey=");
        builder.append(sourceKey);
        builder.append(", sourceJcbValue=");
        builder.append(sourceJcbValue);
        builder.append(", sourceCollectorValue=");
        builder.append(sourceCollectorValue);
        builder.append(", statusActiveValue=");
        builder.append(statusActiveValue);
        builder.append(", timeZoneStr=");
        builder.append(timeZoneStr);
        builder.append(", configClientCount=");
        builder.append(configClientCount);
        builder.append(", swClientCount=");
        builder.append(swClientCount);
        builder.append(", hwClientCount=");
        builder.append(hwClientCount);
        builder.append(", fwClientCount=");
        builder.append(fwClientCount);
        builder.append(", nwElementColumnAttrs=");
        builder.append(nwElementColumnAttrs);
        builder.append(", nwelementJcbKeys=");
        builder.append(nwelementJcbKeys);
        builder.append(", timeZone=");
        builder.append(timeZone);
        builder.append("]");
        return builder.toString();
    }
}
