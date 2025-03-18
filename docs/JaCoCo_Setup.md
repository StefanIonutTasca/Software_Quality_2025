# JaCoCo Code Coverage Setup

This document explains how JaCoCo is set up in the Jabberpoint project to measure code coverage.

## What is JaCoCo?

JaCoCo (Java Code Coverage) is an open-source toolkit for measuring and reporting Java code coverage. It helps identify which parts of your code are executed during tests and which parts remain untested.

## Configuration in Our Project

JaCoCo is configured in our `pom.xml` file:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## How to Run JaCoCo

JaCoCo automatically runs as part of the Maven test phase. To generate a coverage report:

```
mvn clean test
```

This runs all tests and generates a JaCoCo report in `target/site/jacoco/`.

## Coverage Thresholds (Future Enhancement)

In a future update, we plan to add coverage thresholds to ensure minimum coverage requirements are met:

```xml
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

## Reading JaCoCo Reports

The JaCoCo HTML report provides:

1. **Line Coverage**: The percentage of code lines executed
2. **Branch Coverage**: The percentage of branches executed (if/switch conditions)
3. **Method Coverage**: The percentage of methods called
4. **Class Coverage**: The percentage of classes used

## CI/CD Integration

Our GitHub Actions workflow automatically generates and uploads the JaCoCo report as an artifact for each build, making it easy to track coverage metrics over time.
