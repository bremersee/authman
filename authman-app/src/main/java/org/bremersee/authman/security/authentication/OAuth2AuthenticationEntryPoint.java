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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.Assert;

/**
 * @author Christian Bremer
 */
@Slf4j
public class OAuth2AuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  private final OAuth2StateCache stateCache;

  @Getter(AccessLevel.PROTECTED)
  private final OAuth2AuthenticationProperties properties;

  public OAuth2AuthenticationEntryPoint(
      @NotNull final OAuth2AuthenticationProperties properties) {

    Assert.notNull(properties, "Properties must be present.");
    Assert.hasText(properties.getStateKeyName(), "State key name must be present.");
    this.properties = properties;
    this.stateCache = new OAuth2StateCache(properties.getStateKeyName());
  }

  @SuppressWarnings("WeakerAccess")
  protected RedirectStrategy getRedirectStrategy() {
    return redirectStrategy;
  }

  @SuppressWarnings("WeakerAccess")
  protected OAuth2StateCache getStateCache() {
    return stateCache;
  }

  @Override
  public void commence(final HttpServletRequest request, final HttpServletResponse response,
      final AuthenticationException authException) throws IOException {

    final String state = UUID.randomUUID().toString().replace("-", "");
    final String url = buildLoginUrl(state);
    getStateCache().saveState(request, state);

    getRedirectStrategy().sendRedirect(request, response, url);
  }

  @SuppressWarnings("WeakerAccess")
  protected String buildLoginUrl(final String state) throws IOException {
    String url = properties.getLoginUrlTemplate();
    url = url.replace("{clientId}",
        URLEncoder.encode(properties.getClientId(), StandardCharsets.UTF_8.name()));
    url = url.replace("{redirectUri}",
        URLEncoder.encode(properties.getRedirectUri(), StandardCharsets.UTF_8.name()));
    url = url.replace("{responseType}",
        URLEncoder.encode(properties.getResponseType(), StandardCharsets.UTF_8.name()));
    url = url.replace("{scope}",
        URLEncoder.encode(properties.getScope(), StandardCharsets.UTF_8.name()));
    url = url.replace("{state}", URLEncoder.encode(state, StandardCharsets.UTF_8.name()));
    return replaceAllAdditionalParameters(url);
  }

  @SuppressWarnings("WeakerAccess")
  protected String replaceAllAdditionalParameters(final String url) throws IOException {
    String tmp = url;
    String newUrl;
    while (!(newUrl = replaceAdditionalParameters(tmp)).equals(tmp)) {
      tmp = newUrl;
    }
    return newUrl;
  }

  private String replaceAdditionalParameters(final String url) throws IOException {
    final Map<String, String> params = properties.getAdditionalLoginParameters();
    final int start = url.indexOf('{');
    final int end = url.indexOf('}');
    if (0 < start && start < end) {
      final String key = url.substring(start + 1, end);
      final String value = params.getOrDefault(key, "");
      return url.substring(0, start) + URLEncoder.encode(value, StandardCharsets.UTF_8.name())
          + url.substring(end + 1);
    }
    return url;
  }

}
