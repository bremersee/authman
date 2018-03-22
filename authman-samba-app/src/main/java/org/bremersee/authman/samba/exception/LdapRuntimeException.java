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

package org.bremersee.authman.samba.exception;

import org.ldaptive.LdapException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Ldap runtime exception is thrown when a ldap operation fails.
 *
 * @author Christian Bremer
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Ldap operation failed")
public class LdapRuntimeException extends RuntimeException {

  private static final long serialVersionUID = -2986602025579372051L;

  /**
   * Instantiates a new Ldap runtime exception.
   *
   * @param cause the cause
   */
  public LdapRuntimeException(LdapException cause) {
    super("Ldap operation failed.", cause);
  }
}
