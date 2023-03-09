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

    public static final Supplier<NotFoundException> productTypeNotFound = () -> new NotFoundException("Product Type not found");
    public static final Supplier<NotFoundException> userNotFound = () -> new NotFoundException("User not found");
    public static final Supplier<NotFoundException> orderNotFound = () -> new NotFoundException("Order not found");
    public static final Supplier<NotFoundException> shopNotFound = () -> new NotFoundException("Shop not found");
    public static final Supplier<NotFoundException> voucherNotFound = () -> new NotFoundException("Voucher not found");
    public static final Supplier<NotFoundException> addressNotFound = () -> new NotFoundException("Voucher not found");

}
