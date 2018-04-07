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

package org.bremersee.authman.business;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.authman.domain.UserProfile;
import org.bremersee.authman.model.SambaSettingsDto;
import org.bremersee.swagger.authman.samba.api.SambaConnectorControllerApi;
import org.bremersee.swagger.authman.samba.model.BooleanWrapper;
import org.bremersee.swagger.authman.samba.model.Name;
import org.bremersee.swagger.authman.samba.model.Names;
import org.bremersee.swagger.authman.samba.model.Password;
import org.bremersee.swagger.authman.samba.model.SambaGroup;
import org.bremersee.swagger.authman.samba.model.SambaGroupItem;
import org.bremersee.swagger.authman.samba.model.SambaUser;
import org.bremersee.swagger.authman.samba.model.SambaUserAddRequest;
import org.bremersee.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class SambaConnectorServiceImpl implements SambaConnectorService {

  private final SambaConnectorControllerApi sambaConnector;

  @Autowired
  public SambaConnectorServiceImpl(
      final SambaConnectorControllerApi sambaConnector) {
    this.sambaConnector = sambaConnector;
  }

  @Override
  public SambaGroup addGroup(@NotNull SambaGroup group) {
    final SambaGroup result = sambaConnector.addGroup(group).getBody();
    Assert.notNull(result, "Result must not be null."); // NOSONAR
    return result;
  }

  @Override
  public void deleteGroup(@NotNull String groupName) {
    sambaConnector.deleteGroup(groupName);
  }

  @Override
  public SambaGroup getGroupByName(@NotNull String groupName) {
    final SambaGroup result = sambaConnector.getGroupByName(groupName).getBody();
    Assert.notNull(result, "Result must not be null.");
    return result;
  }

  @Override
  public List<SambaGroupItem> getGroups() {
    final List<SambaGroupItem> result = sambaConnector.getGroups().getBody();
    Assert.notNull(result, "Result must not be null.");
    return result;
  }

  @Override
  public SambaGroup updateGroupMembers(@NotNull String groupName,
      @NotNull Collection<String> members) {
    final Names memberNames = new Names();
    memberNames.setValues(members
        .stream()
        .map(memberName -> {
          final Name name = new Name();
          name.setValue(memberName);
          name.setDistinguishedName(true);
          return name;
        })
        .collect(Collectors.toList()));
    final SambaGroup result = sambaConnector.updateGroupMembers(groupName, memberNames).getBody();
    Assert.notNull(result, "Result must not be null.");
    return result;
  }


  @Async
  @Override
  public void addSambaUserAsync(@NotNull final UserProfile user, @NotNull final String password) {
    addSambaUser(user, password);
  }

  @Override
  public void addSambaUser(@NotNull final UserProfile user, @NotNull final String password) {
    final SambaUserAddRequest sambaUserAddRequest = new SambaUserAddRequest();
    sambaUserAddRequest.setUserName(user.getUserName());
    sambaUserAddRequest.setPassword(password);
    sambaUserAddRequest.setEnabled(user.isEnabled());
    sambaUserAddRequest.setDisplayName(user.getDisplayName());
    sambaUserAddRequest.setEmail(user.getEmail());
    sambaUserAddRequest.setMobile(user.getMobile());
    sambaUserAddRequest.setGroups(
        user.getSambaSettings().getSambaGroups()
            .stream()
            .map(groupName -> {
              final Name name = new Name();
              name.setDistinguishedName(true);
              name.setValue(groupName);
              return name;
            })
            .collect(Collectors.toList()));
    final SambaUser sambaUser = sambaConnector.addUser(sambaUserAddRequest).getBody();

    if (sambaUser != null && sambaUser.getGroups() != null) {
      final SambaSettingsDto sambaSettings = new SambaSettingsDto();
      sambaSettings.setSambaGroups(
          sambaUser.getGroups()
              .stream()
              .map(Name::getValue)
              .collect(Collectors.toList()));
      user.setSambaSettings(sambaSettings);
    }
  }

  @Async
  @Override
  public void updateSambaUserAsync(@NotNull final UserProfile user) {
    updateSambaUser(user);
  }

  @Override
  public void updateSambaUser(@NotNull final UserProfile user) {

    // if samba settings == null && samba user exists
    // -> disable samba user

    // if samba settings != null && samba user does not exist
    // -> create a new samba user with random password

    final boolean sambaUserExists = userExists(user.getUserName());

    if (user.getSambaSettings() != null || sambaUserExists) {

      if (user.getSambaSettings() != null && !sambaUserExists) {
        addSambaUser(user,
            PasswordUtils.createRandomClearPassword(
                14, false, false));
      } else {

        final SambaUser sambaUser = new SambaUser();
        sambaUser.setUserName(user.getUserName());
        sambaUser.setEnabled(user.isEnabled());
        sambaUser.setDisplayName(user.getDisplayName());
        sambaUser.setEmail(user.getEmail());
        sambaUser.setMobile(user.getMobile());
        if (user.getSambaSettings() != null) {
          sambaUser.setGroups(user.getSambaSettings()
              .getSambaGroups()
              .stream()
              .map(
                  groupName -> {
                    final Name name = new Name();
                    name.setValue(groupName);
                    name.setDistinguishedName(true);
                    return name;
                  })
              .collect(Collectors.toList()));
        }

        if (user.getSambaSettings() == null) {
          sambaUser.setEnabled(false);
        }
        sambaConnector.updateUser(user.getUserName(), sambaUser);
      }
    }

  }

  @Override
  public boolean userExists(@NotNull String userName) {
    final BooleanWrapper wrapper = sambaConnector.userExists(userName).getBody();
    return wrapper != null && Boolean.TRUE.equals(wrapper.isValue());
  }

  @Override
  public void deleteUser(@NotNull String userName) {
    if (userExists(userName)) {
      sambaConnector.deleteUser(userName);
    }
  }

  @Async
  @Override
  public void deleteUserAsync(@NotNull String userName) {
    deleteUser(userName);
  }

  @Override
  public SambaUser getUser(@NotNull String userName) {
    final SambaUser result = sambaConnector.getUser(userName).getBody();
    Assert.notNull(result, "Result must not be null.");
    return result;
  }

  @Override
  public SambaUser updateUserGroups(@NotNull String userName, @NotNull Collection<String> groups) {
    final Names groupNames = new Names();
    groupNames.setValues(groups
        .stream()
        .map(groupName -> {
          final Name name = new Name();
          name.setDistinguishedName(true);
          name.setValue(groupName);
          return name;
        })
        .collect(Collectors.toList()));
    final SambaUser result = sambaConnector.updateUserGroups(userName, groupNames).getBody();
    Assert.notNull(result, "Result must not be null.");
    return result;
  }

  @Override
  public void updateUserPassword(@NotNull String userName, @NotNull String newPassword) {
    final Password password = new Password();
    password.setValue(newPassword);
    sambaConnector.updateUserPassword(userName, password);
  }

  @Async
  @Override
  public void updateUserPasswordAsync(@NotNull String userName, @NotNull String newPassword) {
    updateUserPassword(userName, newPassword);
  }

}
