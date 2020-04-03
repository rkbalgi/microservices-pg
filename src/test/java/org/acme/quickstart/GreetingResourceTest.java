package org.acme.quickstart;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

@QuarkusTest
public class GreetingResourceTest {

  @Test
  public void testHelloEndpoint() {
    given()
        .when().get("/kotlin/test")
        .then()
        .statusCode(200)
        .body(startsWith("Hello"));
  }

}