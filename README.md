# Spring Boot Auto Configuration for R2DBC Observation

Provides Spring Boot 3 auto configuration for R2DBC observation with [Micrometer Observation API](https://micrometer.io/docs/observation).

## How to use

Spring Boot automatically picks up the `R2dbcObservationAutoConfiguration` class when the jar file is added to the classpath.
In addition, `r2dbc.observation.enabled` property can toggle the auto configuration for the R2DBC observation.
OR, use `spring.autoconfigure.exclude` property from Spring Boot for excluding the auto configuration.

## License
This project is released under version 2.0 of the [Apache License][l].


## Development

Requires JDK 17.

[l]: https://www.apache.org/licenses/LICENSE-2.0
