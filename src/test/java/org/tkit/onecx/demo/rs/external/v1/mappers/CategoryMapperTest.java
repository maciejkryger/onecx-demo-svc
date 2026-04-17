package org.tkit.onecx.demo.rs.external.v1.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.domain.models.Category;

import gen.org.tkit.onecx.demo.rs.external.v1.model.CategoryDTOV1;
import gen.org.tkit.onecx.demo.rs.external.v1.model.CategorySearchCriteriaDTOV1;

class CategoryMapperTest {

    private final CategoryMapper mapper = new CategoryMapperImpl();

    @Test
    void shouldMapEntityToExternalDto() {
        Category entity = new Category();
        entity.setName("test-value");

        CategoryDTOV1 dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("test-value", dto.getName());

    }

    @Test
    void shouldMapExternalSearchCriteriaToInternalCriteria() {
        CategorySearchCriteriaDTOV1 request = new CategorySearchCriteriaDTOV1();
        request.setPageNumber(0);
        request.setPageSize(10);
        request.setName("test-value");

        var criteria = mapper.toCriteria(request);

        assertNotNull(criteria);
    }
}
