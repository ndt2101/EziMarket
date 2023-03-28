package com.ndt2101.ezimarket.constant;

import com.ndt2101.ezimarket.exception.NotFoundException;

import java.util.function.Supplier;

public class Common {
    public static final String SUCCESSFUL_RESPONSE = "Successful response";
    public static final String UNSUCCESSFUL_RESPONSE = "Unsuccessful response";
    public static final Integer PAGING_DEFAULT_LIMIT = 10;
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String ORDER_STATUS_IN_CART = "in_cart";
    public static final String ORDER_STATUS_CONFIRMING = "confirming";
    public static final String ORDER_STATUS_PICKING = "picking";
    public static final String ORDER_STATUS_PAYING = "paying";
    public static final String ORDER_STATUS_DELIVERING = "delivering";
    public static final String ORDER_STATUS_RECEIVED = "received";

    public static final String ORDER_STATUS_CANCELED = "canceled";
    public static final String CREATE_STORE_IN_GHN_API = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shop/register";
    public static final String CREATE_ORDER_IN_GHN_API = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create";
    public static final String CALCULATE_FEE_IN_GHN_API = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
    public static final String CANCEL_ORDER_IN_GHN_API = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/switch-status/cancel";

    public static final String GHN_TOKEN = "0a64e1dc-a1e6-11ed-b62e-2a5743127145";
    public static final String GHN_CONTENT_TYPE = "application/json";
    public static final Supplier<NotFoundException> productNotFound = () -> new NotFoundException("Product not found");

    public static final Supplier<NotFoundException> reportNotFound = () -> new NotFoundException("Report not found");

    public static final Supplier<NotFoundException> postNotFound = () -> new NotFoundException("Post not found");

    public static final Supplier<NotFoundException> productTypeNotFound = () -> new NotFoundException("Product Type not found");
    public static final Supplier<NotFoundException> userNotFound = () -> new NotFoundException("User not found");
    public static final Supplier<NotFoundException> orderNotFound = () -> new NotFoundException("Order not found");
    public static final Supplier<NotFoundException> shopNotFound = () -> new NotFoundException("Shop not found");
    public static final Supplier<NotFoundException> voucherNotFound = () -> new NotFoundException("Voucher not found");
    public static final Supplier<NotFoundException> addressNotFound = () -> new NotFoundException("Voucher not found");
    public static final Supplier<NotFoundException> paymentMethodNotFound = () -> new NotFoundException("Payment method not found");


}
