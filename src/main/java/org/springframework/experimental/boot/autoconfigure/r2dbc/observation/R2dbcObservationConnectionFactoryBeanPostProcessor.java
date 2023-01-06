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

package org.springframework.experimental.boot.autoconfigure.r2dbc.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.ObservationRegistry;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.observation.ObservationProxyExecutionListener;
import io.r2dbc.spi.ConnectionFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.util.StringUtils;

/**
 * Create a {@link ConnectionFactory} proxy bean that provides observation with Micrometer
 * Observation API.
 *
 * @author Tadaya Tsuyukubo
 * @author Marcin Grzejszczak
 */
public class R2dbcObservationConnectionFactoryBeanPostProcessor implements BeanPostProcessor {

	private final ObjectProvider<ObservationRegistry> observationRegistryProvider;

	private final ObjectProvider<R2dbcObservationProperties> r2dbcObservationPropertiesProvider;

	private final ObjectProvider<R2dbcProperties> r2dbcPropertiesProvider;

	public R2dbcObservationConnectionFactoryBeanPostProcessor(ObjectProvider<ObservationRegistry> observationRegistryProvider,
			ObjectProvider<R2dbcObservationProperties> r2dbcObservationPropertiesProvider,
			ObjectProvider<R2dbcProperties> r2dbcPropertiesProvider) {
		this.observationRegistryProvider = observationRegistryProvider;
		this.r2dbcObservationPropertiesProvider = r2dbcObservationPropertiesProvider;
		this.r2dbcPropertiesProvider = r2dbcPropertiesProvider;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ConnectionFactory connectionFactory) {
			String url = getUrl();
			boolean includeParamValues = this.r2dbcObservationPropertiesProvider.getObject().isIncludeParameterValues();

			ObservationProxyExecutionListener listener = new ObservationProxyExecutionListener(
					this.observationRegistryProvider.getObject(), connectionFactory, url);
			listener.setIncludeParameterValues(includeParamValues);
			ConnectionFactory proxyConnectionFactory = ProxyConnectionFactory.builder(connectionFactory)
					.listener(listener).build();
			return proxyConnectionFactory;
		}
		return bean;
	}

	@Nullable
	private String getUrl() {
		String r2dbcUrl = r2dbcObservationPropertiesProvider.getObject().getUrl();
		if (!StringUtils.hasText(r2dbcUrl)) {
			r2dbcUrl = r2dbcPropertiesProvider.getObject().getUrl();
		}
		return r2dbcUrl;
	}

}
