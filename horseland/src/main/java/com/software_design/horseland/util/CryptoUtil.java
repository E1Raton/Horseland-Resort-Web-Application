package com.software_design.horseland.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CryptoUtil {
    @Value("${ENCRYPTION_KEY}")
    private String key;

    private SecretKeySpec getKeySpec() {
        return new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKeySpec());
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getKeySpec());
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}
