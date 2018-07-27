/*
 * Copyright 2016 the original author or authors.
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

package org.bremersee.authman.mapper;

import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.bremersee.authman.domain.OAuth2ForeignToken;
import org.bremersee.authman.security.authentication.CodeExchangeResponse;
import org.bremersee.authman.security.authentication.OAuth2AuthenticationToken;

/**
 * @author Christian Bremer
 */
public interface OAuth2ForeignTokenMapper {

  void updateForeignToken(
      @NotNull final OAuth2ForeignToken destination,
      @NotNull final OAuth2AuthenticationToken source);

  void updateForeignToken(
      @NotNull final OAuth2ForeignToken destination,
      @NotNull final String provider,
      final String userName,
      @NotNull final String foreignUserName,
      final Collection<String> scopes,
      @NotNull final CodeExchangeResponse codeResponse);

}
