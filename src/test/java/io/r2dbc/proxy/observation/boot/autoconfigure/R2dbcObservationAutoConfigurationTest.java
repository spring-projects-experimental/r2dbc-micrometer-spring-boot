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

package io.r2dbc.proxy.observation.boot.autoconfigure;

import io.micrometer.observation.ObservationRegistry;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link R2dbcObservationAutoConfiguration}.
 *
 * @author Tadaya Tsuyukubo
 */
class R2dbcObservationAutoConfigurationTest {

	@Test
	void defaultEnabled() {
		new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(R2dbcObservationAutoConfiguration.class))
				.withBean(ConnectionFactory.class, () -> mock(ConnectionFactory.class))
				.withInitializer(new ConditionEvaluationReportLoggingListener())
				.withBean(ObservationRegistry.class, ObservationRegistry::create).run((context) -> {
					assertThat(context).hasSingleBean(R2dbcObservationConnectionFactoryBeanPostProcessor.class);
				});
	}

}
