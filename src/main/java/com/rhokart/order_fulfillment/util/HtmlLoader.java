package com.rhokart.order_fulfillment.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class HtmlLoader {
    private final ResourceLoader resourceLoader;

    public HtmlLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    public String loadHtmlTemplate() {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/invoice.html");
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String loadSellerHtmlTemplate() {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/seller_invoice.html");
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
