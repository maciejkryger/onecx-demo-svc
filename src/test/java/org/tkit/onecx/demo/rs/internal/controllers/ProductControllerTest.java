package org.tkit.onecx.demo.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.AbstractTest;
import org.tkit.onecx.demo.domain.daos.ProductDAO;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
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

    @Inject
    ProductController controller;

    @Inject
    ProductDAO dao;

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
    void getProductByIdNotFoundTest() {
        String id = "non-existing-id";

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/internal/products/{id}", id)
                .then()
                .statusCode(404);
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
    void updateProductNotFoundTest() {
        String id = "non-existing-id";

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
                .statusCode(404);
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
    void deleteProductNotFoundTest() {
        String id = "non-existing-id";

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .delete("/internal/products/{id}", id)
                .then()
                .statusCode(404);
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

    @Test
    void searchWithExplicitPageNumberAndSizeShouldSucceed() {
        ProductSearchCriteriaDTO criteria = new ProductSearchCriteriaDTO();
        criteria.setPageNumber(1);
        criteria.setPageSize(5);

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/internal/products/search")
                .then()
                .statusCode(200);
    }

    @Test
    void searchWithNullBodyShouldTriggerDaoCatchAndReturnError() {
        int status = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .post("/internal/products/search")
                .then()
                .extract()
                .statusCode();

        assertTrue(status >= 400);
    }

    @Test
    void shouldMapConstraintExceptionWithRealMapper() {
        ConstraintException ex = mock(ConstraintException.class, RETURNS_DEEP_STUBS);
        when(ex.getMessage()).thenReturn("constraint");
        when(ex.getConstraints()).thenReturn("constraint");
        when(ex.getMessageKey().name()).thenReturn("CONSTRAINT_VIOLATIONS");

        var response = controller.exception(ex);

        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    void shouldMapConstraintViolationExceptionWithRealMapper() {
        ConstraintViolationException ex = new ConstraintViolationException(java.util.Collections.emptySet());

        var response = controller.constraint(ex);

        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    void shouldMapOptimisticLockExceptionWithRealMapper() {
        OptimisticLockException ex = new OptimisticLockException("optimistic-lock");

        var response = controller.daoException(ex);

        assertNotNull(response);
        assertEquals(400, response.getStatus());
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

    private String createCategoryIdThroughProduct(String ignoredSeedValue) {
        String request = """
                {
                  "name": "test-value",
                  "price": 1.0,
                  "category": {
                    "name": "test-category"
                  }
                }
                """;

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
                .path("category.id");
    }

}
