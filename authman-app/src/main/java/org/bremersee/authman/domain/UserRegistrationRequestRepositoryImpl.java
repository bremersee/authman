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

package org.bremersee.authman.domain;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author Christian Bremer
 */
public class UserRegistrationRequestRepositoryImpl extends AbstractMongoRepositoryImpl implements
    UserRegistrationRequestRepositoryCustom {

  public UserRegistrationRequestRepositoryImpl(
      @NotNull final MongoOperations mongoOperations) {
    super(mongoOperations);
  }

  @Override
  public List<UserRegistrationRequest> findExpiredAndRemove() {

    Query query = new Query();
    query.addCriteria(Criteria.where("registrationExpiration").lt(new Date()));
    return getMongoOperations().findAllAndRemove(query, UserRegistrationRequest.class);
  }
}
