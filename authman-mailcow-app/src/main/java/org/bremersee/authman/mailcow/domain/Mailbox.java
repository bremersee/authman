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

package org.bremersee.authman.mailcow.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Christian Bremer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(
    fieldVisibility = Visibility.ANY,
    getterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE
)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "mailbox", indexes = {@Index(columnList = "domain")})
public class Mailbox extends AbstractUser {

  private static final long serialVersionUID = -8797346591416851685L;

  @JsonProperty(value = "name")
  @Column(name = "name")
  private String name;

  @JsonProperty(value = "mailDir", required = true)
  @Column(name = "maildir", nullable = false)
  private String mailDir;

  @JsonProperty(value = "quota", defaultValue = "102400")
  @Column(name = "quota", nullable = false)
  private long quota = 102400L;

  @JsonProperty(value = "localPart", required = true)
  @Column(name = "local_part", nullable = false)
  private String localPart;

  @JsonProperty(value = "domain", required = true)
  @Column(name = "domain", nullable = false)
  private String domain;

  @JsonProperty(value = "tlsEnforceIn", defaultValue = "false")
  @Column(name = "tls_enforce_in", nullable = false)
  private boolean tlsEnforceIn;

  @JsonProperty(value = "tlsEnforceOut", defaultValue = "false")
  @Column(name = "tls_enforce_out", nullable = false)
  private boolean tlsEnforceOut;

  @JsonProperty(value = "kind", defaultValue = "")
  @Column(name = "kind", nullable = false, length = 100)
  private String kind = "";

  @JsonProperty(value = "multipleBookings", defaultValue = "false")
  @Column(name = "multiple_bookings", nullable = false)
  private boolean multipleBookings;

  @JsonProperty(value = "wantsTaggedSubject", defaultValue = "false")
  @Column(name = "wants_tagged_subject", nullable = false)
  private boolean wantsTaggedSubject;

  public Mailbox() {
    setUserType(UserType.USER);
  }
}
