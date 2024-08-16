package com.softeer.podo.common.utils;

import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;

public class MdcUtils {
    private static MDCAdapter mdc = MDC.getMDCAdapter();
    public static final String HEADER_MAP_MDC = "HEADER_MAP_MDC";
    public static final String PARAMETER_MAP_MDC = "PARAMETER_MAP_MDC";
    public static final String REQUEST_URI_MDC = "REQUEST_URI_MDC";
    public static final String BODY_MDC = "BODY_MDC";

    public static void putMdc(String key, String value) {
        mdc.put(key, value);
    }

    public static void setJsonValueAndPutMdc(String key, Object value) {
        if(value != null) {
            mdc.put(key, JsonUtils.toJson(value));
        }
    }

    public static String getFromMdc(String key) {
        return mdc.get(key);
    }

    public static void clear() {
        MDC.clear();
    }
}
