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

package org.bremersee.authman.controller;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Christian Bremer
 */
@Data
@NoArgsConstructor
public class RedirectMessage implements Serializable {

  private static final long serialVersionUID = 6782221503683539133L;

  public static final String ATTRIBUTE_NAME = "rmsg";

  private RedirectMessageType msgType = RedirectMessageType.INFO;

  private String msg;

  public RedirectMessage(@NotNull String msg) {
    this.msg = msg;
  }

  public RedirectMessage(@NotNull String msg, RedirectMessageType msgType) {
    if (msgType != null) {
      this.msgType = msgType;
    }
    this.msg = msg;
  }
}
