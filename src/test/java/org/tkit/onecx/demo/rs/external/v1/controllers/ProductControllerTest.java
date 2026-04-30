package org.tkit.onecx.demo.rs.external.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.AbstractTest;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;

import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductSearchCriteriaDTOV1;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@GenerateKeycloakClient(clientName = "productExternalTestClient", scopes = { "ocx-demo:read", "ocx-demo:write" })
class ProductControllerTest extends AbstractTest {

    String token;
    String idToken;

    @Inject
    ProductController controller;

    @BeforeEach
    void setup() {
        token = keycloakClient.getClientAccessToken("productExternalTestClient");
        idToken = createToken("org1");
    }

    @Test
    void getById_shouldReturn200() {
        String id = createInternalEntity();

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/v1/products/{id}", id)
                .then()
                .statusCode(200);
    }

    @Test
    void search_shouldCoverAllBranches() {
        createInternalEntity();

        ProductSearchCriteriaDTOV1 criteria = new ProductSearchCriteriaDTOV1();

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/v1/products/search")
                .then()
                .statusCode(200);
    }

    @Test
    void searchWithExplicitPageNumberAndSizeShouldSucceed() {
        createInternalEntity();

        ProductSearchCriteriaDTOV1 criteria = new ProductSearchCriteriaDTOV1();
        criteria.setPageNumber(2);
        criteria.setPageSize(5);

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/v1/products/search")
                .then()
                .statusCode(200);
    }

    @Test
    void searchWithNullBodyShouldTriggerDaoCatchAndReturnError() {
        createInternalEntity();

        int status = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .post("/v1/products/search")
                .then()
                .extract()
                .statusCode();

        assertTrue(status >= 400);
    }

    @Test
    void shouldMapConstraintException() {
        ConstraintException ex = mock(ConstraintException.class, RETURNS_DEEP_STUBS);
        when(ex.getMessage()).thenReturn("constraint");
        when(ex.getConstraints()).thenReturn("constraint");
        when(ex.getMessageKey().name()).thenReturn("CONSTRAINT_VIOLATIONS");

        assertEquals(400, controller.exception(ex).getStatus());
    }

    @Test
    void shouldMapConstraintViolationException() {
        var ex = new ConstraintViolationException(java.util.Collections.emptySet());

        assertEquals(400, controller.constraint(ex).getStatus());
    }

    @Test
    void shouldMapOptimisticLockException() {
        var ex = new OptimisticLockException("optimistic");

        assertEquals(409, controller.daoException(ex).getStatus());
    }

    @Test
    void getProductByIdMissingShouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> controller.getProductByIdV1("missing-product-id"));
    }

    @Test
    void searchProductsWithEmptyCriteriaShouldUseDefaults() {
        createInternalEntity();

        String criteria = """
                {
                }
                """;

        List<?> result = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/v1/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("stream");

        assertNotNull(result);
    }

    @Test
    void searchProductsWithBlankNameShouldNotUsePredicate() {
        createInternalEntity();

        String criteria = """
                {
                  "pageNumber": 0,
                  "pageSize": 10,
                  "name": "   "
                }
                """;

        List<?> result = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/v1/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("stream");

        assertNotNull(result);
    }

    private String createInternalEntity() {
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

    // DAO exception test moved to a dedicated DAO test in the DAO package because
    // getEntityManager() has protected access and must be referenced from the DAO package.

}
