package com.rhokart.order_fulfillment.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInfo {
        private String orderId;
        private String userId;
        private String sellerId;
        private LocalDateTime orderAt;
        private Integer subTotal; //excluded GST
        private Integer gst;
        private Integer deliveryCharges;
        private Integer discount;
        private Integer offer;
        private Integer totalAmount; // included GST
        private Integer totalUnits;
        private String paymentId;
}
