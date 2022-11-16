/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.r2dbc.observation.boot.autoconfigure;

import io.micrometer.observation.ObservationRegistry;
import io.r2dbc.spi.ConnectionFactory;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Micrometer Observation
 * instrumentation for {@link ConnectionFactory}.
 *
 * @author Tadaya Tsuyukubo
 * @author Marcin Grzejszczak
 */
@AutoConfiguration(before = R2dbcAutoConfiguration.class)
@EnableConfigurationProperties({ R2dbcProperties.class, R2dbcObservationProperties.class })
@ConditionalOnClass({ ConnectionFactory.class, ObservationRegistry.class })
@ConditionalOnProperty(prefix = "r2dbc.observation", name = "enabled", havingValue = "true", matchIfMissing = true)
public class R2dbcObservationAutoConfiguration {

	@Bean
	public static R2dbcObservationConnectionFactoryBeanPostProcessor r2dbcObservationConnectionFactoryBeanPostProcessor(
			R2dbcObservationProperties observationProperties, ObjectProvider<ObservationRegistry> observationRegistry,
			R2dbcProperties r2dbcProperties) {
		String r2dbcUrl = observationProperties.getUrl();
		if (!StringUtils.hasText(r2dbcUrl)) {
			r2dbcUrl = r2dbcProperties.getUrl();
		}
		return new R2dbcObservationConnectionFactoryBeanPostProcessor(observationProperties, observationRegistry,
				r2dbcUrl);
	}

}
