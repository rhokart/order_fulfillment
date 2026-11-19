package com.rhokart.order_fulfillment.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rhokart.order_fulfillment.dto.request.CheckoutRequest;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GlobalValidator {
    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 10000 + random.nextInt(90000); // Generates number between 10000 and 99999
        return String.valueOf(otp);
    }

    public static String convertToWords(long number) {
        if (number == 0) return "Zero";

        StringBuilder words = new StringBuilder();

        if (number / 10000000 > 0) {
            words.append(convertToWords(number / 10000000)).append(" Crore ");
            number %= 10000000;
        }
        if (number / 100000 > 0) {
            words.append(convertToWords(number / 100000)).append(" Lakh ");
            number %= 100000;
        }
        if (number / 1000 > 0) {
            words.append(convertToWords(number / 1000)).append(" Thousand ");
            number %= 1000;
        }
        if (number / 100 > 0) {
            words.append(convertToWords(number / 100)).append(" Hundred ");
            number %= 100;
        }
        if (number > 0) {
            if (number < 20) {
                words.append(units[(int) number]);
            } else {
                words.append(tens[(int) (number / 10)]);
                if (number % 10 > 0) {
                    words.append(" ").append(units[(int) (number % 10)]);
                }
            }
        }
        return words.toString().trim();
    }

    public static String convertRupeesToWords(double amount) {
        long rupees = (long) amount;
        long paise = Math.round((amount - rupees) * 100);

        String result = convertToWords(rupees) + " Rupees";
        if (paise > 0) {
            result += " and " + convertToWords(paise) + " Paise";
        }
        return result + " Only";
    }

    public static boolean isValidCheckout(CheckoutRequest checkoutRequest){
        //Input data validator
        AtomicBoolean FLAG = new AtomicBoolean(Boolean.TRUE);
        AtomicInteger  totalCalculatedPayableAmount = new AtomicInteger(0);
        checkoutRequest.getCheckoutItems().forEach( detail -> {
            AtomicInteger totalAmount = new AtomicInteger();
            detail.getSkus().forEach( sku -> totalAmount.set(totalAmount.get() + sku.getTotalAmount()));

            if(!Objects.equals(totalAmount.get(),detail.getOrderAmountIncludesGst()))
                FLAG.set(Boolean.FALSE);

            Integer calculatePayableAmount = totalAmount.get() + detail.getDeliveryCharges() - detail.getOfferAmount() - detail.getDiscount();
            if(!calculatePayableAmount.equals(detail.getPayableAmount()))
                FLAG.set(Boolean.FALSE);

            totalCalculatedPayableAmount.addAndGet(calculatePayableAmount);

            if(checkoutRequest.getUserId().isEmpty() || Objects.isNull(checkoutRequest.getUserId()))
                FLAG.set(Boolean.FALSE);;

            if(Objects.isNull(detail.getCartId()) || detail.getCartId() < 100)
                FLAG.set(Boolean.FALSE);;

            if(Objects.isNull(detail.getDeliveryAddress()))
                FLAG.set(Boolean.FALSE);
        });

        if(checkoutRequest.getTotalPayableAmount() != totalCalculatedPayableAmount.get())
            return Boolean.FALSE;

        return FLAG.get();
    }

    public static BufferedImage generateQRCode(String url) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 300, 300);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }


}
