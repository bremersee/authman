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

package org.bremersee.authman.samba;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.ConnectionInitializer;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.ConnectionPool;
import org.ldaptive.pool.IdlePruneStrategy;
import org.ldaptive.pool.PoolConfig;
import org.ldaptive.pool.PooledConnectionFactory;
import org.ldaptive.pool.PruneStrategy;
import org.ldaptive.pool.SearchValidator;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author Christian Bremer
 */
@Configuration
@EnableConfigurationProperties(LdaptiveProperties.class)
@Slf4j
public class LdaptiveConfiguration {

  private final LdaptiveProperties properties;

  @Autowired
  public LdaptiveConfiguration(LdaptiveProperties ldaptiveProperties) {
    this.properties = ldaptiveProperties;
  }

  @Bean
  public ConnectionFactory connectionFactory() {

    if (properties.isPooled()) {
      return pooledConnectionFactory();
    }
    return defaultConnectionFactory();
  }

  private DefaultConnectionFactory defaultConnectionFactory() { // Provider<?> provider
    DefaultConnectionFactory factory = new DefaultConnectionFactory();
    factory.setConnectionConfig(connectionConfig());
    return factory;
  }

  private ConnectionConfig connectionConfig() {

    ConnectionConfig cc = new ConnectionConfig();
    cc.setLdapUrl(properties.getLdapUrl());

    if (properties.getConnectTimeout() > 0L) {
      cc.setConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()));
    }
    if (properties.getResponseTimeout() > 0L) {
      cc.setResponseTimeout(Duration.ofMillis(properties.getResponseTimeout()));
    }

    cc.setUseSSL(properties.isUseSsl());
    cc.setUseStartTLS(properties.isUseStartTls());

    if ((properties.isUseSsl() || properties.isUseStartTls()) && hasSslConfig()) {
      cc.setSslConfig(sslConfig());
    }

    // binds all operations to a dn
    if (StringUtils.hasText(properties.getBindDn())) {
      cc.setConnectionInitializer(connectionInitializer());
    }

    return cc;
  }

  private SslConfig sslConfig() {
    SslConfig sc = new SslConfig();
    sc.setCredentialConfig(sslCredentialConfig());
    return sc;
  }

  private CredentialConfig sslCredentialConfig() {
    X509CredentialConfig x509 = new X509CredentialConfig();
    if (StringUtils.hasText(properties.getAuthenticationCertificate())) {
      x509.setAuthenticationCertificate(properties.getAuthenticationCertificate());
    }
    if (StringUtils.hasText(properties.getAuthenticationKey())) {
      x509.setAuthenticationKey(properties.getAuthenticationKey());
    }
    if (StringUtils.hasText(properties.getTrustCertificates())) {
      x509.setTrustCertificates(properties.getTrustCertificates());
    }
    return x509;
  }

  private boolean hasSslConfig() {
    return StringUtils.hasText(properties.getTrustCertificates())
        || StringUtils.hasText(properties.getAuthenticationCertificate())
        || StringUtils.hasText(properties.getAuthenticationKey());
  }

  private ConnectionInitializer connectionInitializer() {
    // sasl is not supported at the moment
    BindConnectionInitializer bci = new BindConnectionInitializer();
    bci.setBindDn(properties.getBindDn());
    bci.setBindCredential(new Credential(properties.getBindCredential()));
    return bci;
  }

  private PooledConnectionFactory pooledConnectionFactory() {
    PooledConnectionFactory factory = new PooledConnectionFactory();
    factory.setConnectionPool(connectionPool());
    return factory;
  }

  private ConnectionPool connectionPool() {
    BlockingConnectionPool pool = new BlockingConnectionPool();
    pool.setConnectionFactory(defaultConnectionFactory());
    pool.setPoolConfig(poolConfig());
    pool.setPruneStrategy(pruneStrategy());
    pool.setValidator(searchValidator());
    if (properties.getBlockWaitTime() > 0L) {
      pool.setBlockWaitTime(Duration.ofMillis(properties.getBlockWaitTime()));
    }
    pool.initialize();
    return pool;
  }

  private PoolConfig poolConfig() {
    PoolConfig pc = new PoolConfig();
    pc.setMaxPoolSize(properties.getMaxPoolSize());
    pc.setMinPoolSize(properties.getMinPoolSize());
    pc.setValidateOnCheckIn(properties.isValidateOnCheckIn());
    pc.setValidateOnCheckOut(properties.isValidateOnCheckOut());
    if (properties.getValidatePeriod() > 0L) {
      pc.setValidatePeriod(Duration.ofSeconds(properties.getValidatePeriod()));
    }
    pc.setValidatePeriodically(properties.isValidatePeriodically());
    return pc;
  }

  private PruneStrategy pruneStrategy() {
    // there may be other ways
    IdlePruneStrategy ips = new IdlePruneStrategy();
    if (properties.getIdleTime() > 0L) {
      ips.setIdleTime(Duration.ofSeconds(properties.getIdleTime()));
    }
    if (properties.getPrunePeriod() > 0L) {
      ips.setPrunePeriod(Duration.ofSeconds(properties.getPrunePeriod()));
    }
    return ips;
  }

  private SearchValidator searchValidator() {
    return properties.getSearchValidator();
  }

}
