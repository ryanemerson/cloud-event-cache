package io.gingersnap.project;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.SpecVersion;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GreetingResourceTest {

   @Inject
   ObjectMapper mapper;

   @Test
   public void testGetEndpoint() throws IOException {
      String eventPayload = "{\"key\":\"Hello\",\"value\":\"World\"}";

      given()
            .when()
            .headers(
                  Map.of(
                        "ce-id", UUID.randomUUID().toString(),
                        "ce-type", GreetingResourceTest.class.getName(),
                        "ce-source", GreetingResourceTest.class.getSimpleName().toLowerCase(),
                        "ce-specversion", SpecVersion.V1.toString()
                  )
            )
            .body(eventPayload)
            .contentType(MediaType.APPLICATION_JSON)
            .post("/")
            .then()
            .statusCode(202);

      given()
            .when().get("/Hello")
            .then()
            .statusCode(200)
            .body(is("World"));
   }
}
