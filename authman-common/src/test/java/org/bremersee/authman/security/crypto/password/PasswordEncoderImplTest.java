package org.bremersee.authman.security.crypto.password;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christian Bremer
 */
@Slf4j
public class PasswordEncoderImplTest {

  private PasswordEncoderImpl buildPasswordEncoder(
      final String algorithm, int saltLength) {
    return buildPasswordEncoder(algorithm, false, saltLength);
  }

  private PasswordEncoderImpl buildPasswordEncoder(
      final boolean storeNoEncryptionFlag) {
    return buildPasswordEncoder("clear", storeNoEncryptionFlag, 4);
  }

  private PasswordEncoderImpl buildPasswordEncoder(
      final String algorithm,
      final boolean storeNoEncryptionFlag,
      final int saltLength) {
    final PasswordEncoderProperties properties = new PasswordEncoderProperties();
    properties.setAlgorithm(algorithm);
    properties.setStoreNoEncryptionFlag(storeNoEncryptionFlag);
    properties.setRandomSaltLength(saltLength);
    return new PasswordEncoderImpl(properties);
  }

  @Test
  public void clearPasswordWithNoPrefix() {
    log.info("Testing clear password with no prefix.");
    final String password = "passwordWithNoEncryption";
    final PasswordEncoderImpl encoder = buildPasswordEncoder(false);
    final String encodedPassword = encoder.encode(password);
    Assert.assertEquals(password, encodedPassword);
  }

  @Test
  public void clearPasswordWithPrefix() {
    log.info("Testing clear password with prefix.");
    final String password = "passwordWithNoEncryption";
    final PasswordEncoderImpl encoder = buildPasswordEncoder(true);
    String encodedPassword = encoder.encode(password);
    Assert.assertEquals("{clear}" + password, encodedPassword);
  }

  @Test
  public void sshaEncoding() {
    log.info("Testing password with salted sha.");
    final String password = "passwordWithSaltedShaEncryption";
    final PasswordEncoderImpl encoder = buildPasswordEncoder("SSHA", 4);
    final String encodedPassword = encoder.encode(password);
    Assert.assertTrue(encoder.matches(password, encodedPassword));
  }

  @Test
  public void ssha256Encoding() {
    log.info("Testing password with salted sha-256.");
    final String password = "passwordWithSaltedSha256Encryption";
    final PasswordEncoderImpl encoder = buildPasswordEncoder("SSHA256", 16);
    log.info("Algorithm: {}", encoder.getAlgorithm());
    final String encodedPassword = encoder.encode(password);
    log.info("Encoded: {}", encodedPassword);
    Assert.assertTrue(encoder.matches(password, encodedPassword));
  }

}