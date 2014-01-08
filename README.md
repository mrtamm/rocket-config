Rocket-Config Library
=======================

_Configuration library for applications when ``java.util.Properties`` is not enough._

This library fills the gap between a configuration file and runtime configuration values. It introduces section-based
customizable configuration file format. Once file parsing is completed, an application has a runtime configuration
object that it can use to configure itself.

Sample configuration file with three sections, each having its own custom data:

```
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
```

Sample configuration object for storing the parsed data:

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

Sample _Rocket-Config_ usage: describe configuration file model (according to which the stream is parsed), and data will
be stored in a new instance of the target type.

```java
ConfigModel<AcmeConfig> model = ConfigModel.expect(AcmeConfig.class)
    .section("general").ofMap().storeIn("name", "url", "timeout")
    .section("entities").ofList(Class.class).storeIn("entities")
    .section("filters").ofList(Filter.class).storeIn("filters")
    .ready();

AcmeConfig conf = model.parse(getClass().getResourceAsStream("/acme.conf"));
```

As shown in the example, ``ws.rocket.config.ConfigModel`` class provides the main functionality for library users.

### Dependencies ###

_Rocket-Config_ only  requires _Java 1.6_ (or newer) for compiling and running.

Unit tests additionally rely on _TestNg_ library.

### Building ###

This project uses [Gradle](http://www.gradle.org/) for

* executing unit tests (``gradle test``),
* compiling the documentation (``gradle javadoc``).
* building the project (``gradle build``).

To view more available tasks, execute following command:

	gradle tasks [--all]

_Gradle_ uses ``./build/`` directory for storing build process results:

	./build/libs/           - contains the created jar file
	./build/docs/javadoc/   - generated JavaDoc directory
	./build/reports/tests/  - generated report for executed tests

_Gradle_ can be configured by editing ``./build.gradle`` file.

### License ###

This library is open-sourced with [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0) and is free to use,
to customize or to extend.

Please use [issue tracker](https://github.com/mrtamm/rocket-config/issues) to report problems and feature requests.
