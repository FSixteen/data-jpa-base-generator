package io.github.fsixteen.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
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

    private static final int AES_BLOCK_SIZE = 16;

    private static final int GCM_IV_LENGTH = 12;

    private static final int GCM_TAG_LENGTH = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private EncodingUtils() {
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
     * 生成随机盐值.<br>
     * 
     * @param length 盐值长度(建议至少16字节)
     * @return 随机盐值
     */
    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * 生成随机盐值(默认16字节).<br>
     * 
     * @return 16字节随机盐值
     */
    public static byte[] generateSalt() {
        return generateSalt(16);
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
     * 转 SHA512.
     *
     * @param data 原始字符串内容
     * @return String
     */
    public static String toSHA512(String data) {
        return Objects.nonNull(data) ? toHex(toSHA512(data.getBytes(DEFAULT_CHARSET))) : null;
    }

    /**
     * 转 SHA512.
     *
     * @param data 数据
     * @return byte数组
     */
    public static byte[] toSHA512(byte[] data) {
        if (Objects.isNull(data)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
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
    public static String toBase64(final String data, final Charset charset) {
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
    public static byte[] toBase64(final byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return data;
        }
        return Base64.getEncoder().encode(data);
    }

    /**
     * 生成随机初始化向量(IV).<br>
     *
     * @return byte[] 16字节的随机IV
     */
    public static byte[] generateIv() {
        return generateIv(AES_BLOCK_SIZE);
    }

    /**
     * 生成指定长度的随机初始化向量(IV).<br>
     *
     * @param length IV长度
     * @return byte[] 随机IV
     */
    public static byte[] generateIv(int length) {
        byte[] iv = new byte[length];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
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
                "Invalid AES key length: " + length + " bytes. " + "AES key must be 16 bytes (AES-128), 24 bytes (AES-192), or 32 bytes (AES-256)");
        }
    }

    /**
     * 使用PBKDF2从密码派生AES-256密钥(默认配置).<br>
     * 默认迭代次数为65536次.
     * 
     * @param password 密码
     * @param salt     盐值
     * @return 派生的256位密钥
     * @throws GeneralSecurityException 如果派生过程失败
     */
    public static byte[] deriveAesKey(char[] password, byte[] salt) throws GeneralSecurityException {
        return deriveAesKey(password, salt, 65536, 256);
    }

    /**
     * 使用PBKDF2从密码派生AES密钥.<br>
     * 
     * @param password   密码
     * @param salt       盐值(建议至少16字节)
     * @param iterations 迭代次数(建议至少10000次)
     * @param keyLength  密钥长度(128、192或256位)
     * @return 派生的密钥
     * @throws GeneralSecurityException 如果派生过程失败
     */
    public static byte[] deriveAesKey(char[] password, byte[] salt, int iterations, int keyLength) throws GeneralSecurityException {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (salt == null || salt.length == 0) {
            throw new IllegalArgumentException("Salt cannot be null or empty");
        }
        if (iterations <= 0) {
            throw new IllegalArgumentException("Iterations must be greater than 0");
        }
        if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
            throw new IllegalArgumentException("Key length must be 128, 192, or 256");
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKey secret = factory.generateSecret(spec);
        return secret.getEncoded();
    }

    /**
     * 转 AES加密 (ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #toAESCbc(byte[], byte[])}或{@link #toAESGcm(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #toAESCbc(byte[], byte[])}或{@link #toAESGcm(byte[], byte[])}替代
     */
    @Deprecated
    public static byte[] toAES(final String data, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? toAES(data.getBytes(DEFAULT_CHARSET), key) : null;
    }

    /**
     * 转 AES加密 (ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #toAESCbc(byte[], byte[])}或{@link #toAESGcm(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #toAESCbc(byte[], byte[])}或{@link #toAESGcm(byte[], byte[])}替代
     */
    @Deprecated
    public static byte[] toAES(final byte[] data, final byte[] key) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * 转 AES加密 (ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #toAESCbc(byte[], int, int, byte[])}或{@link #toAESGcm(byte[], int, int, byte[])}.
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return byte 数组
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #toAESCbc(byte[], int, int, byte[])}或{@link #toAESGcm(byte[], int, int, byte[])}替代
     */
    @Deprecated
    public static byte[] toAES(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return toAES(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 转 AES加密 (ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #toAESCbcString(byte[], byte[])}或{@link #toAESGcmString(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #toAESCbcString(byte[], byte[])}或{@link #toAESGcmString(byte[], byte[])}替代
     */
    @Deprecated
    public static String toAESString(final String data, final byte[] key) throws GeneralSecurityException {
        return toAESString(data.getBytes(DEFAULT_CHARSET), key);
    }

    /**
     * 转 AES加密 (ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #toAESCbcString(byte[], byte[])}或{@link #toAESGcmString(byte[], byte[])}.
     * 
     * @param data 数据
     * @param key  密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #toAESCbcString(byte[], byte[])}或{@link #toAESGcmString(byte[], byte[])}替代
     */
    @Deprecated
    public static String toAESString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(toBase64(toAES(data, key)), DEFAULT_CHARSET);
    }

    /**
     * 转 AES加密 (ECB模式, 已过时).<br>
     * ECB模式存在安全漏洞,
     * 建议使用{@link #toAESCbcString(byte[], int, int, byte[])}或{@link #toAESGcmString(byte[], int, int, byte[])}.
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return String
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     * @deprecated 此方法使用不安全的ECB模式,
     *             请使用{@link #toAESCbcString(byte[], int, int, byte[])}或{@link #toAESGcmString(byte[], int, int, byte[])}替代
     */
    @Deprecated
    public static String toAESString(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return toAESString(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 转 AES-CBC加密(带HMAC认证).<br>
     * 输出格式：[IV(16) + 密文 + HMAC(32)]
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组(IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESCbc(final String data, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? toAESCbc(data.getBytes(DEFAULT_CHARSET), key) : null;
    }

    /**
     * 转 AES-CBC加密(带HMAC认证).<br>
     * 输出格式：[IV(16) + 密文 + HMAC(32)]
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组(IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESCbc(final byte[] data, final byte[] key) throws GeneralSecurityException {
        byte[] iv = generateIv();
        return toAESCbc(data, iv, key);
    }

    /**
     * 转 AES-CBC加密(带HMAC认证).<br>
     * 输出格式：[IV(16) + 密文 + HMAC(32)]
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return byte 数组(IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESCbc(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return toAESCbc(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 转 AES-CBC加密(使用指定IV, 带HMAC认证).<br>
     * 输出格式：[IV + 密文 + HMAC(32)]
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组(IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESCbc(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? toAESCbc(data.getBytes(DEFAULT_CHARSET), iv, key) : null;
    }

    /**
     * 转 AES-CBC加密(使用指定IV, 带HMAC认证).<br>
     * 输出格式：[IV + 密文 + HMAC(32)]
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组(IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESCbc(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data);

        byte[] ivPlusCipher = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, ivPlusCipher, 0, iv.length);
        System.arraycopy(encrypted, 0, ivPlusCipher, iv.length, encrypted.length);

        byte[] mac = hmacSha256(ivPlusCipher, key);

        byte[] result = new byte[ivPlusCipher.length + mac.length];
        System.arraycopy(ivPlusCipher, 0, result, 0, ivPlusCipher.length);
        System.arraycopy(mac, 0, result, ivPlusCipher.length, mac.length);

        return result;
    }

    /**
     * 转 AES-CBC加密(使用指定IV, 带HMAC认证).<br>
     * 输出格式：[IV + 密文 + HMAC(32)]
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return byte 数组(IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESCbc(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return toAESCbc(Arrays.copyOfRange(data, start, end), iv, key);
    }

    /**
     * 转 AES-CBC加密(带HMAC认证).<br>
     * 
     * @param data 数据
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESCbcString(final String data, final byte[] key) throws GeneralSecurityException {
        return toAESCbcString(data.getBytes(DEFAULT_CHARSET), key);
    }

    /**
     * 转 AES-CBC加密(带HMAC认证).<br>
     * 
     * @param data 数据
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESCbcString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(toBase64(toAESCbc(data, key)), DEFAULT_CHARSET);
    }

    /**
     * 转 AES-CBC加密(带HMAC认证).<br>
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return String(Base64编码的IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESCbcString(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return toAESCbcString(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 转 AES-CBC加密(使用指定IV, 带HMAC认证).<br>
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESCbcString(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return toAESCbcString(data.getBytes(DEFAULT_CHARSET), iv, key);
    }

    /**
     * 转 AES-CBC加密(使用指定IV, 带HMAC认证).<br>
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESCbcString(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return new String(toBase64(toAESCbc(data, iv, key)), DEFAULT_CHARSET);
    }

    /**
     * 转 AES-CBC加密(使用指定IV, 带HMAC认证).<br>
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return String(Base64编码的IV + 密文 + HMAC)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESCbcString(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return toAESCbcString(Arrays.copyOfRange(data, start, end), iv, key);
    }

    /**
     * 转 AES-GCM加密.<br>
     * 输出格式：[IV(12) + 密文 + 认证标签]
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final String data, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? toAESGcm(data.getBytes(DEFAULT_CHARSET), key) : null;
    }

    /**
     * 转 AES-GCM加密.<br>
     * 输出格式：[IV(12) + 密文 + 认证标签]
     * 
     * @param data 数据
     * @param key  密钥
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final byte[] data, final byte[] key) throws GeneralSecurityException {
        byte[] iv = generateIv(GCM_IV_LENGTH);
        return toAESGcm(data, iv, key);
    }

    /**
     * 转 AES-GCM加密.<br>
     * 输出格式：[IV(12) + 密文 + 认证标签]
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return toAESGcm(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 转 AES-GCM加密(使用指定IV).<br>
     * 输出格式：[IV + 密文 + 认证标签]
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return Objects.nonNull(data) ? toAESGcm(data.getBytes(DEFAULT_CHARSET), iv, key) : null;
    }

    /**
     * 转 AES-GCM加密(使用指定IV).<br>
     * 输出格式：[IV + 密文 + 认证标签]
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
        byte[] encrypted = cipher.doFinal(data);
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    /**
     * 转 AES-GCM加密(使用指定IV, 带额外认证数据).<br>
     * 输出格式：[IV + 密文 + 认证标签]
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @param aad  额外认证数据(可为null)
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final byte[] data, final byte[] iv, final byte[] key, final byte[] aad) throws GeneralSecurityException {
        validateAesKey(key);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
        if (aad != null && aad.length > 0) {
            cipher.updateAAD(aad);
        }
        byte[] encrypted = cipher.doFinal(data);
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    /**
     * 转 AES-GCM加密(使用指定IV).<br>
     * 输出格式：[IV + 密文 + 认证标签]
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return byte 数组(IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static byte[] toAESGcm(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return toAESGcm(Arrays.copyOfRange(data, start, end), iv, key);
    }

    /**
     * 转 AES-GCM加密.<br>
     * 
     * @param data 数据
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESGcmString(final String data, final byte[] key) throws GeneralSecurityException {
        return toAESGcmString(data.getBytes(DEFAULT_CHARSET), key);
    }

    /**
     * 转 AES-GCM加密.<br>
     * 
     * @param data 数据
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESGcmString(final byte[] data, final byte[] key) throws GeneralSecurityException {
        return new String(toBase64(toAESGcm(data, key)), DEFAULT_CHARSET);
    }

    /**
     * 转 AES-GCM加密.<br>
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param key   密钥
     * @return String(Base64编码的IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESGcmString(final byte[] data, final int start, final int end, final byte[] key) throws GeneralSecurityException {
        return toAESGcmString(Arrays.copyOfRange(data, start, end), key);
    }

    /**
     * 转 AES-GCM加密(使用指定IV).<br>
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESGcmString(final String data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return toAESGcmString(data.getBytes(DEFAULT_CHARSET), iv, key);
    }

    /**
     * 转 AES-GCM加密(使用指定IV).<br>
     * 
     * @param data 数据
     * @param iv   初始化向量
     * @param key  密钥
     * @return String(Base64编码的IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESGcmString(final byte[] data, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return new String(toBase64(toAESGcm(data, iv, key)), DEFAULT_CHARSET);
    }

    /**
     * 转 AES-GCM加密(使用指定IV).<br>
     * 
     * @param data  数据
     * @param start 开始位置
     * @param end   结束位置
     * @param iv    初始化向量
     * @param key   密钥
     * @return String(Base64编码的IV + 密文 + 认证标签)
     * @throws GeneralSecurityException {@link javax.crypto.Cipher#doFinal(byte[])}
     */
    public static String toAESGcmString(final byte[] data, final int start, final int end, final byte[] iv, final byte[] key) throws GeneralSecurityException {
        return toAESGcmString(Arrays.copyOfRange(data, start, end), iv, key);
    }

}