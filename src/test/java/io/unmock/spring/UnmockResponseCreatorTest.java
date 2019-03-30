package io.unmock.spring;

import io.unmock.core.UnmockOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

public class UnmockResponseCreatorTest {
  @Test
  public void testSimpleRequest() throws IOException {
    RestTemplate restTemplate = new RestTemplate();
    MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

    UnmockResponseCreator responseCreator = new UnmockResponseCreator(new UnmockOptions.Builder().build());

    server.expect(manyTimes(), requestTo(org.hamcrest.Matchers.any(String.class)))
      .andRespond(responseCreator);

    String body = restTemplate.getForObject("https://www.behance.net/v2/projects", String.class);

    JSONObject json = new JSONObject(body);
    JSONArray projects = json.getJSONArray("projects");
    // smoke test, will fail if ID not present
    projects.getJSONObject(0).getInt("id");
    Assert.assertEquals("Story size is correct", 1, responseCreator.getStories().size());
  }
}
