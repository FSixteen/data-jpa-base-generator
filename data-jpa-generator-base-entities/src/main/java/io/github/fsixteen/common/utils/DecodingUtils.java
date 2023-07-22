package io.github.fsixteen.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DecodingUtils {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final byte HEX_DIGITS[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12,
        13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15 };

    private DecodingUtils() {
    }

    /**
     * 从 十六进制 转回.
     *
     * @param data
     * @return String
     */
    public static String fromHex(String data) {
        return fromHex(data, DEFAULT_CHARSET);
    }

    /**
     * 从 十六进制 转回.
     *
     * @param data    Hex 数据
     * @param charset 编码方式
     * @return String
     */
    public static String fromHex(String data, Charset charset) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(fromHex(data.getBytes(StandardCharsets.US_ASCII)), charset);
    }

    /**
     * 从 十六进制 转回.
     *
     * @param data
     * @return byte数组
     */
    public static byte[] fromHex(byte[] data) {
        if (data == null) {
            return null;
        }
        final byte[] bytes = new byte[data.length / 2];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((HEX_DIGITS[data[j++]] << 4) | HEX_DIGITS[data[j++]]);
        }
        return bytes;
    }

    /**
     * 从 BASE64 转回.
     *
     * @param data
     * @return byte数组
     */
    public static String fromBase64(String data) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(fromBase64(data.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
    }

    /**
     * 从 BASE64 转回.
     *
     * @param data
     * @return byte数组
     */
    public static byte[] fromBase64(byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return new byte[] {};
        }
        return Base64.getDecoder().decode(data);
    }

    /**
     * 从 AES加密 转回.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException
     */
    public static byte[] fromAES(final String data, final byte[] key) throws GeneralSecurityException {
        return fromAES(fromBase64(data.getBytes(DEFAULT_CHARSET)), key);
    }

    /**
     * 从 AES加密 转回.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException
     */
    public static byte[] fromAES(final byte[] data, final byte[] key) throws GeneralSecurityException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * 从 AES加密 转回.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException
     */
    public static String fromAESString(final String data, final byte[] key) throws GeneralSecurityException {
        return fromAESString(fromBase64(data.getBytes(DEFAULT_CHARSET)), key);
    }

    /**
     * 从 AES加密 转回.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException
     */
    public static String fromAESString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(fromAES(data, key), DEFAULT_CHARSET);
    }

}
