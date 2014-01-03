Rocket-Config Library
=======================

_Configuration library for applications when ``java.util.Properties`` is not enough._

Overview
--------

This library fills the gap between a configuration file and runtime configuration values. It introduces section-based
customizable configuration file formats. Once file parsing is completed, an application has a runtime configuration
object that it can use to configure itself.

Sample configuration file:

    [general]
    # name-value pairs:
    name    = HelloWorld
    url     = https://www.example.org/
    timeout = 10000

    [entities]
    # 3 classes:
    org.example.entity.User
    org.example.entity.UserGroup
    org.example.entity.Widget

    [filters ]
    # 1 filter (an instance of this class):
    org.example.filter.AuditFilter

Sample configuration object:

```java
public class AcmeConfig {
  private String name;
  private String url;
  private int timeout = 20000;
  private Class[] entities;
  private Filter[] filters;

  public void setName(String name) { this.name = name; }
  public void setUrl(String url) { this.url = url; }
  public void setTimeout(int timeout) { this.timeout = timeout; }
  public void setEntities(Class[] entities) { this.entities = entities; }
  public void setFilters(Filter[] filters) { this.filters = filters; }
}
```

Sample _Rocket-Config_ usage: describes configuration file model according to which the stream is parsed, and data will
be stored in a new instance of target type.

```java
ConfigModel<AcmeConfig> model = ConfigModel.expect(AcmeConfig.class)
    .section("general").ofMap().storeIn("name", "url", "timeout")
    .section("entities").ofList(Class.class).storeIn("entities")
    .section("filters").ofList(Filter.class).storeIn("filters")
    .ready();

AcmeConfig conf = model.parse(getClass().getResourceAsStream("/acme.conf"));
```

As shown in the example, ``ws.rocket.config.ConfigModel`` class provides the main functionality to use and usually that's enough.

### Dependencies ###

_Rocket-Config_ only  requires _Java 1.6_ or new runtime.

### Building ###

This project uses [Gradle](http://www.gradle.org/) for

* downloading dependencies (automatically),
* executing unit tests (``gradle test``),
* building the project (``gradle jar``).

To view more available tasks, execute following command:

	gradle tasks [--all]

Gradle uses .``/build/`` directory for storing build process results:

	./build/libs/           - composed JAR file directory
	./build/docs/javadoc/   - generated JavaDoc directory
	./build/reports/tests/  - generated report for executed tests

Gradle can be configured by editing ``./build.gradle`` file.

### License ###

This library is open-sourced with [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0) and is free to use.
