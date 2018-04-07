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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.authman.model.UserProfileCreateRequestDto;
import org.bremersee.authman.model.UserProfileDto;
import org.bremersee.swagger.authman.listener.api.UserProfileListenerApi;
import org.bremersee.swagger.authman.listener.model.EnabledDto;
import org.bremersee.swagger.authman.listener.model.NewEmailDto;
import org.bremersee.swagger.authman.listener.model.NewMobileDto;
import org.bremersee.swagger.authman.listener.model.NewPasswordDto;
import org.bremersee.swagger.authman.listener.model.NewRolesDto;
import org.bremersee.swagger.authman.listener.model.UserProfileChangeEventDto;
import org.bremersee.swagger.authman.listener.model.UserProfileCreationEventDto;
import org.bremersee.swagger.authman.listener.model.UserProfileRegistrationRequestEventDto;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Christian Bremer
 */
@Slf4j
public class UserProfileListenerImpl implements UserProfileListener {

  @Setter
  private UserProfileListenerMapper mapper = new UserProfileListenerMapperImpl();

  @Getter
  private List<UserProfileListenerApi> httpListeners = new ArrayList<>();

  @Async
  @Override
  public void onUserRegistrationRequest(
      @NotNull UserProfileCreateRequestDto request,
      @NotNull Date expirationDate) {

    final UserProfileRegistrationRequestEventDto dto = mapper.mapToRegistrationRequestEvent(
        request, expirationDate);
    httpListeners.forEach(listener -> {
      try {
        listener.onUserRegistrationRequest(dto);

      } catch (final RuntimeException re) {
        log.error(
            "Publishing registration event [" + dto
                + "] failed with listener [" + listener + "].", re);// NOSONAR
      }
    });
  }

  @Async
  @Override
  public void onCreateUserProfile(
      @NotNull UserProfileDto userProfile,
      String password,
      @NotNull Collection<String> roles) {

    final UserProfileCreationEventDto dto = mapper.mapToCreationEvent(userProfile, password, roles);
    httpListeners.forEach(listener -> {
      try {
        listener.onCreateUserProfile(dto);

      } catch (final RuntimeException re) {
        log.error("Publishing create event [" + dto + "] failed with listener [" + listener + "].",
            re);
      }
    });
  }

  @Async
  @Override
  public void onChangeUserProfile(@NotNull UserProfileDto userProfile) {

    final UserProfileChangeEventDto dto = mapper.mapToChangeEvent(userProfile);
    httpListeners.forEach(listener -> {
      try {
        listener.onChangeUserProfile(dto);

      } catch (final RuntimeException re) {
        log.error("Publishing change event [" + dto + "] failed with listener [" + listener + "].",
            re);
      }
    });
  }

  @Async
  @Override
  public void onDeleteUserProfile(@NotBlank final String userName) {

    httpListeners.forEach(listener -> {
      try {
        listener.onDeleteUserProfile(userName);

      } catch (final RuntimeException re) {
        log.error(
            "Publishing delete event of user profile [" + userName + "] failed with listener ["
                + listener + "].", re);
      }
    });
  }

  @Async
  @Override
  public void onChangeEnabledState(@NotBlank final String userName, final boolean enabled) {

    final EnabledDto dto = new EnabledDto();
    dto.setValue(enabled);
    httpListeners.forEach(listener -> {
      try {
        listener.onChangeEnabledState(userName, dto);

      } catch (final RuntimeException re) {
        log.error("Publishing new enabled state [" + enabled + "] of user profile [" // NOSONAR
            + userName + "] failed with listener [" + listener + "].", re);
      }
    });
  }

  @Async
  @Override
  public void onNewPassword(@NotBlank final String userName, final String newPassword) {

    final NewPasswordDto dto = new NewPasswordDto();
    dto.setValue(newPassword);
    httpListeners.forEach(listener -> {
      try {
        listener.onNewPassword(userName, dto);

      } catch (final RuntimeException re) {
        log.error(
            "Publishing new password of user profile [" + userName + "] failed with listener ["
                + listener + "].", re);
      }
    });
  }

  @Async
  @Override
  public void onNewEmail(@NotBlank final String userName, @NotBlank final String newEmail) {

    final NewEmailDto dto = new NewEmailDto();
    dto.setValue(newEmail);
    httpListeners.forEach(listener -> {
      try {
        listener.onNewEmail(userName, dto);

      } catch (final RuntimeException re) {
        log.error("Publishing new email [" + newEmail + "] of user profile [" + userName
            + "] failed with listener [" + listener + "].", re);
      }
    });
  }

  @Async
  @Override
  public void onNewMobile(@NotBlank final String userName, @NotBlank final String newMobile) {

    final NewMobileDto dto = new NewMobileDto();
    dto.setValue(newMobile);
    httpListeners.forEach(listener -> {
      try {
        listener.onNewMobile(userName, dto);

      } catch (final RuntimeException re) {
        log.error("Publishing new mobile number [" + newMobile + "] of user profile [" + userName
            + "] failed with listener [" + listener + "].", re);
      }
    });
  }

  @Async
  @Override
  public void onDeleteMobile(@NotBlank final String userName, final String number) {

    httpListeners.forEach(listener -> {
      try {
        listener.onDeleteMobile(userName, number);

      } catch (final RuntimeException re) {
        log.error(
            "Publishing deletion of mobile number [" + number + "] of user profile [" + userName
                + "] failed with listener [" + listener + "].", re);
      }
    });
  }

  @Async
  @Override
  public void onNewRoles(
      @NotBlank final String userName,
      @NotNull final Collection<String> newRoles) {

    final NewRolesDto dto = new NewRolesDto();
    dto.setRoles(new ArrayList<>(newRoles));
    httpListeners.forEach(listener -> {
      try {
        listener.onNewRoles(userName, dto);

      } catch (final RuntimeException re) {
        log.error("Publishing new mobile number roles of user profile [" + userName
            + "] failed with listener [" + listener + "].", re);
      }
    });
  }
}
