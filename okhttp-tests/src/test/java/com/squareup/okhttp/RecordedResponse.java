/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.okhttp;

import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * A received response or failure recorded by the response recorder.
 */
public class RecordedResponse {
  public final Request request;
  public final Response response;
  public final String body;
  public final Failure failure;

  RecordedResponse(Request request, Response response, String body, Failure failure) {
    this.request = request;
    this.response = response;
    this.body = body;
    this.failure = failure;
  }

  public RecordedResponse assertRequestUrl(URL url) {
    assertEquals(url, request.url());
    return this;
  }

  public RecordedResponse assertRequestHeader(String name, String... values) {
    assertEquals(Arrays.asList(values), request.headers(name));
    return this;
  }

  public RecordedResponse assertCode(int expectedCode) {
    assertEquals(expectedCode, response.code());
    return this;
  }

  public RecordedResponse assertHeader(String name, String... values) {
    assertEquals(Arrays.asList(values), response.headers(name));
    return this;
  }

  public RecordedResponse assertBody(String expectedBody) {
    assertEquals(expectedBody, body);
    return this;
  }

  public RecordedResponse assertHandshake() {
    Handshake handshake = response.handshake();
    assertNotNull(handshake.cipherSuite());
    assertNotNull(handshake.peerPrincipal());
    assertEquals(1, handshake.peerCertificates().size());
    assertNull(handshake.localPrincipal());
    assertEquals(0, handshake.localCertificates().size());
    return this;
  }

  /**
   * Asserts that the current response was redirected and returns a new recorded
   * response for the original request.
   */
  public RecordedResponse priorResponse() {
    Response priorResponse = response.priorResponse();
    assertNotNull(priorResponse);
    assertNull(priorResponse.body());
    return new RecordedResponse(priorResponse.request(), priorResponse, null, null);
  }

  /**
   * Asserts that the current response consulted the cache and returns a new
   * recorded response for the cached response.
   */
  public RecordedResponse cacheResponse() {
    Response cacheResponse = response.cacheResponse();

    assertNotNull(cacheResponse);
    assertNull(cacheResponse.body());
    return new RecordedResponse(cacheResponse.request(), cacheResponse, null, null);
  }

  /**
   * Asserts that the current response was validated by the cache and returns a
   * new recorded response for the validation response.
   */
  public RecordedResponse validationResponse() {
    Response validationResponse = response.validationResponse();
    assertNotNull(validationResponse);
    assertNull(validationResponse.body());
    return new RecordedResponse(validationResponse.request(), validationResponse, null, null);
  }

  public void assertFailure(String message) {
    assertNotNull(failure);
    assertEquals(message, failure.exception().getMessage());
  }
}
