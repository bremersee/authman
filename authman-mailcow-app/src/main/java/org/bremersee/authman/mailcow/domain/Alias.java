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
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

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
@ToString
@EqualsAndHashCode(of = "address")
@Entity
@Table(name = "alias", indexes = {@Index(columnList = "domain")})
public class Alias implements Serializable {

  private static final long serialVersionUID = -4340013012371533080L;

  @JsonProperty(value = "address", required = true)
  @Id
  @Column(name = "address", nullable = false)
  private String address;

  @JsonProperty(value = "goTo", required = true)
  @Column(name = "goto", nullable = false)
  @Type(type = "text")
  private String goTo;

  @JsonProperty(value = "domain", required = true)
  @Column(name = "domain", nullable = false)
  private String domain;

  @JsonProperty(value = "created", required = true)
  @Column(name = "created", nullable = false)
  private Date created = new Date();

  @JsonProperty(value = "modified")
  @Column(name = "modified")
  private Date modified;

  @JsonProperty(value = "active", defaultValue = "false")
  @Column(name = "active", nullable = false)
  private boolean active;

}
