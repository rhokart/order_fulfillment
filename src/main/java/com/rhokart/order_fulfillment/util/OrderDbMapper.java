package com.rhokart.order_fulfillment.util;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface OrderDbMapper {

    String value() default "";
}


