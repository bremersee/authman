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

package org.bremersee.authman.mailcow.controller;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.authman.mailcow.domain.MailboxRepository;
import org.bremersee.authman.security.crypto.password.PasswordEncoder;
import org.bremersee.swagger.authman.listener.api.UserProfileListenerApi;
import org.bremersee.swagger.authman.listener.model.EnabledDto;
import org.bremersee.swagger.authman.listener.model.NewEmailDto;
import org.bremersee.swagger.authman.listener.model.NewMobileDto;
import org.bremersee.swagger.authman.listener.model.NewPasswordDto;
import org.bremersee.swagger.authman.listener.model.NewRolesDto;
import org.bremersee.swagger.authman.listener.model.UserProfileChangeEventDto;
import org.bremersee.swagger.authman.listener.model.UserProfileCreationEventDto;
import org.bremersee.swagger.authman.listener.model.UserProfileRegistrationRequestEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Christian Bremer
 */
@SuppressWarnings("MVCPathVariableInspection")
@RestController
@Slf4j
public class UserProfileListenerController implements UserProfileListenerApi {

  private final MailboxRepository mailboxRepository;

  private final PasswordEncoder passwordEncoder;

  @Value("${bremersee.domain:}")
  private String domain;

  @Autowired
  public UserProfileListenerController(
      final MailboxRepository mailboxRepository,
      final PasswordEncoder passwordEncoder) {
    this.mailboxRepository = mailboxRepository;
    this.passwordEncoder = passwordEncoder;
  }

  private String buildUserName(String userName) {
    final String mailbox;
    if (StringUtils.hasText(domain)) {
      final boolean domainStartsWithAt = domain.startsWith("@");
      if (domainStartsWithAt) {
        mailbox = userName + domain;
      } else {
        mailbox = userName + "@" + domain;
      }
    } else {
      mailbox = userName;
    }
    return mailbox;
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onUserRegistrationRequest(
      @Valid @RequestBody UserProfileRegistrationRequestEventDto userRegistrationRequest) {

    log.debug("Got registration request event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onCreateUserProfile(
      @Valid @RequestBody UserProfileCreationEventDto createRequest) {

    log.debug("Got user creation event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onChangeUserProfile(
      @Valid @RequestBody UserProfileChangeEventDto newUserProfile) {

    log.debug("Got user change event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onDeleteUserProfile(
      @PathVariable("userName") String userName) {

    log.debug("Got user delete event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onChangeEnabledState(
      @PathVariable("userName") String userName,
      @Valid @RequestBody EnabledDto newEnabledState) {

    mailboxRepository
        .findByUserName(buildUserName(userName))
        .ifPresent(mailbox -> {
          final boolean enabled = Boolean.TRUE.equals(newEnabledState.isValue());
          log.info("Setting mailbox [{}] to {}.",
              mailbox.getUserName(), enabled ? "active" : "inactive");
          mailbox.setActive(enabled);
          mailboxRepository.save(mailbox);
        });

    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onNewPassword(
      @PathVariable("userName") String userName,
      @Valid @RequestBody NewPasswordDto newPassword) {

    mailboxRepository
        .findByUserName(buildUserName(userName))
        .ifPresent(mailbox -> {
          final String encryptedPassword = passwordEncoder.encode(newPassword.getValue());
          log.info("Setting password of mailbox [{}].", mailbox.getUserName());
          mailbox.setPassword(encryptedPassword);
          mailboxRepository.save(mailbox);
        });

    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onNewEmail(
      @PathVariable("userName") String userName,
      @Valid @RequestBody NewEmailDto newEmail) {

    log.debug("Got new email event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onNewMobile(
      @PathVariable("userName") String userName,
      @Valid @RequestBody NewMobileDto newMobile) {

    log.debug("Got new mobile number event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onDeleteMobile(
      @PathVariable("userName") String userName,
      @RequestParam(value = "number", required = false) String number) {

    log.debug("Got mobile number delete event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("#oauth2.hasScope('profile:sync')")
  @Override
  public ResponseEntity<Void> onNewRoles(
      @PathVariable("userName") String userName,
      @Valid NewRolesDto newRoles) {

    log.debug("Got new roles event ... nothing to do.");
    return ResponseEntity.ok().build();
  }

}
