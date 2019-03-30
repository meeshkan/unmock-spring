package io.unmock.spring;

import io.unmock.core.Token;
import io.unmock.core.UnmockOptions;
import io.unmock.okhttp.UnmockInterceptor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.ResponseCreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UnmockResponseCreator implements ResponseCreator {
  final private @NotNull OkHttpClient unmockClient;
  final private @NotNull UnmockInterceptor interceptor;

  public UnmockResponseCreator(UnmockOptions unmockOptions) throws IOException {
    this.interceptor = new UnmockInterceptor(unmockOptions);
    this.unmockClient = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();
  }

  public @NotNull List<String> getStories() {
    return this.interceptor.getStories();
  }

  @Override
  public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
    final URI url = request.getURI();
    final HttpHeaders headers = request.getHeaders();
    final OutputStream body = request.getBody();
    final String method = request.getMethodValue();
    Headers.Builder headersBuilder = new Headers.Builder();

    if (body != null && !(body instanceof ByteArrayOutputStream)) {
      throw new IOException("Unmock can only decode ByteArrayOutputStreams, got this instead: "+body);
    }

    ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) body;

    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      headersBuilder = headersBuilder.add(entry.getKey(), Arrays.stream(entry.getValue().toArray(new String[] {})).collect(Collectors.joining(",")));
    }

    final org.springframework.http.MediaType contentType = headers.getContentType();

    Request okhttpRequest = new Request.Builder()
            .url(url.toURL())
            .headers(headersBuilder.build())
            .method(method, contentType == null ? null : RequestBody.create(MediaType.get(contentType.toString()), byteArrayOutputStream.toByteArray()))
            .build();

    Response response = this.unmockClient.newCall(okhttpRequest).execute();

    return new UnmockHttpResponse(response.code(), response.headers().toMultimap(), response.body().byteStream());
  }
}
