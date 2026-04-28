package io.github.fsixteen.common.utils.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.fsixteen.common.utils.DecodingUtils;
import io.github.fsixteen.common.utils.EncodingUtils;

/**
 * EncodingUtils and DecodingUtils unit tests.
 */
@DisplayName("EncodingUtils and DecodingUtils Tests")
public class EncodingDecodingUtilsTest {

    private static final byte[] TEST_KEY_128 = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };

    private static final byte[] TEST_KEY_192 = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
        0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };

    private static final byte[] TEST_KEY_256 = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F,
        0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F };

    private static final String TEST_STRING = "Hello, World! 测试数据";

    private static final String SHORT_STRING = "A";

    private static final String CHINESE_STRING = "你好世界! @#$%^&*() 中文测试";

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    @Nested
    @DisplayName("IV Generation Tests")
    class IvGenerationTests {

        @Test
        @DisplayName("generateIv() should return 16-byte IV")
        public void testGenerateIv() {
            byte[] iv = EncodingUtils.generateIv();
            assertNotNull(iv);
            assertEquals(16, iv.length);
        }

        @Test
        @DisplayName("generateIv(int) should return specified length IV")
        public void testGenerateIvWithLength() {
            byte[] iv12 = EncodingUtils.generateIv(12);
            assertNotNull(iv12);
            assertEquals(12, iv12.length);

            byte[] iv8 = EncodingUtils.generateIv(8);
            assertNotNull(iv8);
            assertEquals(8, iv8.length);
        }

    }

    @Nested
    @DisplayName("Salt Generation Tests")
    class SaltGenerationTests {

        @Test
        @DisplayName("generateSalt() should return 16-byte salt")
        public void testGenerateSalt() {
            byte[] salt = EncodingUtils.generateSalt();
            assertNotNull(salt);
            assertEquals(16, salt.length);
        }

        @Test
        @DisplayName("generateSalt(int) should return specified length salt")
        public void testGenerateSaltWithLength() {
            byte[] salt8 = EncodingUtils.generateSalt(8);
            assertNotNull(salt8);
            assertEquals(8, salt8.length);

            byte[] salt32 = EncodingUtils.generateSalt(32);
            assertNotNull(salt32);
            assertEquals(32, salt32.length);
        }

    }

    @Nested
    @DisplayName("Key Validation Tests")
    class KeyValidationTests {

        @Test
        @DisplayName("validateAesKey should accept valid 128-bit key")
        public void testValidateAesKey128() throws InvalidKeyException {
            EncodingUtils.validateAesKey(TEST_KEY_128);
        }

        @Test
        @DisplayName("validateAesKey should accept valid 192-bit key")
        public void testValidateAesKey192() throws InvalidKeyException {
            EncodingUtils.validateAesKey(TEST_KEY_192);
        }

        @Test
        @DisplayName("validateAesKey should accept valid 256-bit key")
        public void testValidateAesKey256() throws InvalidKeyException {
            EncodingUtils.validateAesKey(TEST_KEY_256);
        }

        @Test
        @DisplayName("validateAesKey should throw exception for invalid key length")
        public void testValidateAesKeyInvalidLength() {
            byte[] invalidKey = new byte[10];
            try {
                EncodingUtils.validateAesKey(invalidKey);
                fail("Invalid key should throw exception");
            } catch (InvalidKeyException e) {
                assertTrue(e.getMessage().contains("Invalid AES key length"));
            }
        }

        @Test
        @DisplayName("validateAesKey should throw exception for null key")
        public void testValidateAesKeyNull() {
            try {
                EncodingUtils.validateAesKey(null);
                fail("Null key should throw exception");
            } catch (InvalidKeyException e) {
                assertTrue(e.getMessage().contains("cannot be null"));
            }
        }

        @Test
        @DisplayName("DecodingUtils.validateAesKey should match EncodingUtils behavior")
        public void testDecodingUtilsValidateAesKey() {
            try {
                DecodingUtils.validateAesKey(TEST_KEY_128);
                DecodingUtils.validateAesKey(TEST_KEY_256);
            } catch (InvalidKeyException e) {
                fail("Valid keys should not throw exception");
            }

            try {
                DecodingUtils.validateAesKey(new byte[10]);
                fail("Invalid key should throw exception");
            } catch (InvalidKeyException e) {
                assertTrue(e.getMessage().contains("Invalid AES key length"));
            }
        }

    }

    @Nested
    @DisplayName("PBKDF2 Key Derivation Tests")
    class KeyDerivationTests {

        @Test
        @DisplayName("deriveAesKey should generate 256-bit key by default")
        public void testDeriveAesKeyDefault() throws GeneralSecurityException {
            char[] password = "mySecurePassword123".toCharArray();
            byte[] salt = EncodingUtils.generateSalt();

            byte[] derivedKey = EncodingUtils.deriveAesKey(password, salt);
            assertNotNull(derivedKey);
            assertEquals(32, derivedKey.length);
        }

        @Test
        @DisplayName("deriveAesKey should generate 128-bit key")
        public void testDeriveAesKey128() throws GeneralSecurityException {
            char[] password = "mySecurePassword123".toCharArray();
            byte[] salt = EncodingUtils.generateSalt();

            byte[] derivedKey = EncodingUtils.deriveAesKey(password, salt, 10000, 128);
            assertNotNull(derivedKey);
            assertEquals(16, derivedKey.length);
        }

        @Test
        @DisplayName("deriveAesKey should generate consistent keys for same input")
        public void testDeriveAesKeyConsistency() throws GeneralSecurityException {
            char[] password = "mySecurePassword123".toCharArray();
            byte[] salt = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };

            byte[] key1 = EncodingUtils.deriveAesKey(password, salt);
            byte[] key2 = EncodingUtils.deriveAesKey(password, salt);

            assertArrayEquals(key1, key2);
        }

        @Test
        @DisplayName("deriveAesKey should work with encryption")
        public void testDerivedKeyEncryption() throws GeneralSecurityException {
            char[] password = "testPassword123".toCharArray();
            byte[] salt = EncodingUtils.generateSalt();
            byte[] derivedKey = EncodingUtils.deriveAesKey(password, salt);

            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, derivedKey);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, derivedKey);

            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("deriveAesKey should throw exception for null password")
        public void testDeriveAesKeyNullPassword() {
            try {
                EncodingUtils.deriveAesKey(null, new byte[16]);
                fail("Null password should throw exception");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("Password"));
            } catch (GeneralSecurityException e) {
                fail("Should have thrown IllegalArgumentException");
            }
        }

        @Test
        @DisplayName("deriveAesKey should throw exception for null salt")
        public void testDeriveAesKeyNullSalt() {
            try {
                EncodingUtils.deriveAesKey("password".toCharArray(), null);
                fail("Null salt should throw exception");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("Salt"));
            } catch (GeneralSecurityException e) {
                fail("Should have thrown IllegalArgumentException");
            }
        }

        @Test
        @DisplayName("deriveAesKey should throw exception for empty salt")
        public void testDeriveAesKeyEmptySalt() {
            try {
                EncodingUtils.deriveAesKey("password".toCharArray(), new byte[0]);
                fail("Empty salt should throw exception");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("Salt"));
            } catch (GeneralSecurityException e) {
                fail("Should have thrown IllegalArgumentException");
            }
        }

        @Test
        @DisplayName("deriveAesKey should throw exception for zero iterations")
        public void testDeriveAesKeyZeroIterations() {
            try {
                EncodingUtils.deriveAesKey("password".toCharArray(), new byte[16], 0, 128);
                fail("Zero iterations should throw exception");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("Iterations"));
            } catch (GeneralSecurityException e) {
                fail("Should have thrown IllegalArgumentException");
            }
        }

        @Test
        @DisplayName("deriveAesKey should throw exception for invalid key length")
        public void testDeriveAesKeyInvalidKeyLength() {
            try {
                EncodingUtils.deriveAesKey("password".toCharArray(), new byte[16], 10000, 100);
                fail("Invalid key length should throw exception");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("Key length"));
            } catch (GeneralSecurityException e) {
                fail("Should have thrown IllegalArgumentException");
            }
        }

    }

    @Nested
    @DisplayName("Secure Wipe Tests")
    class SecureWipeTests {

        @Test
        @DisplayName("wipe should clear byte array")
        public void testWipeByteArray() {
            byte[] data = new byte[] { 1, 2, 3, 4, 5 };
            EncodingUtils.wipe(data);
            for (byte b : data) {
                assertEquals(0, b);
            }
        }

        @Test
        @DisplayName("wipe should clear char array")
        public void testWipeCharArray() {
            char[] chars = new char[] { 'a', 'b', 'c' };
            EncodingUtils.wipe(chars);
            for (char c : chars) {
                assertEquals('\0', c);
            }
        }

        @Test
        @DisplayName("wipe should handle null byte array")
        public void testWipeNullByteArray() {
            EncodingUtils.wipe((byte[]) null);
        }

        @Test
        @DisplayName("wipe should handle null char array")
        public void testWipeNullCharArray() {
            EncodingUtils.wipe((char[]) null);
        }

        @Test
        @DisplayName("DecodingUtils.wipe should match EncodingUtils behavior")
        public void testDecodingUtilsWipe() {
            byte[] data = new byte[] { 1, 2, 3 };
            DecodingUtils.wipe(data);
            for (byte b : data) {
                assertEquals(0, b);
            }

            char[] chars = new char[] { 'a', 'b' };
            DecodingUtils.wipe(chars);
            for (char c : chars) {
                assertEquals('\0', c);
            }
        }

    }

    @Nested
    @DisplayName("HMAC-SHA256 Tests")
    class HmacTests {

        @Test
        @DisplayName("hmacSha256 should generate 32-byte hash")
        public void testHmacSha256() throws GeneralSecurityException {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);
            byte[] mac = EncodingUtils.hmacSha256(data, TEST_KEY_256);

            assertNotNull(mac);
            assertEquals(32, mac.length);
        }

        @Test
        @DisplayName("hmacSha256 should generate consistent hash for same input")
        public void testHmacSha256Consistency() throws GeneralSecurityException {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);
            byte[] mac1 = EncodingUtils.hmacSha256(data, TEST_KEY_256);
            byte[] mac2 = EncodingUtils.hmacSha256(data, TEST_KEY_256);

            assertArrayEquals(mac1, mac2);
        }

        @Test
        @DisplayName("DecodingUtils.hmacSha256 should match EncodingUtils behavior")
        public void testDecodingUtilsHmacSha256() throws GeneralSecurityException {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);
            byte[] mac = DecodingUtils.hmacSha256(data, TEST_KEY_256);

            assertNotNull(mac);
            assertEquals(32, mac.length);
        }

    }

    @Nested
    @DisplayName("AES-ECB Tests (Backward Compatibility)")
    class EcbTests {

        @Test
        @DisplayName("toAES/fromAES should encrypt/decrypt byte array")
        public void testEcbByteArray() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAES(TEST_STRING, TEST_KEY_128);
            assertNotNull(encrypted);

            byte[] decrypted = DecodingUtils.fromAES(encrypted, TEST_KEY_128);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("toAESString/fromAESString should encrypt/decrypt string")
        public void testEcbString() throws GeneralSecurityException {
            String encrypted = EncodingUtils.toAESString(TEST_STRING, TEST_KEY_128);
            assertNotNull(encrypted);

            String decrypted = DecodingUtils.fromAESString(encrypted, TEST_KEY_128);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, decrypted);
        }

    }

    @Nested
    @DisplayName("AES-CBC Tests")
    class CbcTests {

        @Test
        @DisplayName("toAESCbc/fromAESCbc should encrypt/decrypt byte array")
        public void testCbcByteArray() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);
            assertNotNull(encrypted);
            assertTrue(encrypted.length > 16 + 32);

            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("toAESCbcString/fromAESCbcString should encrypt/decrypt string")
        public void testCbcString() throws GeneralSecurityException {
            String encrypted = EncodingUtils.toAESCbcString(TEST_STRING, TEST_KEY_256);
            assertNotNull(encrypted);

            String decrypted = DecodingUtils.fromAESCbcString(encrypted, TEST_KEY_256);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, decrypted);
        }

        @Test
        @DisplayName("toAESCbc with custom IV should work correctly")
        public void testCbcWithCustomIv() throws GeneralSecurityException {
            byte[] iv = EncodingUtils.generateIv();
            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, iv, TEST_KEY_256);
            assertNotNull(encrypted);

            byte[] actualIv = Arrays.copyOfRange(encrypted, 0, 16);
            assertArrayEquals(iv, actualIv);

            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("CBC format consistency between byte[] and String methods")
        public void testCbcFormatConsistency() throws GeneralSecurityException {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);

            byte[] encryptedBytes = EncodingUtils.toAESCbc(data, TEST_KEY_256);
            String encryptedString = EncodingUtils.toAESCbcString(data, TEST_KEY_256);

            byte[] decryptedFromBytes = DecodingUtils.fromAESCbc(encryptedBytes, TEST_KEY_256);
            byte[] decryptedFromString = DecodingUtils.fromAESCbcString(encryptedString, TEST_KEY_256).getBytes(StandardCharsets.UTF_8);

            assertArrayEquals(data, decryptedFromBytes);
            assertArrayEquals(data, decryptedFromString);
        }

        @Test
        @DisplayName("CBC should detect tampered data via HMAC")
        public void testCbcHmacVerification() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);

            byte[] tampered = Arrays.copyOf(encrypted, encrypted.length);
            tampered[16] ^= 0xFF;

            try {
                DecodingUtils.fromAESCbc(tampered, TEST_KEY_256);
                fail("Tampered data should throw exception");
            } catch (GeneralSecurityException e) {
                assertTrue(e.getMessage().contains("HMAC verification failed"));
            }
        }

        @Test
        @DisplayName("CBC should work with offset parameters")
        public void testCbcWithOffset() throws GeneralSecurityException {
            byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);

            byte[] encrypted = EncodingUtils.toAESCbc(data, 6, data.length, TEST_KEY_256);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);

            assertEquals("World", new String(decrypted, StandardCharsets.UTF_8));
        }

    }

    @Nested
    @DisplayName("AES-GCM Tests")
    class GcmTests {

        @Test
        @DisplayName("toAESGcm/fromAESGcm should encrypt/decrypt byte array")
        public void testGcmByteArray() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESGcm(TEST_STRING, TEST_KEY_256);
            assertNotNull(encrypted);
            assertTrue(encrypted.length > 12);

            byte[] decrypted = DecodingUtils.fromAESGcm(encrypted, TEST_KEY_256);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("toAESGcmString/fromAESGcmString should encrypt/decrypt string")
        public void testGcmString() throws GeneralSecurityException {
            String encrypted = EncodingUtils.toAESGcmString(TEST_STRING, TEST_KEY_256);
            assertNotNull(encrypted);

            String decrypted = DecodingUtils.fromAESGcmString(encrypted, TEST_KEY_256);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, decrypted);
        }

        @Test
        @DisplayName("toAESGcm with custom IV should work correctly")
        public void testGcmWithCustomIv() throws GeneralSecurityException {
            byte[] iv = EncodingUtils.generateIv(12);
            byte[] encrypted = EncodingUtils.toAESGcm(TEST_STRING, iv, TEST_KEY_256);
            assertNotNull(encrypted);

            byte[] actualIv = Arrays.copyOfRange(encrypted, 0, 12);
            assertArrayEquals(iv, actualIv);

            byte[] decrypted = DecodingUtils.fromAESGcm(encrypted, TEST_KEY_256);
            assertNotNull(decrypted);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("GCM format consistency between byte[] and String methods")
        public void testGcmFormatConsistency() throws GeneralSecurityException {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);

            byte[] encryptedBytes = EncodingUtils.toAESGcm(data, TEST_KEY_256);
            String encryptedString = EncodingUtils.toAESGcmString(data, TEST_KEY_256);

            byte[] decryptedFromBytes = DecodingUtils.fromAESGcm(encryptedBytes, TEST_KEY_256);
            byte[] decryptedFromString = DecodingUtils.fromAESGcmString(encryptedString, TEST_KEY_256).getBytes(StandardCharsets.UTF_8);

            assertArrayEquals(data, decryptedFromBytes);
            assertArrayEquals(data, decryptedFromString);
        }

        @Test
        @DisplayName("GCM should detect tampered data")
        public void testGcmTamperDetection() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESGcm(TEST_STRING, TEST_KEY_256);

            byte[] tampered = Arrays.copyOf(encrypted, encrypted.length);
            tampered[12] ^= 0xFF;

            try {
                DecodingUtils.fromAESGcm(tampered, TEST_KEY_256);
                fail("Tampered GCM data should throw exception");
            } catch (GeneralSecurityException e) {
                assertTrue(e.getMessage().contains("Tag mismatch") || e instanceof javax.crypto.AEADBadTagException);
            }
        }

        @Test
        @DisplayName("GCM should work with AAD")
        public void testGcmWithAad() throws GeneralSecurityException {
            byte[] aad = "additional authenticated data".getBytes(StandardCharsets.UTF_8);
            byte[] iv = EncodingUtils.generateIv(12);

            byte[] encrypted = EncodingUtils.toAESGcm(TEST_STRING.getBytes(StandardCharsets.UTF_8), iv, TEST_KEY_256, aad);

            byte[] encryptedData = Arrays.copyOfRange(encrypted, 12, encrypted.length);
            byte[] decrypted = DecodingUtils.fromAESGcm(encryptedData, iv, TEST_KEY_256, aad);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("GCM should work with offset parameters")
        public void testGcmWithOffset() throws GeneralSecurityException {
            byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);

            byte[] encrypted = EncodingUtils.toAESGcm(data, 6, data.length, TEST_KEY_256);
            byte[] decrypted = DecodingUtils.fromAESGcm(encrypted, TEST_KEY_256);

            assertEquals("World", new String(decrypted, StandardCharsets.UTF_8));
        }

    }

    @Nested
    @DisplayName("Key Length Tests")
    class KeyLengthTests {

        @Test
        @DisplayName("AES-128 should work correctly")
        public void testAes128() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_128);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_128);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("AES-192 should work correctly")
        public void testAes192() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_192);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_192);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("AES-256 should work correctly")
        public void testAes256() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);
            assertEquals(TEST_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("All key lengths should produce correct results")
        public void testDifferentKeyLengths() throws GeneralSecurityException {
            byte[] encrypted128 = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_128);
            byte[] encrypted192 = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_192);
            byte[] encrypted256 = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);

            assertNotNull(encrypted128);
            assertNotNull(encrypted192);
            assertNotNull(encrypted256);

            byte[] decrypted128 = DecodingUtils.fromAESCbc(encrypted128, TEST_KEY_128);
            byte[] decrypted192 = DecodingUtils.fromAESCbc(encrypted192, TEST_KEY_192);
            byte[] decrypted256 = DecodingUtils.fromAESCbc(encrypted256, TEST_KEY_256);

            assertEquals(TEST_STRING, new String(decrypted128, StandardCharsets.UTF_8));
            assertEquals(TEST_STRING, new String(decrypted192, StandardCharsets.UTF_8));
            assertEquals(TEST_STRING, new String(decrypted256, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("Decrypting with wrong key should fail")
        public void testWrongKeyDecryption() {
            try {
                byte[] encrypted = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);
                DecodingUtils.fromAESCbc(encrypted, TEST_KEY_128);
                fail("Decrypting with wrong key should throw exception");
            } catch (GeneralSecurityException e) {
                assertTrue(true);
            }
        }

    }

    @Nested
    @DisplayName("Data Boundary Tests")
    class BoundaryTests {

        @Test
        @DisplayName("Null input should return null")
        public void testNullInput() throws GeneralSecurityException {
            assertNull(EncodingUtils.toAES((String) null, TEST_KEY_128));
            assertNull(EncodingUtils.toAESCbc((String) null, TEST_KEY_256));
            assertNull(EncodingUtils.toAESGcm((String) null, TEST_KEY_256));

            assertNull(DecodingUtils.fromAESCbc((String) null, TEST_KEY_256));
            assertNull(DecodingUtils.fromAESGcm((String) null, TEST_KEY_256));
        }

        @Test
        @DisplayName("Empty input should work correctly")
        public void testEmptyInput() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(EMPTY_BYTE_ARRAY, TEST_KEY_256);
            assertNotNull(encrypted);

            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);
            assertArrayEquals(EMPTY_BYTE_ARRAY, decrypted);
        }

        @Test
        @DisplayName("Short input should work correctly")
        public void testShortDataEncryption() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(SHORT_STRING, TEST_KEY_256);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);

            assertEquals(SHORT_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("Long input should work correctly")
        public void testLongDataEncryption() throws GeneralSecurityException {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("test" + i);
            }
            String longString = sb.toString();

            byte[] encrypted = EncodingUtils.toAESCbc(longString, TEST_KEY_256);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);

            assertEquals(longString, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("Chinese and special characters should work correctly")
        public void testChineseAndSpecialCharacters() throws GeneralSecurityException {
            byte[] encrypted = EncodingUtils.toAESCbc(CHINESE_STRING, TEST_KEY_256);
            byte[] decrypted = DecodingUtils.fromAESCbc(encrypted, TEST_KEY_256);

            assertEquals(CHINESE_STRING, new String(decrypted, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("Random IV should produce different encryption results")
        public void testRandomIvDifferentEncryptions() throws GeneralSecurityException {
            byte[] encrypted1 = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);
            byte[] encrypted2 = EncodingUtils.toAESCbc(TEST_STRING, TEST_KEY_256);

            assertFalse(Arrays.equals(encrypted1, encrypted2));
        }

    }

    @Nested
    @DisplayName("Hash Function Tests")
    class HashTests {

        @Test
        @DisplayName("toMd5 should generate 32-character hash")
        public void testMd5() {
            String md5 = EncodingUtils.toMd5(TEST_STRING);
            assertNotNull(md5);
            assertEquals(32, md5.length());

            String md5Again = EncodingUtils.toMd5(TEST_STRING);
            assertEquals(md5, md5Again);
        }

        @Test
        @DisplayName("toSHA256 should generate 32-byte hash")
        public void testSha256() {
            byte[] sha = EncodingUtils.toSHA256(TEST_STRING.getBytes(StandardCharsets.UTF_8));
            assertNotNull(sha);
            assertEquals(32, sha.length);

            String shaHex = EncodingUtils.toSHA256(TEST_STRING);
            assertNotNull(shaHex);
            assertEquals(64, shaHex.length());
        }

        @Test
        @DisplayName("toSHA512 should generate 64-byte hash")
        public void testSha512() {
            byte[] sha = EncodingUtils.toSHA512(TEST_STRING.getBytes(StandardCharsets.UTF_8));
            assertNotNull(sha);
            assertEquals(64, sha.length);

            String shaHex = EncodingUtils.toSHA512(TEST_STRING);
            assertNotNull(shaHex);
            assertEquals(128, shaHex.length());
        }

    }

    @Nested
    @DisplayName("Encoding/Decoding Tests")
    class EncodingDecodingTests {

        @Test
        @DisplayName("Hex conversion should be reversible")
        public void testHexConversion() {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);
            String hex = EncodingUtils.toHex(data);

            assertNotNull(hex);
            assertEquals(data.length * 2, hex.length());

            byte[] decoded = DecodingUtils.fromHex(hex.getBytes(StandardCharsets.US_ASCII));
            assertArrayEquals(data, decoded);
        }

        @Test
        @DisplayName("Base64 conversion should be reversible")
        public void testBase64Conversion() {
            byte[] data = TEST_STRING.getBytes(StandardCharsets.UTF_8);
            byte[] base64 = EncodingUtils.toBase64(data);

            assertNotNull(base64);

            byte[] decoded = DecodingUtils.fromBase64(base64);
            assertArrayEquals(data, decoded);
        }

    }

}