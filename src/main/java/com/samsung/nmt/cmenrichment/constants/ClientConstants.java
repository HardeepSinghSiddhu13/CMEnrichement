package com.samsung.nmt.cmenrichment.constants;

public class ClientConstants {
    public static String CLIENT_URL = "http://localhost:8080/cmsim/cm/inventory";
    public static String DOMAIN = "CM";
    public static String VERSION = "V1";
    public static long WAIT_TIME = 2000;

    public static String CLIENT_DOMAIN_KEY_NAME = "domain";
    public static String CLIENT_VERSION_KEY_NAME = "version";
    public static String CLIENT_SUBDOMAIN_KEY_NAME = "subDomain";

    public static int CLIENT_EMPTY_RESPONSE_RETRY = 10;
    public static long CLIENT_EMPTY_RESPONSE_RETRY_WAIT_TIME = 10000;

    public static final class SubDomain {
        public static String CONFIG = "CONFIG";
        public static String HW = "HW";
        public static String FW = "FW";
        public static String SW = "SW";
        public static String TOTPOLOGY = "TOTPOLOGY";
    }

}
