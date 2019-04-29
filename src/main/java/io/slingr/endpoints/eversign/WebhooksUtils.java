package io.slingr.endpoints.eversign;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class WebhooksUtils {
    private static final Logger logger = LoggerFactory.getLogger(WebhooksUtils.class);

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA256";
    private static final char[] HEX = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static boolean verifySignature(String payload, String signature, String secret) {
        boolean isValid = false;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(payload.getBytes());

            if(StringUtils.isBlank(signature) || signature.length() < 5){
                return false;
            }
            String expected = signature;
            String actual = new String(encode(rawHmac));
            isValid = expected.equals(actual);

        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException ex) {
            logger.error("Error validating webhook signature", ex);
        }

        return isValid;
    }

    private static char[] encode(byte[] bytes) {
        final int amount = bytes.length;
        char[] result = new char[2 * amount];
        int j = 0;
        for (int i = 0; i < amount; i++) {
            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4];
            result[j++] = HEX[(0x0F & bytes[i])];
        }
        return result;
    }

}
