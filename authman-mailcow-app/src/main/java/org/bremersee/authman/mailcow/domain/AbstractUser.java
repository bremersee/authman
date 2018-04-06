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
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "userType")
@JsonSubTypes({
    @Type(value = Admin.class, name = "ADMIN"),
    @Type(value = Mailbox.class, name = "USER")
})
@ToString(exclude = "password")
@EqualsAndHashCode(of = "userName")
@MappedSuperclass
public abstract class AbstractUser implements Serializable {

  private static final long serialVersionUID = 7620309360467915410L;

  @JsonProperty(value = "userType", access = Access.READ_ONLY)
  @Getter
  @Setter(AccessLevel.PROTECTED)
  @Transient
  private UserType userType;

  @JsonProperty(value = "userName", required = true)
  @Getter
  @Setter
  @Id
  @Column(name = "username", nullable = false)
  private String userName;

  @JsonProperty(value = "password", required = true)
  @Getter
  @Setter
  @Column(name = "password", nullable = false)
  private String password;

  @JsonProperty(value = "created", required = true)
  @Getter
  @Setter
  @Column(name = "created", nullable = false)
  private Date created = new Date();

  @JsonProperty(value = "modified")
  @Getter
  @Setter
  @Column(name = "modified")
  private Date modified;

  @JsonProperty(value = "active", defaultValue = "false")
  @Getter
  @Setter
  @Column(name = "active", nullable = false)
  private boolean active;

}
