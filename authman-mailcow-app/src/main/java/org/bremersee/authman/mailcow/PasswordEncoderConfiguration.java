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

package org.bremersee.authman.mailcow;

import org.bremersee.authman.security.crypto.password.PasswordEncoderImpl;
import org.bremersee.authman.security.crypto.password.PasswordEncoderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Christian Bremer
 */
@Configuration
@EnableConfigurationProperties({PasswordEncoderProperties.class})
public class PasswordEncoderConfiguration {

  private final PasswordEncoderProperties properties;

  @Autowired
  public PasswordEncoderConfiguration(
      PasswordEncoderProperties properties) {
    this.properties = properties;
  }

  @Bean(name = "passwordEncoder", initMethod = "init")
  public PasswordEncoderImpl passwordEncoder() {
    return new PasswordEncoderImpl(properties);
  }
}
