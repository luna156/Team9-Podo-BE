package com.softeer.podo.common.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES 암호화를 위한 유틸리티 클래스
 * @Value로 secret key 정보를 받아야 하고, 이는 내부 상태에 의존적이라는 의미이므로 컴포넌트로 관리
 */
@Component
public class AESUtils {
    private static final String ALGORITHM = "AES";

    @Value("${secret.jwt}")
    private String baseSecretKey;

    private static String STATIC_BASE_SECRET_KEY;

    @PostConstruct
    public void init() {
        STATIC_BASE_SECRET_KEY = this.baseSecretKey;
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, makeSecretKeyByString(STATIC_BASE_SECRET_KEY));
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, makeSecretKeyByString(STATIC_BASE_SECRET_KEY));
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

    // 특정 문자열을 기반으로 SecretKey 객체를 생성하는 메서드
    public SecretKey makeSecretKeyByString(String password) {
        byte[] keyBytes = password.getBytes(StandardCharsets.UTF_8);
        keyBytes = Arrays.copyOf(keyBytes, 16); // 128비트(16바이트)로 맞추기
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
