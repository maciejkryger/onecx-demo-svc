package org.tkit.onecx.demo.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductSearchCriteriaDTOV1;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GenerateKeycloakClient(clientName = "productExternalTestClient", scopes = { "ocx-demo:read", "ocx-demo:write" })
class ProductControllerTest extends AbstractTest {

    String token;
    String idToken;

    @BeforeEach
    void setup() {
        token = keycloakClient.getClientAccessToken("productExternalTestClient");
        idToken = createToken("org1");
    }

    @Test
    void getProductByIdTest() {
        String id = createProductAndReturnId();

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/v1/products/{id}", id)
                .then()
                .statusCode(200);
    }

    @Test
    void searchProductsTest() {
        createProductAndReturnId();

        ProductSearchCriteriaDTOV1 request = new ProductSearchCriteriaDTOV1();
        request.setPageNumber(0);
        request.setPageSize(10);
        request.setName("test-value");
        request.setPrice(1.0D);

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(request)
                .when()
                .post("/v1/products/search")
                .then()
                .statusCode(200);
    }

    private String createProductAndReturnId() {
        ProductDTO request = new ProductDTO();
        request.setName("test-value");
        request.setPrice(1.0D);

        return given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(request)
                .when()
                .post("/internal/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
