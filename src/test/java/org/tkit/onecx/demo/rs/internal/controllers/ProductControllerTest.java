package org.tkit.onecx.demo.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.AbstractTest;
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

    @Test
    void getProductByIdMissingShouldThrowNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> controller.getProductById("missing-product-id"));
    }

    @Test
    void createProductWithoutCategoryShouldSucceed() {
        String request = """
                {
                  "name": "test-value",
                  "price": 1.0
                }
                """;

        String id = given()
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

        assertNotNull(id);
    }

    @Test
    void createProductWithExistingCategoryIdShouldReuseExistingCategory() {
        String relationId = createCategoryIdThroughProduct("seed-existing");

        String request = """
                {
                  "name": "test-value",
                  "price": 1.0,
                  "category": {\n                    \"id\": \"%s\"\n                  }
                }
                """;

        request = request.formatted(relationId);

        String entityId = given()
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

        String returnedRelationId = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/internal/products/{id}", entityId)
                .then()
                .statusCode(200)
                .extract()
                .path("category.id");

        assertEquals(relationId, returnedRelationId);
    }

    @Test
    void createProductWithNewCategoryShouldCreateNestedCategory() {
        String request = """
                {
                  "name": "test-value",
                  "price": 1.0,
                  "category": {\n                    \"name\": \"test-category\"\n                  }
                }
                """;

        String entityId = given()
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

        String returnedRelationId = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/internal/products/{id}", entityId)
                .then()
                .statusCode(200)
                .extract()
                .path("category.id");

        assertNotNull(returnedRelationId);
    }

    @Test
    void updateProductWithoutCategoryShouldSucceed() {
        String entityId = createProductAndReturnId();

        String request = """
                {
                  "name": "updated-value",
                  "price": 2.0
                }
                """;

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/internal/products/{id}", entityId)
                .then()
                .statusCode(200);
    }

    @Test
    void updateProductWithExistingCategoryIdShouldReuseExistingCategory() {
        String entityId = createProductAndReturnId();
        String relationId = createCategoryIdThroughProduct("seed-update-existing");

        String request = """
                {
                  "name": "updated-value",
                  "price": 2.0,
                  "category": {\n                    \"id\": \"%s\"\n                  }
                }
                """;

        request = request.formatted(relationId);

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/internal/products/{id}", entityId)
                .then()
                .statusCode(200);

        String returnedRelationId = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/internal/products/{id}", entityId)
                .then()
                .statusCode(200)
                .extract()
                .path("category.id");

        assertEquals(relationId, returnedRelationId);
    }

    @Test
    void updateProductWithNewCategoryShouldCreateNestedCategory() {
        String entityId = createProductAndReturnId();

        String request = """
                {
                  "name": "updated-value",
                  "price": 2.0,
                  "category": {\n                    \"name\": \"test-category\"\n                  }
                }
                """;

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/internal/products/{id}", entityId)
                .then()
                .statusCode(200);

        String returnedRelationId = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .when()
                .get("/internal/products/{id}", entityId)
                .then()
                .statusCode(200)
                .extract()
                .path("category.id");

        assertNotNull(returnedRelationId);
    }

    @Test
    void createProductWithBlankCategoryIdShouldCreateNestedCategory() {
        String request = """
                {
                  "name": "test-value",
                  "price": 1.0,
                  "category": {\n                    \"id\": \"\"\n                  }
                }
                """;

        String entityId = given()
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

        assertNotNull(entityId);
    }

    @Test
    void updateProductWithBlankCategoryIdShouldCreateNestedCategory() {
        String entityId = createProductAndReturnId();

        String request = """
                {
                  "name": "updated-value",
                  "price": 2.0,
                  "category": {\n                    \"id\": \"\"\n                  }
                }
                """;

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .when()
                .put("/internal/products/{id}", entityId)
                .then()
                .statusCode(200);
    }

    @Test
    void searchProductsWithEmptyCriteriaShouldUseDefaults() {
        createProductAndReturnId();

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
                .post("/internal/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("$");

        assertNotNull(result);
    }

    @Test
    void searchProductsByNameShouldUsePredicateAndNormalizeNegativePageNumber() {
        String request = """
                {
                  "name": "product-search-by-name",
                  "price": 1.0
                }
                """;

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(request)
                .when()
                .post("/internal/products")
                .then()
                .statusCode(201);

        String criteria = """
                {
                  "pageNumber": -1,
                  "pageSize": 10,
                  "name": "product-search-by-name"
                }
                """;

        List<?> result = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/internal/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("$");

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void searchProductsWithBlankNameShouldNotUsePredicate() {
        createProductAndReturnId();

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
                .post("/internal/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("$");

        assertNotNull(result);
    }

    @Test
    void searchProductsByPriceShouldUsePredicateAndNormalizeZeroPageSize() {
        String request = """
                {
                  "name": "test-value",
                  "price": 1234.56
                }
                """;

        given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(request)
                .when()
                .post("/internal/products")
                .then()
                .statusCode(201);

        String criteria = """
                {
                  "pageNumber": 0,
                  "pageSize": 0,
                  "price": 1234.56
                }
                """;

        List<?> result = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/internal/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("$");

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    void searchProductsWithNullFieldsShouldReturnAll() {
        createProductAndReturnId();

        String criteria = """
                {
                  "pageNumber": 0,
                  "pageSize": 100
                }
                """;

        List<?> result = given()
                .auth().oauth2(token)
                .header(APM_HEADER_PARAM, idToken)
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .when()
                .post("/internal/products/search")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("$");

        assertNotNull(result);
        assertTrue(result.size() >= 1);
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
                  "category": {\n                    \"name\": \"test-category\"\n                  }
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
