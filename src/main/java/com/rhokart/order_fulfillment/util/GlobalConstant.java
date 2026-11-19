package com.rhokart.order_fulfillment.util;


import java.time.Instant;

public class GlobalConstant {

    public static final String ORDERID_PREFIX = "ord_";
    public static final String ORDERDTL_PREFIX ="dtl_";
    public static final String DRAFT_STATUS = "DRAFT";
    public static final String EMPTY_STRING = "";
    public static final String PENDING_STATUS = "PENDING";
    public static final Integer VALUE_ZERO = 0;
    public static final int ERROR_CODE = 16;
    public static final int ERROR_STATUS = 1600;
    public static final String SYSTEM_USER = "SYSTEM";
    public static final String ORDERED_STATUS = "ORDERED"; // Referring to ORDERED
    public static final String PAID_STATUS = "PAID";
    public static final String COMPLETED_STATUS = "COMPLETED";
    public static final String UNIQUE_ID_BY_EPOC = String.valueOf(Instant.now().toEpochMilli());
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String FAIL_STATUS = "FAIL";

}