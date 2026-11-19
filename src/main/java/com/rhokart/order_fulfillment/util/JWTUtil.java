package com.rhokart.order_fulfillment.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

public class JWTUtil {
    // A strong 256-bit (32-byte) Base64-encoded key
    private static final String BASE64_SECRET = "K4d9lBq7T1yXpS3zU2Rvq1+F9CmX8Lk7OcB2eJ6V0Ys=";

    private static SecretKey getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(BASE64_SECRET);
        return new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
    }

    public static String generateToken(String invoiceId) {
        return Jwts.builder()
                .setSubject("invoice")
                .claim("invoiceId", invoiceId)
                .setIssuedAt(new Date())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getInvoiceIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("invoiceId", String.class);
    }
}
