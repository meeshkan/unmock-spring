package io.unmock.spring;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class UnmockHttpResponse extends AbstractClientHttpResponse {


  private final @NotNull Map<String, List<String>> headers;
  private final @NotNull int statusCode;
  private final @NotNull InputStream responseStream;

  UnmockHttpResponse(int statusCode, Map<String, List<String>> headers, InputStream responseStream) {
    this.statusCode = statusCode;
    this.headers = headers;
    this.responseStream = responseStream;
  }


  @Override
  public int getRawStatusCode() throws IOException {
    return this.statusCode;
  }

  @Override
  public String getStatusText() throws IOException {
    // TODO: fix
    return "unimplemented";
  }

  @Override
  public HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
      headers.put(entry.getKey(), entry.getValue());
    }
    return headers;
  }

  @Override
  public InputStream getBody() throws IOException {
    return this.responseStream;
  }

  @Override
  public void close() {
    try {
      if (this.responseStream == null) {
        getBody();
      }
      StreamUtils.drain(this.responseStream);
      this.responseStream.close();
    }
    catch (Exception ex) {
      // ignore
    }
  }

}
