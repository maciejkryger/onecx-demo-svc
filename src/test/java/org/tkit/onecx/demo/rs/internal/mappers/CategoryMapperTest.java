package org.tkit.onecx.demo.rs.internal.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.demo.domain.models.Category;

import gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class CategoryMapperTest {

    @Inject
    CategoryMapper mapper;

    @Test
    void shouldMapEntityToDto() {
        Category entity = new Category();
        entity.setName("test-value");

        CategoryDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("test-value", dto.getName());

    }

    @Test
    void shouldMapDtoToEntity() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("test-value");

        Category entity = mapper.fromDto(dto);

        assertNotNull(entity);
        assertEquals("test-value", entity.getName());

    }

    @Test
    void shouldUpdateEntityFromDto() {
        Category entity = new Category();
        entity.setName("test-value");

        CategoryDTO dto = new CategoryDTO();
        dto.setName("updated-value");

        mapper.update(dto, entity);

        assertEquals("updated-value", entity.getName());

    }
}
