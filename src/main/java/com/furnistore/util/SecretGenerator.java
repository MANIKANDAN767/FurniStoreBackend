
package com.furnistore.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretGenerator {

    public static void main(String[] args) {

        // Generate 48 cryptographically secure random bytes
        byte[] secretBytes = new byte[48];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(secretBytes);

        // Convert bytes into Base64 string
        String secret = Base64.getEncoder()
                .encodeToString(secretBytes);

        System.out.println("Your new JWT Secret:");
        System.out.println(secret);
    }
}
