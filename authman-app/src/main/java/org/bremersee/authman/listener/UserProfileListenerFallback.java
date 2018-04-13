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

package org.bremersee.authman.listener;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.swagger.authman.listener.api.UserProfileListenerApi;
import org.bremersee.swagger.authman.listener.model.EnabledDto;
import org.bremersee.swagger.authman.listener.model.NewEmailDto;
import org.bremersee.swagger.authman.listener.model.NewMobileDto;
import org.bremersee.swagger.authman.listener.model.NewPasswordDto;
import org.bremersee.swagger.authman.listener.model.NewRolesDto;
import org.bremersee.swagger.authman.listener.model.UserProfileChangeEventDto;
import org.bremersee.swagger.authman.listener.model.UserProfileCreationEventDto;
import org.bremersee.swagger.authman.listener.model.UserProfileRegistrationRequestEventDto;
import org.springframework.http.ResponseEntity;

/**
 * @author Christian Bremer
 */
@Slf4j
public class UserProfileListenerFallback implements UserProfileListenerApi {

  @Override
  public ResponseEntity<Void> onChangeEnabledState(
      final String userName,
      @Valid final EnabledDto newEnabledState) {

    log.error("Changing enabled state failed for user [{}] and state [{}]. "
        + "Calling user profile listener fallback.", userName, newEnabledState); // NOSONAR
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onChangeUserProfile(
      @Valid final UserProfileChangeEventDto newUserProfile) {

    log.error("Changing user profile [{}]  failed. Calling user profile listener fallback.",
        newUserProfile);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onCreateUserProfile(
      @Valid final UserProfileCreationEventDto createRequest) {

    log.error("Creating user profile [{}] failed. Calling user profile listener fallback.",
        createRequest);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onDeleteMobile(
      final String userName,
      final String number) {

    log.error("Deleting mobile number of user [{}] failed. Calling user profile listener fallback.",
        userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onDeleteUserProfile(final String userName) {

    log.error("Deleting user profile [{}] failed. Calling user profile listener fallback.",
        userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onNewEmail(
      final String userName,
      @Valid final NewEmailDto newEmail) {

    log.error("Changing email [{}] of user [{}] failed. Calling user profile listener fallback.",
        newEmail, userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onNewMobile(
      final String userName,
      @Valid final NewMobileDto newMobile) {

    log.error("Changing mobile number [{}] of user [{}] failed. "
        + "Calling user profile listener fallback.", newMobile, userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onNewPassword(
      final String userName,
      @Valid final NewPasswordDto newPassword) {

    log.error("Changing password of user [{}] failed. Calling user profile listener fallback.",
        userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onNewRoles(
      final String userName,
      @Valid final NewRolesDto newRoles) {

    log.error("Changing roles [{}] of user [{}] failed. Calling user profile listener fallback.",
        newRoles, userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> onUserRegistrationRequest(
      @Valid final UserProfileRegistrationRequestEventDto userRegistrationRequest) {

    log.error("Processing registration request [{}] failed. "
        + "Calling user profile listener fallback.", userRegistrationRequest);
    return ResponseEntity.ok().build();
  }
}
