package org.tkit.onecx.demo.rs.external.v1.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.domain.models.Product;

import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductDTOV1;
import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductSearchCriteriaDTOV1;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapperImpl();

    @Test
    void shouldMapEntityToExternalDto() {
        Product entity = new Product();
        entity.setName("test-value");
        entity.setPrice(java.math.BigDecimal.ONE);

        ProductDTOV1 dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("test-value", dto.getName());
        assertEquals(1.0D, dto.getPrice());

    }

    @Test
    void shouldMapExternalSearchCriteriaToInternalCriteria() {
        ProductSearchCriteriaDTOV1 request = new ProductSearchCriteriaDTOV1();
        request.setPageNumber(0);
        request.setPageSize(10);
        request.setName("test-value");
        request.setPrice(1.0D);

        var criteria = mapper.toCriteria(request);

        assertNotNull(criteria);
    }
}
