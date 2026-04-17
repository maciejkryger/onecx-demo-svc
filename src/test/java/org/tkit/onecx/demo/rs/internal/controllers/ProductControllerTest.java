package org.tkit.onecx.demo.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GenerateKeycloakClient(clientName = "productInternalTestClient", scopes = { "ocx-demo:read", "ocx-demo:write",
        "ocx-demo:delete" })
class ProductControllerTest extends AbstractTest {

    String token;
    String idToken;

    @BeforeEach
    void setup() {
        token = keycloakClient.getClientAccessToken("productInternalTestClient");
        idToken = createToken("org1");
    }

    @Test
    void createProductTest() {
        ProductDTO request = new ProductDTO();
        request.setName("test-value");
        request.setPrice(1.0D);

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(request)
                .when()
                .post("/internal/products")
                .then()
                .statusCode(201);
    }

    @Test
    void getProductByIdTest() {
        String id = createProductAndReturnId();

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/internal/products/{id}", id)
                .then()
                .statusCode(200);
    }

    @Test
    void updateProductTest() {
        String id = createProductAndReturnId();

        ProductDTO request = new ProductDTO();
        request.setName("updated-value");
        request.setPrice(2.0D);

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/internal/products/{id}", id)
                .then()
                .statusCode(200);
    }

    @Test
    void deleteProductTest() {
        String id = createProductAndReturnId();

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .delete("/internal/products/{id}", id)
                .then()
                .statusCode(204);
    }

    @Test
    void searchProductsTest() {
        ProductSearchCriteriaDTO request = new ProductSearchCriteriaDTO();
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
                .post("/internal/products/search")
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
