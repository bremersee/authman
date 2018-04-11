/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.authman.security.crypto.password;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.security.crypto.password-encoder")
@Data
@NoArgsConstructor
public class PasswordEncoderProperties implements Serializable {

  /**
   * The length of the random salt.
   */
  private int randomSaltLength = 4;

  /**
   * The hash algorithm. The default is {@code SSHA256}, that is also used in mailcow, set to {@code
   * clear} to use no encryption.
   */
  private String algorithm = "SSHA256";

  /**
   * If this flag is set to {@code true}, the clear password is prefixed with {@code {clear}},
   * otherwise it has no prefix. The default is {@code false}.
   */
  private boolean storeNoEncryptionFlag = false;

}
