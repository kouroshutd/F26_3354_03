# SafeGuard - SafeWalkSession unit (Deliverable 2, Section 4.2)

Unit under test: `SafeWalkSession` (FR-09, FR-10, FR-11, FR-12, FR-17).
Tests: `SafeWalkSessionTest` - 12 JUnit 5 tests (TC-01 to TC-12, Section 4.3).

## 1. Install dependencies
- Java JDK 17 or newer
  - macOS: `brew install openjdk@17`
  - Ubuntu: `sudo apt install openjdk-17-jdk`
  - Windows: install from https://adoptium.net
- Apache Maven 3.8 or newer
  - macOS: `brew install maven`
  - Ubuntu: `sudo apt install maven`
  - Windows: https://maven.apache.org/download.cgi
- Check: `java -version` and `mvn -version`

JUnit 5 is downloaded automatically by Maven from `pom.xml`.

## 2. Configure environment variables
None are required. If `mvn` cannot find Java, set `JAVA_HOME` to your JDK folder:
- macOS: `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`
- Linux: `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` (path may differ)

## 3. Run the project locally
From this folder (the one containing `pom.xml`):

```
mvn test
```

Expected result: `Tests run: 12, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`.
Detailed reports are written to `target/surefire-reports/`.
