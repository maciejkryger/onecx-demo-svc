package org.tkit.onecx.demo.domain.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.domain.daos.CategoryDAO;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.onecx.demo.rs.internal.mappers.CategoryMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.CategorySearchCriteriaDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class CategoryServiceTest {

    @Inject
    CategoryService service;

    @InjectMock
    CategoryDAO dao;

    @InjectMock
    CategoryMapper mapper;

    @Test
    void shouldCreateCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("test-value");

        Category entity = new Category();
        entity.setName("test-value");

        when(mapper.fromDto(any(CategoryDTO.class))).thenReturn(entity);

        Category result = service.create(dto);

        assertNotNull(result);
    }

    @Test
    void shouldUpdateCategory() {
        Category entity = new Category();
        entity.setName("test-value");

        CategoryDTO dto = new CategoryDTO();
        dto.setName("updated-value");

        when(dao.findById(any())).thenReturn(entity);
        when(dao.update(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = service.update("test-id", dto);

        assertNotNull(result);
    }

    @Test
    void shouldFindByCriteria() {
        CategorySearchCriteriaDTO criteria = new CategorySearchCriteriaDTO();
        criteria.setPageNumber(0);
        criteria.setPageSize(10);

        Category entity = new Category();
        entity.setName("test-value");

        when(dao.findByCriteria(any())).thenReturn(List.of(entity));

        var result = service.findByCriteria(criteria);

        assertNotNull(result);
    }
}
