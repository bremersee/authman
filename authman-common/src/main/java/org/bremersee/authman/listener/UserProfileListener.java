/*
 * Copyright 2016 the original author or authors.
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

import java.util.List;
import org.bremersee.authman.model.UserProfileCreateRequestDto;
import org.bremersee.authman.model.UserProfileDto;

/**
 * @author Christian Bremer
 */
public interface UserProfileListener {

  // post
  void onCreateUserProfile(UserProfileCreateRequestDto createRequest); // TODO

  // put
  void onChangeUserProfile(UserProfileDto newUserProfile);

  // delete
  void onDeleteUserProfile(String userName);

  // put
  void onNewPassword(String userName, String newPassword);

  // put
  void onNewEmail(String userName, String newEmail);

  // put
  void onNewMobile(String userName, String newMobile);

  // put
  void onDeleteMobile(String userName, String mobileNumber);

  // put
  void onChangeRoles(String userName, List<String> newRoles);

}
