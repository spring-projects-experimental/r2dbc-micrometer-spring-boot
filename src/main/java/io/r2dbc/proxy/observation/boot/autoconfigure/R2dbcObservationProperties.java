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

import io.micrometer.common.lang.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for R2DBC instrumentation.
 *
 * @author Tadaya Tsuyukubo
 */
@ConfigurationProperties("r2dbc.observation")
public class R2dbcObservationProperties {

	/**
	 * R2DBC url to be used for remote-service information in observation. If this value
	 * is not set, it falls back to the "spring.r2dbc.url" property. NOTE: this value does
	 * not affect actual connection factory url.
	 */
	@Nullable
	private String url;

	/**
	 * Whether to tag actual query parameter values.
	 */
	private boolean includeParameterValues;

	@Nullable
	public String getUrl() {
		return this.url;
	}

	public void setUrl(@Nullable String url) {
		this.url = url;
	}

	public boolean isIncludeParameterValues() {
		return this.includeParameterValues;
	}

	public void setIncludeParameterValues(boolean includeParameterValues) {
		this.includeParameterValues = includeParameterValues;
	}

}
