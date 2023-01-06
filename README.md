# Spring Boot Auto Configuration for R2DBC Observation

Provides Spring Boot 3 auto configuration for R2DBC observation with [Micrometer Observation API](https://micrometer.io/docs/observation).

## Project Setup

Released artifacts are available in the maven central. Snapshot are available in the spring snapshot repository.
- https://repo.spring.io/release
- https://repo.spring.io/snapshot (For snapshot version)

### Maven
```xml
  <!-- For snapshot
  <repositories>
    <repository>
      <id>spring-snapshots</id>
      <name>Spring Snapshots</name>
      <url>https://repo.spring.io/snapshot</url>
      <releases>
          <enabled>false</enabled>
      </releases>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
  </repositories>
  -->

  <dependencies>
    <dependency>
        <groupId>org.springframework.experimental</groupId>
        <artifactId>r2dbc-micrometer-spring-boot</artifactId>
        <version>[VERSION]</version>
    </dependency>

  <dependencies>
```

### Gradle

```groovy
// for snapshot usage
/*
repositories {
	 maven { url 'https://repo.spring.io/snapshot' }  
}
*/

dependencies {
    implementation 'org.springframework.experimental:r2dbc-micrometer-spring-boot:[VERSION]'
}
```

## How to use

Spring Boot automatically picks up the `R2dbcObservationAutoConfiguration` class when the jar file is added to the classpath.
In addition, `r2dbc.observation.enabled` property can toggle the auto configuration for the R2DBC observation.
OR, use `spring.autoconfigure.exclude` property from Spring Boot for excluding the auto configuration.

## License
This project is released under version 2.0 of the [Apache License][l].


## Development

Requires JDK 17.

### Release

Push the releasing version to the `release` branch.

[l]: https://www.apache.org/licenses/LICENSE-2.0
