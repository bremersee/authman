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

package org.bremersee.authman.samba.business;

import static org.bremersee.authman.samba.business.LdapEntryUtils.UF_ACCOUNT_DISABLED;
import static org.bremersee.authman.samba.business.LdapEntryUtils.UF_DONT_EXPIRE_PASSWD;
import static org.bremersee.authman.samba.business.LdapEntryUtils.UF_NORMAL_ACCOUNT;
import static org.bremersee.authman.samba.business.LdapEntryUtils.createDn;
import static org.bremersee.authman.samba.business.LdapEntryUtils.getAttributeValue;
import static org.bremersee.authman.samba.business.LdapEntryUtils.getUserAccountControl;
import static org.bremersee.authman.samba.business.LdapEntryUtils.updateAttribute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.authman.samba.SambaDomainProperties;
import org.bremersee.authman.samba.exception.AlreadyExistsException;
import org.bremersee.authman.samba.exception.LdapRuntimeException;
import org.bremersee.authman.samba.exception.NotFoundException;
import org.bremersee.swagger.authman.samba.model.Name;
import org.bremersee.swagger.authman.samba.model.Names;
import org.bremersee.swagger.authman.samba.model.Password;
import org.bremersee.swagger.authman.samba.model.SambaGroup;
import org.bremersee.swagger.authman.samba.model.SambaGroupItem;
import org.bremersee.swagger.authman.samba.model.SambaUser;
import org.bremersee.swagger.authman.samba.model.SambaUserAddRequest;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Christian Bremer
 */
@Component
@Slf4j
public class SambaConnectorServiceImpl implements SambaConnectorService {

  private final SambaDomainProperties properties;

  private final LdapEntryMapper mapper;

  private final ConnectionFactory connectionFactory;

  private final SambaTool sambaTool;

  @Autowired
  public SambaConnectorServiceImpl(
      final SambaDomainProperties properties,
      final LdapEntryMapper mapper,
      final ConnectionFactory connectionFactory,
      final SambaTool sambaTool) {

    this.properties = properties;
    this.mapper = mapper;
    this.connectionFactory = connectionFactory;
    this.sambaTool = sambaTool;
  }

  @Override
  public List<SambaGroupItem> getGroups() {

    log.info("Getting samba groups ...");
    final SearchFilter sf = new SearchFilter(properties.getGroupFindAllFilter());
    final SearchRequest sr = new SearchRequest(properties.getGroupBaseDn(), sf);
    sr.setSearchScope(properties.getGroupFindAllSearchScope());
    Connection conn = null;
    try {
      conn = getConnection();
      final SearchOperation so = new SearchOperation(conn);
      final SearchResult searchResult = so.execute(sr).getResult();
      List<SambaGroupItem> groups = searchResult.getEntries()
          .stream()
          .map(mapper::mapLdapEntryToSambaGroupItem)
          .collect(Collectors.toList());
      log.info("Getting samba groups: {} group(s) found.", groups.size());
      return groups;

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Getting samba groups failed.", lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  @Override
  public SambaGroup addGroup(@Valid final SambaGroup group) {

    log.info("Adding samba group {}", group);
    try {
      getGroupByName(group.getName());
      throw new AlreadyExistsException(group);

    } catch (final NotFoundException nfe) {

      sambaTool.addGroup(group.getName());
      final Names names = new Names();
      names.setValues(group.getMembers());
      return updateGroupMembers(group.getName(), names);
    }
  }

  private Optional<LdapEntry> findGroupByName(
      @NotNull final String groupName,
      @NotNull final Connection conn) throws LdapException {

    final SearchFilter sf = new SearchFilter(properties.getGroupFindOneFilter());
    sf.setParameters(new Object[]{groupName});
    final SearchRequest sr = new SearchRequest(properties.getGroupBaseDn(), sf);
    sr.setSearchScope(properties.getGroupFindOneSearchScope());
    final SearchOperation so = new SearchOperation(conn);
    final SearchResult searchResult = so.execute(sr).getResult();
    final LdapEntry ldapEntry = searchResult.getEntry();
    return ldapEntry == null ? Optional.empty() : Optional.of(ldapEntry);
  }

  @Override
  public SambaGroup getGroupByName(@NotNull final String groupName) {

    log.info("Getting samba group by name [{}] ...", groupName);
    Connection conn = null;
    try {
      conn = getConnection();
      final SambaGroup group = mapper.mapLdapEntryToSambaGroup(
          findGroupByName(groupName, conn).orElseThrow(NotFoundException::new));
      log.info("Getting samba group by name [{}]: {}", groupName, group);
      return group;

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Getting samba group by name failed.", lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  @Override
  public SambaGroup updateGroupMembers(
      @NotNull final String groupName,
      @Valid final Names members) {

    log.info("Updating samba group [{}] members {}", groupName, members);
    Connection conn = null;
    try {
      conn = getConnection();
      LdapEntry ldapEntry = findGroupByName(groupName, conn).orElseThrow(NotFoundException::new);

      final LdapAttribute memberAttr = ldapEntry.getAttribute(properties.getGroupMemberAttr());
      final boolean hasMemberAttr = memberAttr != null;
      final Set<String> oldUserDns = hasMemberAttr
          ? new HashSet<>(memberAttr.getStringValues())
          : new HashSet<>();
      log.debug("Updating members of group {}: old members = {}", groupName, oldUserDns);

      final Set<String> newUserDns = members.getValues()
          .stream()
          .map(name -> name.isDistinguishedName()
              ? name.getValue()
              : createDn(properties.getUserRdn(), name.getValue(), properties.getUserBaseDn()))
          .collect(Collectors.toSet());
      log.debug("Updating members of group {}: new members = {}", groupName, newUserDns);

      final Set<String> both = new HashSet<>(oldUserDns);
      both.retainAll(newUserDns);
      log.debug("Updating members of group {}: kept members = {}", groupName, both);

      oldUserDns.removeAll(both);
      log.debug("Updating members of group {}: remove old members = {}", groupName, oldUserDns);
      if (hasMemberAttr) {
        memberAttr.removeStringValues(oldUserDns);
      }

      newUserDns.removeAll(both);
      log.debug("Updating members of group {}: add new members = {}", groupName, newUserDns);
      if (hasMemberAttr) {
        memberAttr.addStringValues(newUserDns);
      } else {
        ldapEntry.addAttribute(new LdapAttribute(
            properties.getGroupMemberAttr(),
            newUserDns.toArray(new String[newUserDns.size()])));
      }

      final List<AttributeModification> mods = new ArrayList<>();
      for (final String userDn : oldUserDns) {
        final LdapAttribute remAttr = new LdapAttribute(properties.getGroupMemberAttr(), userDn);
        mods.add(new AttributeModification(AttributeModificationType.REMOVE, remAttr));
      }

      for (final String userDn : newUserDns) {
        final LdapAttribute addAttr = new LdapAttribute(properties.getGroupMemberAttr(), userDn);
        mods.add(new AttributeModification(AttributeModificationType.ADD, addAttr));
      }

      if (!mods.isEmpty()) {
        log.debug("Updating members of group {}: making {} modification(s).",
            groupName, mods.size());
        new ModifyOperation(conn).execute(
            new ModifyRequest(
                ldapEntry.getDn(),
                mods.toArray(new AttributeModification[mods.size()])));
      }

      return mapper.mapLdapEntryToSambaGroup(ldapEntry);

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Updating samba group [{}] members failed.", groupName, lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  @Override
  public void deleteGroup(@NotNull final String groupName) {

    log.info("Deleting samba group [{}] ...", groupName);
    sambaTool.deleteGroup(groupName);
  }

  @Override
  public boolean userExists(@NotNull final String userName) {

    log.info("Checking whether samba user [{}] exists.", userName);
    Connection conn = null;
    try {
      conn = getConnection();
      final boolean result = findUserByName(userName, conn).isPresent();
      log.info("Samba user [{}] exists? {}", userName, result);
      return result;

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Checking whether samba user exists failed.", lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  @Override
  public SambaUser addUser(@Valid final SambaUserAddRequest sambaUser) {

    log.info("Adding samba user {}", sambaUser);
    try {
      getUser(sambaUser.getUserName());
      log.debug("Samba user {} already exists. Updating it ...", sambaUser.getUserName());
      final Password password = new Password();
      password.setValue(sambaUser.getPassword());
      updateUserPassword(sambaUser.getUserName(), password);
      return updateUser(sambaUser.getUserName(), sambaUser);

    } catch (final NotFoundException nfe) {

      sambaTool.createUser(
          sambaUser.getUserName(),
          sambaUser.getPassword(),
          sambaUser.getDisplayName(),
          sambaUser.getEmail(),
          sambaUser.getMobile());
      final SambaUser user = updateUser(sambaUser.getUserName(), sambaUser);
      log.info("Samba user successfully added: {}", user);
      return user;
    }
  }

  private Optional<LdapEntry> findUserByName(
      @NotNull final String userName,
      @NotNull final Connection conn) throws LdapException {

    final SearchFilter sf = new SearchFilter(properties.getUserFindOneFilter());
    sf.setParameters(new Object[]{userName});
    final SearchRequest sr = new SearchRequest(properties.getUserBaseDn(), sf);
    sr.setSearchScope(properties.getUserFindOneSearchScope());
    final SearchOperation so = new SearchOperation(conn);
    final SearchResult searchResult = so.execute(sr).getResult();
    final LdapEntry ldapEntry = searchResult.getEntry();
    return ldapEntry == null ? Optional.empty() : Optional.of(ldapEntry);
  }

  @Override
  public SambaUser getUser(@NotNull final String userName) {

    log.info("Getting samba user by name [{}] ...", userName);
    Connection conn = null;
    try {
      conn = getConnection();
      final SambaUser user = mapper.mapLdapEntryToSambaUser(
          findUserByName(userName, conn).orElseThrow(NotFoundException::new));
      log.info("Getting samba user by name [{}]: {}", userName, user);
      return user;

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Getting samba user by name failed.", lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  @Override
  public SambaUser updateUser(@NotNull final String userName, @Valid final SambaUser sambaUser) {

    log.info("Updating samba user [{}]: {}", userName, sambaUser);
    Connection conn = null;
    try {
      conn = getConnection();
      final LdapEntry ldapEntry = findUserByName(userName, conn)
          .orElseThrow(NotFoundException::new);

      int userAccountControl = getUserAccountControl(ldapEntry);
      if ((userAccountControl & UF_NORMAL_ACCOUNT) != UF_NORMAL_ACCOUNT) {
        userAccountControl = userAccountControl + UF_NORMAL_ACCOUNT;
      }
      if ((userAccountControl & UF_DONT_EXPIRE_PASSWD) != UF_DONT_EXPIRE_PASSWD) {
        userAccountControl = userAccountControl + UF_DONT_EXPIRE_PASSWD;
      }
      if (sambaUser.isEnabled() &&
          ((userAccountControl & UF_ACCOUNT_DISABLED) == UF_ACCOUNT_DISABLED)) {
        userAccountControl = userAccountControl - UF_ACCOUNT_DISABLED;
      } else if (!sambaUser.isEnabled() &&
          ((userAccountControl & UF_ACCOUNT_DISABLED) != UF_ACCOUNT_DISABLED)) {
        userAccountControl = userAccountControl + UF_ACCOUNT_DISABLED;
      }

      final List<AttributeModification> mods = new ArrayList<>();
      updateAttribute(ldapEntry, "displayName", sambaUser.getDisplayName(), mods);
      updateAttribute(ldapEntry, "gecos", sambaUser.getDisplayName(), mods);
      updateAttribute(ldapEntry, "mail", sambaUser.getEmail(), mods);
      updateAttribute(ldapEntry, "telephoneNumber", sambaUser.getMobile(), mods);
      updateAttribute(
          ldapEntry, "userAccountControl", String.valueOf(userAccountControl), mods);

      if (!mods.isEmpty()) {
        log.debug("Updating samba user [{}]: making {} modification(s).",
            userName, mods.size());
        new ModifyOperation(conn).execute(
            new ModifyRequest(
                ldapEntry.getDn(),
                mods.toArray(new AttributeModification[mods.size()])));
      }

      updateUserGroups(ldapEntry, sambaUser.getGroups(), conn);

      final SambaUser user = mapper.mapLdapEntryToSambaUser(ldapEntry);
      log.info("Samba user [{}] successfully updated: {}", userName, user);
      return user;

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Getting user by name failed.", lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  private void updateUserGroups(
      @NotNull final LdapEntry ldapEntry,
      @NotNull final List<Name> groups,
      @NotNull final Connection conn) throws LdapException {

    final String userName = getAttributeValue(
        ldapEntry, "sAMAccountName", "<unknown>");
    final LdapAttribute userGroupAttr = ldapEntry.getAttribute(properties.getUserGroupAttr());
    final boolean hasUserGroupAttr = userGroupAttr != null;
    final Set<String> oldGroupDns = hasUserGroupAttr
        ? new HashSet<>(userGroupAttr.getStringValues())
        : new HashSet<>();
    log.debug("Updating groups of {}: old groups = {}", userName, oldGroupDns);

    final Set<String> newGroupDns = groups
        .stream()
        .map(name -> name.isDistinguishedName()
            ? name.getValue()
            : createDn(properties.getGroupRdn(), name.getValue(), properties.getGroupBaseDn()))
        .collect(Collectors.toSet());
    log.debug("Updating groups of {}: new groups = {}", userName, newGroupDns);

    final Set<String> both = new HashSet<>(oldGroupDns);
    both.retainAll(newGroupDns);
    log.debug("Updating groups of {}: kept groups = {}", userName, both);

    oldGroupDns.removeAll(both);
    log.debug("Updating groups of {}: remove old groups = {}", userName, oldGroupDns);
    if (hasUserGroupAttr) {
      userGroupAttr.removeStringValues(oldGroupDns);
    }

    newGroupDns.removeAll(both);
    log.debug("Updating groups of {}: add new groups = {}", userName, newGroupDns);
    if (hasUserGroupAttr) {
      userGroupAttr.addStringValues(newGroupDns);
    } else if (!newGroupDns.isEmpty()) {
      ldapEntry.addAttribute(new LdapAttribute(
          properties.getUserGroupAttr(),
          newGroupDns.toArray(new String[newGroupDns.size()])));
    }

    // We only have to modify the groups, the attribute 'memberOf' in the user entry is just a link.
    for (final String groupDn : oldGroupDns) {
      new ModifyOperation(conn).execute(
          new ModifyRequest(
              groupDn,
              new AttributeModification(
                  AttributeModificationType.REMOVE,
                  new LdapAttribute(properties.getGroupMemberAttr(), ldapEntry.getDn()))));
    }

    // We only have to modify the groups, the attribute 'memberOf' in the user entry is just a link.
    for (final String groupDn : newGroupDns) {
      new ModifyOperation(conn).execute(
          new ModifyRequest(
              groupDn,
              new AttributeModification(
                  AttributeModificationType.ADD,
                  new LdapAttribute(properties.getGroupMemberAttr(), ldapEntry.getDn()))));
    }
  }

  @Override
  public SambaUser updateUserGroups(@NotNull final String userName, @Valid final Names groups) {

    log.info("Updating samba user [{}]'s groups {}", userName, groups);
    Connection conn = null;
    try {
      conn = getConnection();
      final LdapEntry ldapEntry = findUserByName(userName, conn)
          .orElseThrow(NotFoundException::new);
      updateUserGroups(ldapEntry, groups.getValues(), conn);
      final SambaUser user = mapper.mapLdapEntryToSambaUser(ldapEntry);
      log.info("Samba user's group successfully updated: {}", user);
      return user;

    } catch (final LdapException e) {
      final LdapRuntimeException lre = new LdapRuntimeException(e);
      log.error("Getting user by name failed.", lre);
      throw lre;

    } finally {
      closeConnection(conn);
    }
  }

  @Override
  public void updateUserPassword(
      @NotNull final String userName,
      @Valid final Password newPassword) {

    log.info("Updating samba user [{}]'s password.", userName);
    sambaTool.setNewPassword(userName, newPassword.getValue());
  }

  @Override
  public void deleteUser(@NotNull final String userName) {

    log.info("Deleting samba user {}", userName);
    sambaTool.deleteUser(userName);
  }

  private Connection getConnection() throws LdapException {
    final Connection c = this.connectionFactory.getConnection();
    if (!c.isOpen()) {
      c.open();
    }
    return c;
  }

  /**
   * Close the given context and ignore any thrown exception. This is useful for typical finally
   * blocks in manual Ldap statements.
   *
   * @param connection the Ldap connection to close
   */
  private void closeConnection(final Connection connection) {
    if (connection != null && connection.isOpen()) {
      try {
        connection.close();
      } catch (final Exception ex) {
        log.warn("Closing ldap connection failed.", ex);
      }
    }
  }

}
