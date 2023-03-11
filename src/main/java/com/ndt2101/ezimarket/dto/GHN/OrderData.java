package com.ndt2101.ezimarket.dto.GHN;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderData {
    private int code;
    private String code_message_value;
    private OrderDetail data;
    private String message;
    private String message_display;

    // Constructor, getters, and setters
}




