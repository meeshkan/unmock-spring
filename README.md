# Unmock
Public API mocking for Java, Scala, Kotlin, Clojure, Groovy and friends.

Unmock can be used to test modules that perform requests to third-party APIs like Hubspot, SendGrid, Behance, and hundreds of other public APIs.

`unmock-spring` is a high-level interceptor for Spring applications.

The ultimate goal of unmock is to provide a semantically and functionally adequate mock of the internet.

## How does it work?

The `unmock-spring` package uses the Spring `ResponseCreator` pattern to intercept requests and run them through unmock.  For example, the following test intercepts a call to the Behance API and automatically returns mock data.

```java
  @Test
  public void testCallingBehanceAPI() throws IOException {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
    
    UnmockResponseCreator responseCreator = new UnmockResponseCreator(new UnmockOptions.Builder().build());
    
    server.expect(manyTimes(), requestTo(org.hamcrest.Matchers.any(String.class)))
        .andRespond(responseCreator);
    
    String body = restTemplate.getForObject("https://www.behance.net/v2/projects", String.class);
    
    JSONObject json = new JSONObject(body);
    JSONArray projects = json.getJSONArray("projects");
    Assert.assertTrue(projects.getJSONObject(0).getInt("id") >= 0);
  }
```

## Install

```gradle
dependencies {
  compile group: 'io.unmock', name: 'spring', version: '0.0.0';
  compile group: 'io.unmock', name: 'core', version: '0.0.0';
}
```

### Java version support

Unmock is written in Java 1.8.  It is currently untested for older versions of Java but may work for them as well.

## unmock.io

The URLs printed to the command line are hosted by [unmock.io](https://www.unmock.io).  You can consult the documentation about that service [here](https://www.unmock.io/docs).

## Contributing

Thanks for wanting to contribute! Take a look at our [Contributing Guide](CONTRIBUTING.md) for notes on our commit message conventions and how to run tests.

Please note that this project is released with a [Contributor Code of Conduct](CODE_OF_CONDUCT.md).
By participating in this project you agree to abide by its terms.

## License

[MIT](LICENSE)

Copyright (c) 2018‚ 2019 [Meeshkan](http://meeshkan.com) and other [contributors](https://github.com/unmock/unmock-js/graphs/contributors).
