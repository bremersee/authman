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

package org.bremersee.swagger.authman.samba;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.swagger.authman.samba.api.SambaConnectorControllerApi;
import org.bremersee.swagger.authman.samba.model.BooleanWrapper;
import org.bremersee.swagger.authman.samba.model.Names;
import org.bremersee.swagger.authman.samba.model.Password;
import org.bremersee.swagger.authman.samba.model.SambaGroup;
import org.bremersee.swagger.authman.samba.model.SambaGroupItem;
import org.bremersee.swagger.authman.samba.model.SambaUser;
import org.bremersee.swagger.authman.samba.model.SambaUserAddRequest;
import org.springframework.http.ResponseEntity;

/**
 * @author Christian Bremer
 */
@Slf4j
public class SambaConnectorMock implements SambaConnectorControllerApi {

  private SambaGroup createGroup(String name) {
    final SambaGroup group = new SambaGroup();
    group.setCreated(OffsetDateTime.now());
    group.setDistinguishedName("cn=" + name);
    group.setMembers(new ArrayList<>());
    group.setModified(OffsetDateTime.now());
    group.setName(name);
    return group;
  }

  private SambaUser createUser(String name) {
    final SambaUser user = new SambaUser();
    user.setCreated(OffsetDateTime.now());
    user.setDistinguishedName("cn=" + name);
    user.setModified(OffsetDateTime.now());
    user.setDisplayName("Mock User");
    user.setEmail("mock@example.org");
    user.setEnabled(true);
    user.setGroups(new ArrayList<>());
    user.setMobile("015158056211");
    user.setUserName(name);
    return user;
  }

  @Override
  public ResponseEntity<SambaGroup> addGroup(@Valid final SambaGroup group) {
    log.info("Samba connector MOCK is adding samba group {}", group);
    return ResponseEntity.ok(group);
  }

  @Override
  public ResponseEntity<SambaUser> addUser(@Valid final SambaUserAddRequest sambaUser) {
    log.info("Samba connector MOCK is adding samba user {}", sambaUser);
    return ResponseEntity.ok(sambaUser);
  }

  @Override
  public ResponseEntity<Void> deleteGroup(String groupName) {
    log.info("Samba connector MOCK is deleting samba group {}", groupName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteUser(String userName) {
    log.info("Samba connector MOCK is deleting samba user {}", userName);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<SambaGroup> getGroupByName(String groupName) {
    log.info("Samba connector MOCK is getting samba group {}", groupName);
    return ResponseEntity.ok(createGroup(groupName));
  }

  @Override
  public ResponseEntity<List<SambaGroupItem>> getGroups() {
    log.info("Samba connector MOCK is getting samba groups.");
    return ResponseEntity.ok(Collections.singletonList(createGroup("Administrators")));
  }

  public ResponseEntity<BooleanWrapper> userExists(String userName) {
    log.info("Samba connector MOCK is getting samba user {} exists?", userName);
    final BooleanWrapper wrapper = new BooleanWrapper();
    wrapper.setValue(true);
    return ResponseEntity.ok(wrapper);
  }

  @Override
  public ResponseEntity<SambaUser> getUser(String userName) {
    log.info("Samba connector MOCK is getting samba user {}", userName);
    return ResponseEntity.ok(createUser(userName));
  }

  @Override
  public ResponseEntity<SambaGroup> updateGroupMembers(String groupName, @Valid Names members) {
    log.info("Samba connector MOCK is updating samba group {} members: {}", groupName, members);
    return ResponseEntity.ok(createGroup(groupName));
  }

  @Override
  public ResponseEntity<SambaUser> updateUser(String userName, @Valid SambaUser sambaUser) {
    log.info("Samba connector MOCK is updating samba user {}", userName);
    return ResponseEntity.ok(createUser(userName));
  }

  @Override
  public ResponseEntity<SambaUser> updateUserGroups(String userName, @Valid Names groups) {
    log.info("Samba connector MOCK is updating samba user {} groups: {}", userName, groups);
    return ResponseEntity.ok(createUser(userName));
  }

  @Override
  public ResponseEntity<Void> updateUserPassword(String userName, @Valid Password newPassword) {
    log.info("Samba connector MOCK is setting new samba user {} password.", userName);
    return ResponseEntity.ok().build();
  }
}
