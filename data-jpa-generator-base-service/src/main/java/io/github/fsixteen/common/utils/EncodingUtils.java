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

    private EncodingUtils() {
    }

    public static String toMd5(String data) {
        if (Objects.isNull(data)) {
            return null;
        }
        try {
            StringBuilder out = new StringBuilder(32);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(data.getBytes(DEFAULT_CHARSET));
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

    public static String toBase64(String data) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(toBase64(data.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
    }

    public static byte[] toBase64(byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return new byte[] {};
        }
        return Base64.getEncoder().encode(data);
    }

}
