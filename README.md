# Spring Boot Auto Configuration for R2DBC Observation

Provides Spring Boot 3 auto configuration for R2DBC observation with [Micrometer Observation API](https://micrometer.io/docs/observation).

## Project Setup

The maven artifacts are available in the spring release/snapshot repositories.

### Maven
```xml
  <repositories>
    <repository>
      <id>spring-releases</id>
      <name>Spring Releases</name>
      <url>https://repo.spring.io/release</url>
      <releases>
          <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>

    <!-- For snapshot
    <repository>
      <id>spring-snapshots</id>
      <name>Spring Snapshots</name>
      <url>https://repo.spring.io/snapshot</url>
      <releases>
          <enabled>false</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>
    -->
  </repositories>

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
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/release" }
    // maven { url "https://oss.sonatype.org/content/repositories/snapshots" } // for snapshot usage
}

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
