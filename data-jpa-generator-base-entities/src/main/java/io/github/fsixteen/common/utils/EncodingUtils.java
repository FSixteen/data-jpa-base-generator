package io.github.fsixteen.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncodingUtils {
    private static final Logger LOGGER = Logger.getLogger(EncodingUtils.class.getName());
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final char[] HEX_CODE = "0123456789abcdef".toCharArray();

    private EncodingUtils() {
    }

    /**
     * 转 MD5.
     *
     * @param data
     * @return String
     */
    public static String toMd5(String data) {
        if (Objects.isNull(data)) {
            return null;
        }
        return toMd5(data.getBytes(DEFAULT_CHARSET));
    }

    /**
     * 转 MD5.
     *
     * @param data
     * @return String
     */
    public static String toMd5(byte[] data) {
        if (Objects.isNull(data)) {
            return null;
        }
        try {
            StringBuilder out = new StringBuilder(32);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(data);
            for (byte b : digest) {
                int bf = b & 0xff;
                if (bf < 16) {
                    out.append('0');
                }
                out.append(Integer.toHexString(bf));
            }
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转 SHA256.
     *
     * @param data
     * @return String
     */
    public static String toSHA256(String data) {
        if (Objects.isNull(data)) {
            return null;
        }
        return toHex(toSHA256(data.getBytes(DEFAULT_CHARSET)));
    }

    /**
     * 转 SHA256.
     *
     * @param data
     * @return byte数组
     */
    public static byte[] toSHA256(byte[] data) {
        if (Objects.isNull(data)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转十六进制.
     *
     * @param data
     * @return String
     */
    public static String toHex(String data) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return toHex(data.getBytes(DEFAULT_CHARSET));
    }

    /**
     * 转十六进制.
     *
     * @param data
     * @return String
     */
    public static String toHex(byte[] data) {
        if (Objects.isNull(data)) {
            return null;
        }
        if (0 == data.length) {
            return "";
        }
        StringBuilder out = new StringBuilder(data.length * 2);
        for (byte b : data) {
            out.append(HEX_CODE[(b >> 4) & 0xF]);
            out.append(HEX_CODE[(b & 0xF)]);
        }
        return out.toString();
    }

    /**
     * 转 BASE64.
     *
     * @param data
     * @return String
     */
    public static String toBase64(String data) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(toBase64(data.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
    }

    /**
     * 转 BASE64.
     *
     * @param data
     * @return byte数组
     */
    public static byte[] toBase64(byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return data;
        }
        return Base64.getEncoder().encode(data);
    }
}
