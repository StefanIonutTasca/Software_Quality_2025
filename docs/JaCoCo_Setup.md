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

## Accessing JaCoCo Reports in GitHub Actions

The JaCoCo code coverage reports are automatically generated and uploaded as artifacts during CI/CD pipeline runs. Here's how to access them:

1. Go to the [Actions tab](https://github.com/StefanIonutTasca/Software_Quality_2025/actions) in the GitHub repository
2. Select the "JaCoCo Code Coverage - Java CI/CD with Maven" workflow
3. Choose the latest successful run (or any specific run you're interested in)
4. Scroll down to the "Artifacts" section
5. Download the "jacoco-report" artifact
6. Extract the ZIP file to a local directory
7. Open the `index.html` file in your web browser to view the detailed coverage report

### Understanding the JaCoCo Report

The JaCoCo HTML report provides detailed coverage information:

- **Package View**: Overall coverage for each package
- **Class View**: Coverage details for each class
- **Method View**: Coverage for individual methods
- **Source View**: Line-by-line coverage highlighting in the source code

The report uses color coding:
- **Green**: Fully covered code
- **Yellow**: Partially covered code (e.g., branches)
- **Red**: Uncovered code

### Integration Tests Coverage

Our integration tests (located in `src/test/java/org/jabberpoint/integration`) verify the interaction between different components of the application. These tests contribute to the overall code coverage and help ensure that the refactored package structure maintains proper functionality across component boundaries.

### Excluded Classes

Some UI classes and the main application class are excluded from coverage requirements as they are difficult to test automatically:

```xml
<excludes>
    <exclude>**/JabberPoint.class</exclude>
    <exclude>**/MenuController.class</exclude>
    <exclude>**/DemoPresentation.class</exclude>
    <exclude>**/SlideViewerComponent.class</exclude>
    <exclude>**/SlideViewerFrame.class</exclude>
    <exclude>**/AboutBox.class</exclude>
    <exclude>**/MenuController$*ActionListener*.class</exclude>
    <exclude>**/SlideViewerFrame$*WindowAdapter*.class</exclude>
    <exclude>**/DummyComponent.class</exclude>
</excludes>
