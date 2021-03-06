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

package org.bremersee.authman.security.authentication;

import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Christian Bremer
 */
@EqualsAndHashCode(callSuper = true, exclude = {"password"})
@ToString(callSuper = true, exclude = {"password"})
class OAuth2LinkAuthenticationToken extends OAuth2AuthenticationToken {

  @Getter
  private final String userName;

  @Getter
  private final String password;

  OAuth2LinkAuthenticationToken(
      @NotNull final OAuth2AuthenticationToken original,
      @NotNull final String userName,
      @NotNull final String password) {

    super(original);
    this.userName = userName;
    this.password = password;
  }

}
