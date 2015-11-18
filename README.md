# dropwizard-json-exceptions
Reusable ExceptionMappers for Dropwizard Apps that want more verbose JSON error outputs

Dropwizard intentionally provides pretty obtuse error messages to the end user; if you're interested in providing additional JSON-formatted exceptions for any exceptions that happen during execution of your REST endpoints, then just add this JAR to your classpath and turn off the Dropwizard server's standard exception mappers.

## Integration
Add this JAR to your Dropwizard -server's POM

```XML
<dependency>
    <groupId>com.washingtonpost.dropwizard</groupId>
    <artifactId>dropwizard-json-exceptions</artifactId>
    <version>${version.wp.dropwizard.exceptions}</version>
</dependency>
```

Then in your application configuration, just turn off the server default exception mappers:

```YAML
server: 
    registerDefaultExceptionMappers: false
```

## TODO
This JAR is really opinionated about JSON output, mapped exceptions, and default verbosity.  To improve its re-usability, make a lot of the behavior of the 2 exception mappers configurable.

Also, add tests!