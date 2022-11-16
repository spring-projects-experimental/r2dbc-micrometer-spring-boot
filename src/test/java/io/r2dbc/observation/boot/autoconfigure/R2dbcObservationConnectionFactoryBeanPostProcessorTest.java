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
import io.r2dbc.proxy.callback.ProxyConfig;
import io.r2dbc.proxy.callback.ProxyConfigHolder;
import io.r2dbc.proxy.observation.ObservationProxyExecutionListener;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Wrapped;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.ObjectProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link R2dbcObservationConnectionFactoryBeanPostProcessor}.
 *
 * @author Tadaya Tsuyukubo
 */
class R2dbcObservationConnectionFactoryBeanPostProcessorTest {

	@Test
	@SuppressWarnings("unchecked")
	void postProcessAfterInitialization() {
		R2dbcObservationProperties properties = new R2dbcObservationProperties();
		ObservationRegistry observationRegistry = ObservationRegistry.create();
		String url = "r2dbc:h2:mem://sa@/testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

		ObjectProvider<ObservationRegistry> objectProvider = mock(ObjectProvider.class);
		when(objectProvider.getObject()).thenReturn(observationRegistry);

		R2dbcObservationConnectionFactoryBeanPostProcessor processor = new R2dbcObservationConnectionFactoryBeanPostProcessor(
				properties, objectProvider, url);

		Object result;
		Object object = new Object();
		result = processor.postProcessAfterInitialization(object, "object-bean");
		assertThat(result).isSameAs(object);

		ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
		result = processor.postProcessAfterInitialization(connectionFactory, "connection-factory");
		assertThat(result).isNotSameAs(connectionFactory).isInstanceOfSatisfying(Wrapped.class, (wrapped) -> {
			Object unwrapped = wrapped.unwrap(ConnectionFactory.class);
			assertThat(unwrapped).isSameAs(connectionFactory);
		}).isInstanceOfSatisfying(ProxyConfigHolder.class, (holder) -> {
			ProxyConfig proxyConfig = holder.getProxyConfig();
			assertThat(proxyConfig.getListeners().getListeners()).hasSize(1).singleElement()
					.isExactlyInstanceOf(ObservationProxyExecutionListener.class);
		});
	}

}
