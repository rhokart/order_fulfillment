package com.rhokart.order_fulfillment.dto.response;

import com.rhokart.datamodel.dto.ResponseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadInvoiceResponse {
    private String invoiceUrl;
}
