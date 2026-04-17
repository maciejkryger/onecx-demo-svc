package org.tkit.onecx.demo.domain.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.domain.daos.ProductDAO;
import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.onecx.demo.rs.internal.mappers.ProductMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ProductServiceTest {

    @Inject
    ProductService service;

    @InjectMock
    ProductDAO dao;

    @InjectMock
    ProductMapper mapper;

    @Test
    void shouldCreateProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setName("test-value");
        dto.setPrice(1.0D);

        Product entity = new Product();
        entity.setName("test-value");
        entity.setPrice(java.math.BigDecimal.ONE);

        when(mapper.fromDto(any(ProductDTO.class))).thenReturn(entity);

        Product result = service.create(dto);

        assertNotNull(result);
    }

    @Test
    void shouldUpdateProduct() {
        Product entity = new Product();
        entity.setName("test-value");
        entity.setPrice(java.math.BigDecimal.ONE);

        ProductDTO dto = new ProductDTO();
        dto.setName("updated-value");
        dto.setPrice(2.0D);

        when(dao.findById(any())).thenReturn(entity);
        when(dao.update(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.update("test-id", dto);

        assertNotNull(result);
    }

    @Test
    void shouldFindByCriteria() {
        ProductSearchCriteriaDTO criteria = new ProductSearchCriteriaDTO();
        criteria.setPageNumber(0);
        criteria.setPageSize(10);

        Product entity = new Product();
        entity.setName("test-value");
        entity.setPrice(java.math.BigDecimal.ONE);

        when(dao.findByCriteria(any())).thenReturn(List.of(entity));

        var result = service.findByCriteria(criteria);

        assertNotNull(result);
    }
}
