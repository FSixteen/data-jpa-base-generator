package io.github.fsixteen.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 解密工具.<br>
 *
 * @author FSixteen
 * @since 1.0.1
 */
public class DecodingUtils {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final byte[] HEX_DIGITS = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12,
        13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15 };

    private static final int AES_BLOCK_SIZE = 16;

    private static final int GCM_IV_LENGTH = 12;

    private static final int GCM_TAG_LENGTH = 128;

    private static final int MAC_LENGTH = 32;

    private DecodingUtils() {
    }

    /**
     * 安全擦除敏感数据.<br>
     * 将数组内容填充为0, 防止敏感信息留在内存中.
     * 
     * @param data 要擦除的数据
     */
    public static void wipe(byte[] data) {
        if (data != null) {
            Arrays.fill(data, (byte) 0);
        }
    }

    /**
     * 安全擦除敏感字符数据.<br>
     * 
     * @param data 要擦除的数据
     */
    public static void wipe(char[] data) {
        if (data != null) {
            Arrays.fill(data, '\0');
        }
    }

    /**
     * 计算HMAC-SHA256.<br>
     * 
     * @param data 数据
     * @param key  密钥
     * @return HMAC值
     * @throws GeneralSecurityException 如果计算失败
     */
    public static byte[] hmacSha256(byte[] data, byte[] key) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }

    /**
     * 从 十六进制 转回.
     *
     * @param data Hex 数据
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
        if (data.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even.");
        }
        return new String(fromHex(data.getBytes(StandardCharsets.US_ASCII)), charset);
    }

    /**
     * 从 十六进制 转回.
     *
     * @param data Hex BYTE 数据
     * @return byte数组
     */
    public static byte[] fromHex(byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Hex byte array length must be even.");
        }
        final byte[] bytes = new byte[data.length / 2];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            byte high = HEX_DIGITS[data[j++]];
            byte low = HEX_DIGITS[data[j++]];
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character.");
            }
            bytes[i] = (byte) ((high << 4) | low);
        }
        return bytes;
    }

    /**
     * 从 BASE64 转回.
     *
     * @param data BASE64 数据
     * @return String
     */
    public static String fromBase64(final String data) {
        return fromBase64(data, DEFAULT_CHARSET);
    }

    /**
     * 从 BASE64 转回.
     *
     * @param data    BASE64 数据
     * @param charset 编码方式
     * @return String
     */
    public static String fromBase64(final String data, final Charset charset) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(fromBase64(data.getBytes(DEFAULT_CHARSET)), Objects.nonNull(charset) ? charset : DEFAULT_CHARSET);
    }

    /**
     * 从 BASE64 转回.
     *
     * @param data BASE64 BYTE 数据
     * @return byte数组
     */
    public static byte[] fromBase64(final byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return new byte[] {};
        }
        return Base64.getDecoder().decode(data);
    }

    /**
     * 验证AES密钥长度是否合法.<br>
     * AES密钥长度必须为16字节(AES-128)、24字节(AES-192)或32字节(AES-256).
     * 
     * @param key 密钥
     * @throws InvalidKeyException 如果密钥长度无效
     */
    public static void validateAesKey(byte[] key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("AES key cannot be null");
        }
        int length = key.length;
        if (length != 16 && length != 24 && length != 32) {
            throw new InvalidKeyException(
                "Invalid AES key length: " + length + " bytes. " + "AES key must be 16 bytes(AES-128), 24 bytes(AES-192), or 32 bytes(AES-256)");
        }
    }

    /**
     * 从 AES加密 转回(ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #fromAESCbc(byte[], byte[])}或{@link #fromAESGcm(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #fromAESCbc(byte[], byte[])}或{@link #fromAESGcm(byte[], byte[])}替代
     */
    @Deprecated
    public static byte[] fromAES(final String data, final byte[] key) throws GeneralSecurityException {
        return fromAES(fromBase64(data.getBytes(DEFAULT_CHARSET)), key);
    }

    /**
     * 从 AES加密 转回(ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #fromAESCbc(byte[], byte[])}或{@link #fromAESGcm(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #fromAESCbc(byte[], byte[])}或{@link #fromAESGcm(byte[], byte[])}替代
     */
    @Deprecated
    public static byte[] fromAES(final byte[] data, final byte[] key) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * 从 AES加密 转回(ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #fromAESCbc(byte[], int, int, byte[])}或{@link #fromAESGcm(byte[], int, int, byte[])}.
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #fromAESCbc(byte[], int, int, byte[])}或{@link #fromAESGcm(byte[], int, int, byte[])}替代
     */
    @Deprecated
    public static byte[] fromAES(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return fromAES(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 从 AES加密 转回(ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #fromAESCbcString(byte[], byte[])}或{@link #fromAESGcmString(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #fromAESCbcString(byte[], byte[])}或{@link #fromAESGcmString(byte[], byte[])}替代
     */
    @Deprecated
    public static String fromAESString(final String data, final byte[] key) throws GeneralSecurityException {
        return fromAESString(fromBase64(data.getBytes(DEFAULT_CHARSET)), key);
    }

    /**
     * 从 AES加密 转回(ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #fromAESCbcString(byte[], byte[])}或{@link #fromAESGcmString(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #fromAESCbcString(byte[], byte[])}或{@link #fromAESGcmString(byte[], byte[])}替代
     */
    @Deprecated
    public static String fromAESString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(fromAES(data, key), DEFAULT_CHARSET);
    }

    /**
     * 从 AES加密 转回(ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #fromAESCbcString(byte[], int, int, byte[])}或{@link #fromAESGcmString(byte[], int, int, byte[])}.
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #fromAESCbcString(byte[], int, int, byte[])}或{@link #fromAESGcmString(byte[], int, int, byte[])}替代
     */
    @Deprecated
    public static String fromAESString(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return fromAESString(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 从 AES-CBC加密 转回(带HMAC认证验证).<br>
     * 输入格式：[IV(16)+ 密文 + HMAC(32)]
     * 
     * @param data 数据(IV + 密文 + HMAC)
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESCbc(final String data, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? fromAESCbc(data.getBytes(DEFAULT_CHARSET), key) : null;
    }

    /**
     * 从 AES-CBC加密 转回(带HMAC认证验证).<br>
     * 输入格式：[IV(16)+ 密文 + HMAC(32)]
     * 
     * @param data 数据(IV + 密文 + HMAC)
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESCbc(final byte[] data, final byte[] key) throws GeneralSecurityException {
        if (Objects.isNull(data) || data.length < AES_BLOCK_SIZE + MAC_LENGTH) {
            throw new IllegalArgumentException("Invalid CBC encrypted data.");
        }

        int dataEnd = data.length - MAC_LENGTH;
        byte[] ivPlusCipher = Arrays.copyOfRange(data, 0, dataEnd);
        byte[] receivedMac = Arrays.copyOfRange(data, dataEnd, data.length);

        byte[] computedMac = hmacSha256(ivPlusCipher, key);
        if (!Arrays.equals(receivedMac, computedMac)) {
            throw new GeneralSecurityException("HMAC verification failed - data may have been tampered with");
        }

        byte[] iv = Arrays.copyOfRange(ivPlusCipher, 0, AES_BLOCK_SIZE);
        byte[] encrypted = Arrays.copyOfRange(ivPlusCipher, AES_BLOCK_SIZE, ivPlusCipher.length);

        return fromAESCbc(encrypted, iv, key);
    }

    /**
     * 从 AES-CBC加密 转回(带HMAC认证验证).<br>
     * 输入格式：[IV(16)+ 密文 + HMAC(32)]
     * 
     * @param data  数据(IV + 密文 + HMAC)
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESCbc(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return fromAESCbc(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 从 AES-CBC加密 转回(使用指定IV).<br>
     * 
     * @param data 密文
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESCbc(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? fromAESCbc(data.getBytes(DEFAULT_CHARSET), iv, key) : null;
    }

    /**
     * 从 AES-CBC加密 转回(使用指定IV).<br>
     * 
     * @param data 密文
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESCbc(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        return cipher.doFinal(data);
    }

    /**
     * 从 AES-CBC加密 转回(使用指定IV).<br>
     * 
     * @param data  密文
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESCbc(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return fromAESCbc(Arrays.copyOfRange(data, start, end), iv, key);
    }

    /**
     * 从 AES-CBC加密 转回(带HMAC认证验证).<br>
     * 
     * @param data 数据(Base64编码的IV + 密文 + HMAC)
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESCbcString(final String data, final byte[] key) throws GeneralSecurityException {
        return fromAESCbcString(fromBase64(data.getBytes(DEFAULT_CHARSET)), key);
    }

    /**
     * 从 AES-CBC加密 转回(带HMAC认证验证).<br>
     * 
     * @param data 数据(IV + 密文 + HMAC)
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESCbcString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(fromAESCbc(data, key), DEFAULT_CHARSET);
    }

    /**
     * 从 AES-CBC加密 转回(带HMAC认证验证).<br>
     * 
     * @param data  数据(IV + 密文 + HMAC)
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESCbcString(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return fromAESCbcString(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 从 AES-CBC加密 转回(使用指定IV).<br>
     * 
     * @param data 数据(Base64编码的密文)
     * @param iv   初始化向量
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESCbcString(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return fromAESCbcString(fromBase64(data.getBytes(DEFAULT_CHARSET)), iv, key);
    }

    /**
     * 从 AES-CBC加密 转回(使用指定IV).<br>
     * 
     * @param data 密文
     * @param iv   初始化向量
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESCbcString(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return new String(fromAESCbc(data, iv, key), DEFAULT_CHARSET);
    }

    /**
     * 从 AES-CBC加密 转回(使用指定IV).<br>
     * 
     * @param data  密文
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESCbcString(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key)
        throws GeneralSecurityException {
        return fromAESCbcString(Arrays.copyOfRange(data, start, end), iv, key);
    }

    /**
     * 从 AES-GCM加密 转回.<br>
     * 输入格式：[IV(12)+ 密文 + 认证标签]
     * 
     * @param data 数据(IV + 密文 + 认证标签)
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final String data, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? fromAESGcm(data.getBytes(DEFAULT_CHARSET), key) : null;
    }

    /**
     * 从 AES-GCM加密 转回.<br>
     * 输入格式：[IV(12)+ 密文 + 认证标签]
     * 
     * @param data 数据(IV + 密文 + 认证标签)
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final byte[] data, final byte[] key) throws GeneralSecurityException {
        if (Objects.isNull(data) || data.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Invalid GCM encrypted data.");
        }
        byte[] iv = Arrays.copyOfRange(data, 0, GCM_IV_LENGTH);
        byte[] encrypted = Arrays.copyOfRange(data, GCM_IV_LENGTH, data.length);
        return fromAESGcm(encrypted, iv, key);
    }

    /**
     * 从 AES-GCM加密 转回.<br>
     * 输入格式：[IV(12)+ 密文 + 认证标签]
     * 
     * @param data  数据(IV + 密文 + 认证标签)
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return fromAESGcm(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV).<br>
     * 
     * @param data 密文(含认证标签)
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? fromAESGcm(data.getBytes(DEFAULT_CHARSET), iv, key) : null;
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV).<br>
     * 
     * @param data 密文(含认证标签)
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
        return cipher.doFinal(data);
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV, 带额外认证数据).<br>
     * 
     * @param data 密文(含认证标签)
     * @param iv   初始化向量
     * @param key  密钥
     * @param aad  额外认证数据(可为null)
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final byte[] data, final byte[] iv, final byte[] key, final byte[] aad) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);
        if (aad != null && aad.length > 0) {
            cipher.updateAAD(aad);
        }
        return cipher.doFinal(data);
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV).<br>
     * 
     * @param data  密文(含认证标签)
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] fromAESGcm(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return fromAESGcm(Arrays.copyOfRange(data, start, end), iv, key);
    }

    /**
     * 从 AES-GCM加密 转回.<br>
     * 
     * @param data 数据(Base64编码的IV + 密文 + 认证标签)
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESGcmString(final String data, final byte[] key) throws GeneralSecurityException {
        return fromAESGcmString(fromBase64(data.getBytes(DEFAULT_CHARSET)), key);
    }

    /**
     * 从 AES-GCM加密 转回.<br>
     * 
     * @param data 数据(IV + 密文 + 认证标签)
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESGcmString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(fromAESGcm(data, key), DEFAULT_CHARSET);
    }

    /**
     * 从 AES-GCM加密 转回.<br>
     * 
     * @param data  数据(IV + 密文 + 认证标签)
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESGcmString(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return fromAESGcmString(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV).<br>
     * 
     * @param data 数据(Base64编码的密文 + 认证标签)
     * @param iv   初始化向量
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESGcmString(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return fromAESGcmString(fromBase64(data.getBytes(DEFAULT_CHARSET)), iv, key);
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV).<br>
     * 
     * @param data 密文(含认证标签)
     * @param iv   初始化向量
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESGcmString(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return new String(fromAESGcm(data, iv, key), DEFAULT_CHARSET);
    }

    /**
     * 从 AES-GCM加密 转回(使用指定IV).<br>
     * 
     * @param data  密文(含认证标签)
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String fromAESGcmString(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key)
        throws GeneralSecurityException {
        return fromAESGcmString(Arrays.copyOfRange(data, start, end), iv, key);
    }

}