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

package org.bremersee.authman.model.postman;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Basic {

  public static final String TYPE_STRING = "string";

  public static final String TYPE_BOOLEAN = "boolean";

  public static final String TYPE_ANY = "any";

  public static List<Basic> newBasicAuth(String username, String password) {
    final List<Basic> list = new ArrayList<>();
    list.add(new Basic("password", password, TYPE_STRING));
    list.add(new Basic("username", username, TYPE_STRING));
    list.add(new Basic("saveHelperData", null, TYPE_ANY));
    list.add(new Basic("showPassword", false, TYPE_BOOLEAN));
    return list;
  }

  private String key;

  private Object value;

  private String type;

}
