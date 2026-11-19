package com.rhokart.order_fulfillment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {

    private String toAddress;
    private String msgBody;
    private String subject;
    private String filename;

}