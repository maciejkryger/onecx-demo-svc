package org.tkit.onecx.demo.rs.internal.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.domain.models.Product;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ProductMapperTest {

    @Inject
    ProductMapper mapper;

    @Test
    void shouldMapEntityToDto() {
        Product entity = new Product();
        entity.setName("test-value");
        entity.setPrice(java.math.BigDecimal.ONE);

        ProductDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("test-value", dto.getName());
        assertEquals(1.0D, dto.getPrice());

    }

    @Test
    void shouldMapDtoToEntity() {
        ProductDTO dto = new ProductDTO();
        dto.setName("test-value");
        dto.setPrice(1.0D);

        Product entity = mapper.fromDto(dto);

        assertNotNull(entity);
        assertEquals("test-value", entity.getName());
        assertEquals(0, entity.getPrice().compareTo(new java.math.BigDecimal("1.0")));

    }

    @Test
    void shouldUpdateEntityFromDto() {
        Product entity = new Product();
        entity.setName("test-value");
        entity.setPrice(java.math.BigDecimal.ONE);

        ProductDTO dto = new ProductDTO();
        dto.setName("updated-value");
        dto.setPrice(2.0D);

        mapper.update(dto, entity);

        assertEquals("updated-value", entity.getName());
        assertEquals(0, entity.getPrice().compareTo(new java.math.BigDecimal("2.0")));

    }
}
