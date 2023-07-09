package io.github.fsixteen.common.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class DecodingUtils {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private DecodingUtils() {
    }

    public static String fromBase64(String data) {
        if (Objects.isNull(data) || data.isEmpty()) {
            return data;
        }
        return new String(fromBase64(data.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
    }

    public static byte[] fromBase64(byte[] data) {
        if (Objects.isNull(data) || 0 == data.length) {
            return new byte[] {};
        }
        return Base64.getDecoder().decode(data);
    }

}
