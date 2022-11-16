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

import java.util.List;
import java.util.stream.Collectors;

import brave.test.TestSpanHandler;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.brave.bridge.BraveFinishedSpan;
import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.test.simple.SpanAssert;
import io.micrometer.tracing.test.simple.SpansAssert;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test.
 *
 * @author Tadaya Tsuyukubo
 */
@SpringBootTest(properties = {
		// @formatter:off
		// embedded DB
		"spring.sql.init.schema-locations=classpath:itest-schema.sql",
		"spring.sql.init.data-locations=classpath:itest-data.sql",
		// tracing
		"management.tracing.sampling.probability=1.0",
		// for debugging, log spans
		"logging.level.brave.Tracer=INFO" },
		// @formatter:on
		args = "--debug")
@AutoConfigureObservability // enable tracing in test
class R2dbcObservationAutoConfigurationIntegrationTest {

	@Autowired
	ObservationRegistry observationRegistry;

	@Autowired
	TestSpanHandler testSpanHandler;

	@Autowired
	MyService myService;

	@Test
	void bootIntegration() {
		List<FinishedSpan> spans = getFinishedSpans();

		// verify initial table/data creation
		SpansAssert.assertThat(getFinishedSpans()).hasNumberOfSpansWithNameEqualTo("query", 3)
				.assertThatASpanWithNameEqualTo("query").hasTagWithKey("r2dbc.query[0]");
		List<FinishedSpan> querySpans = getFinishedSpans().stream().filter((span) -> "query".equals(span.getName()))
				.toList();
		SpanAssert.assertThat(querySpans.get(0)).hasTag("r2dbc.query[0]", "CREATE TABLE emp(id INT, name VARCHAR(20))");
		SpansAssert.assertThat(querySpans.subList(1, 3)).allSatisfy((span) -> {
			SpanAssert.assertThat(span).hasTagWithKey("r2dbc.query[0]");
		});
		this.testSpanHandler.clear();

		// start parent observation. note: it's not scoped
		Observation parentObservation = Observation.start("parent-obs", this.observationRegistry);

		// perform business operation
		String str = myService.perform(parentObservation).block();
		assertThat(str).isEqualTo("DEF");

		parentObservation.stop();

		// @formatter:off
		SpansAssert.assertThat(getFinishedSpans())
				.hasNumberOfSpansEqualTo(2)
				.haveSameTraceId()
				.hasASpanWithName("query", (spanAssert) -> {
					spanAssert.hasTag("r2dbc.query[0]", "SELECT id, name FROM emp WHERE id = ?")
							.doesNotHaveTagWithKey("r2dbc.param[0]");
				})
		;
		// @formatter:on

	}

	private List<FinishedSpan> getFinishedSpans() {
		return this.testSpanHandler.spans().stream().map(BraveFinishedSpan::fromBrave).collect(Collectors.toList());
	}

	@SpringBootApplication
	static class MyApplication {

		@Bean
		public TestSpanHandler spanHandler() {
			return new TestSpanHandler();
		}

		@Bean
		public MyService myService(ConnectionFactory connectionFactory) {
			return new MyService(connectionFactory);
		}

	}

	static class MyService {

		private final ConnectionFactory connectionFactory;

		public MyService(ConnectionFactory connectionFactory) {
			this.connectionFactory = connectionFactory;
		}

		public Mono<String> perform(Observation parentObservation) {
			// @formatter:off
			return Mono.from(connectionFactory.create())
					.flatMapMany(connection -> connection.createStatement("SELECT id, name FROM emp WHERE id = ?")
							.bind(0, 2).execute())
					.flatMap(result -> result.map((row, rowMetadata) -> row.get("name", String.class)))
					.single()
					.subscribeOn(Schedulers.boundedElastic())
					.contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, parentObservation))
					.log();
			// @formatter:on
		}

	}

}
