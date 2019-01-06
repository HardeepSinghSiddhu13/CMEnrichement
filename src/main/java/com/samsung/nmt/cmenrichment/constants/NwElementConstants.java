package com.samsung.nmt.cmenrichment.constants;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NwElementConstants {
    public static String TYPE = "ENODEB";
    public static String SYS_ID = "sysId";
    public static String STATUS = "status";
    public static String VENDOR_NAME = "vendorName";
    public static String SW_VERSION = "swVersion";
    public static String LOCATION_NAME = "locationName";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String IDENTIFIER = "ManagedElement";
    public static String CM_SOURCE_KEY_NAME = "cmdata-src";
    public static String JCB_SOURCE_VALUE = "j";
    public static String COLLECTOR_SOURCE_VALUE = "c";

    public static Set<String> COLUMN_ATTRS = Stream.of(SYS_ID, VENDOR_NAME, SW_VERSION, LOCATION_NAME)
            .collect(Collectors.toSet());

    public static Set<String> IMMUTABLE_ATTRS = Stream
            .of(LATITUDE, LONGITUDE)
            .collect(Collectors.toSet());
}
