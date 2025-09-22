package com.example.java_aes_gcm_api;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class CryptoService {

    private static final int GCM_TAG_LENGTH_BITS = 128; // 128 bits = 16 bytes
    private static final int IV_LENGTH_BYTES = 12; // 96 bits = 12 bytes
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 從原始位元組創建 SecretKey。
     * @param keyBytes 原始密鑰位元組，長度需為 16, 24 或 32 bytes。
     * @return SecretKey 物件。
     */
    private SecretKey getSecretKey(byte[] keyBytes) {
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length.");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 從字串（例如明文密鑰字串）直接轉成 SecretKey，透過字串的 UTF-8 bytes。
     * @param keyString 明文密鑰字串，UTF-8 bytes 需為 16, 24, 或 32 bytes。
     * @return SecretKey 物件。
     */
    private SecretKey getSecretKeyFromString(String keyString) {
        byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
        return getSecretKey(keyBytes);
    }


    /**
     * 產生安全的隨機 IV (Initialization Vector)。
     * @return 隨機生成的 IV。
     */
    private byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        secureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * 加密明文，並將結果 Base64 編碼後再 Hex 編碼。
     * @param plainText 待加密的明文。
     * @param keyString 用於加密的密鑰。
     * @param charset 明文字元集。
     * @return Hex 編碼的加密結果（IV + 密文 + TAG）。
     * @throws Exception 如果加密失敗。
     */
    public String encrypt(String plainText, String keyString, Charset charset) throws Exception {
        SecretKey secretKey = getSecretKeyFromString(keyString);
        byte[] plainTextBytes = plainText.getBytes(charset);
        byte[] iv = generateIv();

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] cipherTextWithTag = cipher.doFinal(plainTextBytes);

        // 將 IV 和密文/TAG 組合成單一陣列
        byte[] encryptedPayload = ByteBuffer.allocate(IV_LENGTH_BYTES + cipherTextWithTag.length)
                .put(iv)
                .put(cipherTextWithTag)
                .array();

        // 步驟1: Base64 編碼
        String base64String = Base64.getEncoder().encodeToString(encryptedPayload);

        // 步驟2: Hex 編碼
        return Hex.encodeHexString(base64String.getBytes(charset));
    }

    /**
     * 解密 Hex 編碼的密文。
     * @param hexCipherText Hex 編碼的密文。
     * @param keyString 用於解密的密鑰。
     * @param charset 解密後明文的字元集。
     * @return 解密後的明文。
     * @throws Exception 如果解密失敗。
     */
    public String decrypt(String hexCipherText, String keyString, Charset charset) throws Exception {
        SecretKey secretKey = getSecretKeyFromString(keyString);

        // 步驟1: Hex 解碼
        byte[] base64Bytes = Hex.decodeHex(hexCipherText);
        String base64String = new String(base64Bytes, charset);

        // 步驟2: Base64 解碼
        byte[] encryptedPayload = Base64.getDecoder().decode(base64String);

        // 分割 IV 和密文/TAG
        if (encryptedPayload.length < IV_LENGTH_BYTES) {
            throw new IllegalArgumentException("Encrypted data is too short.");
        }
        byte[] iv = Arrays.copyOfRange(encryptedPayload, 0, IV_LENGTH_BYTES);
        byte[] cipherTextWithTag = Arrays.copyOfRange(encryptedPayload, IV_LENGTH_BYTES, encryptedPayload.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] decryptedTextBytes = cipher.doFinal(cipherTextWithTag);

        return new String(decryptedTextBytes, charset);
    }

}