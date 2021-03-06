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

package org.bremersee.authman.domain;

import java.security.Principal;
import java.util.Locale;
import java.util.TimeZone;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bremersee.authman.model.SambaSettingsDto;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = {"password"})
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Document(collection = "userProfile")
public class UserProfile extends AbstractAuditBase implements Principal {

  @Indexed(unique = true)
  private String userName;

  private String password;

  private Boolean enabled = Boolean.TRUE;

  private String displayName;

  private String preferredLocale = Locale.getDefault().toString();

  private String preferredTimeZoneId = TimeZone.getDefault().getID();

  @Indexed(unique = true, sparse = true)
  private String email;

  @Indexed(unique = true, sparse = true)
  private String mobile;

  private SambaSettingsDto sambaSettings;

  @Transient
  @Override
  public String getName() {
    return userName;
  }

  public boolean isEnabled() {
    return enabled == null || enabled;
  }

}
