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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(exclude = {"clientSecret"})
@EqualsAndHashCode(exclude = {"clientSecret"})
@NoArgsConstructor
public abstract class OAuth2AuthenticationProperties implements Serializable {

  private static final long serialVersionUID = 5704725929075936143L;

  private String provider;


  private String stateKeyName = getProvider() + ".state";

  private String loginUrlTemplate;

  private String clientId;

  private String clientSecret;

  private String redirectUri;

  // must be 'code', because 'token' would result in hash fragments, which can't be parsed by HttpServletRequest
  private String responseType = "code";

  private String scope;

  private String scopeSeparator = " ";

  private Map<String, String> additionalLoginParameters = new LinkedHashMap<>();


  private String responseCodeParameter = "code";

  private String responseStateParameter = "state";

  private String responseErrorParameter = "error";


  private String tokenUrlTemplate;

  private HttpMethod tokenMethod = HttpMethod.GET;

  private Map<String, String> additionalTokenParameters = new LinkedHashMap<>();

  private Map<String, String> tokenHeaders = new LinkedHashMap<>();


  private String apiBaseUrl;

  private String profilePathTemplate;


  public Set<String> scopes() {
    LinkedHashSet<String> scopes = new LinkedHashSet<>();
    if (StringUtils.hasText(getScope())) {
      final String sep;
      if (scopeSeparator == null || scopeSeparator.length() == 0) {
        sep = " ";
      } else {
        sep = scopeSeparator;
      }
      String[] scopeArray = getScope().split(Pattern.quote(sep));
      for (String entry : scopeArray) {
        String trimmedEntry = entry.trim();
        if (StringUtils.hasText(trimmedEntry)) {
          scopes.add(trimmedEntry);
        }
      }
    }
    return scopes;
  }

}
