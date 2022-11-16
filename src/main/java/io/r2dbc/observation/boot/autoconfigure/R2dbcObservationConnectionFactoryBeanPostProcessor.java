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

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.ObservationRegistry;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.observation.ObservationProxyExecutionListener;
import io.r2dbc.spi.ConnectionFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Create a {@link ConnectionFactory} proxy bean that provides observation with Micrometer
 * Observation API.
 *
 * @author Tadaya Tsuyukubo
 * @author Marcin Grzejszczak
 */
public class R2dbcObservationConnectionFactoryBeanPostProcessor implements BeanPostProcessor {

	private final R2dbcObservationProperties observationProperties;

	private final ObjectProvider<ObservationRegistry> observationRegistry;

	private final String url;

	public R2dbcObservationConnectionFactoryBeanPostProcessor(R2dbcObservationProperties observationProperties,
			ObjectProvider<ObservationRegistry> observationRegistry, @Nullable String url) {
		this.observationProperties = observationProperties;
		this.observationRegistry = observationRegistry;
		this.url = url;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ConnectionFactory connectionFactory) {
			ObservationProxyExecutionListener listener = new ObservationProxyExecutionListener(
					this.observationRegistry.getObject(), connectionFactory, this.url);
			listener.setIncludeParameterValues(this.observationProperties.isIncludeParameterValues());
			ConnectionFactory proxyConnectionFactory = ProxyConnectionFactory.builder(connectionFactory)
					.listener(listener).build();
			return proxyConnectionFactory;
		}
		return bean;
	}

}
