package io.github.fsixteen.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加密工具.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class EncodingUtils {
    private static final Logger LOG = LoggerFactory.getLogger(EncodingUtils.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final char[] HEX_CODE = "0123456789abcdef".toCharArray();

    private EncodingUtils() {
    }

    /**
     * 转 MD5.
     *
     * @param data 原始字符串内容
     * @return String
     */
    public static String toMd5(String data) {
        return Objects.nonNull(data) ? toMd5(data.getBytes(DEFAULT_CHARSET)) : null;
    }

    /**
     * 转 MD5.
     *
     * @param data 数据
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
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转 SHA256.
     *
     * @param data 数据
     * @return String
     */
    public static String toSHA256(String data) {
        return Objects.nonNull(data) ? toHex(toSHA256(data.getBytes(DEFAULT_CHARSET))) : null;
    }

    /**
     * 转 SHA256.
     *
     * @param data 数据
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
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 转十六进制.
     *
     * @param data 数据
     * @return String
     */
    public static String toHex(String data) {
        return toHex(data, DEFAULT_CHARSET);
    }

    /**
     * 转十六进制.
     *
     * @param data    数据
     * @param charset 编码方式
     * @return String
     */
    public static String toHex(String data, Charset charset) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return toHex(data.getBytes(charset));
    }

    /**
     * 转十六进制.
     *
     * @param data 数据
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
     * @param data 数据
     * @return String
     */
    public static String toBase64(String data) {
        return toBase64(data, DEFAULT_CHARSET);
    }

    /**
     * 转 BASE64.
     *
     * @param data    数据
     * @param charset 编码方式
     * @return String
     */
    public static String toBase64(String data, Charset charset) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(toBase64(data.getBytes(charset)), DEFAULT_CHARSET);
    }

    /**
     * 转 BASE64.
     *
     * @param data 数据
     * @return byte数组
     */
    public static byte[] toBase64(byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return data;
        }
        return Base64.getEncoder().encode(data);
    }

    /**
     * 转 AES加密.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAES(final String data, final byte[] key) throws GeneralSecurityException {
        return toAES(data.getBytes(DEFAULT_CHARSET), key);
    }

    /**
     * 转 AES加密.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAES(final byte[] data, final byte[] key) throws GeneralSecurityException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * 转 AES加密.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESString(final String data, final byte[] key) throws GeneralSecurityException {
        return toAESString(data.getBytes(DEFAULT_CHARSET), key);
    }

    /**
     * 转 AES加密.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(toBase64(toAES(data, key)), DEFAULT_CHARSET);
    }
}
