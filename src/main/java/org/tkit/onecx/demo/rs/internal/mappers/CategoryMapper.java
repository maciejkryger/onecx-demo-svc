package org.tkit.onecx.demo.rs.internal.mappers;

import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.CategoryPageResultDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface CategoryMapper {

    CategoryDTO toDto(Category entity);

    default CategoryPageResultDTO toPageResultDto(PageResult<Category> page) {
        CategoryPageResultDTO dto = new CategoryPageResultDTO();
        dto.setStream(page.getStream().map(this::toDto).collect(Collectors.toList()));
        dto.setTotalElements(page.getTotalElements());
        dto.setNumber((int) page.getNumber());
        dto.setSize((int) page.getSize());
        dto.setTotalPages((int) page.getTotalPages());
        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    Category fromDto(CategoryDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    void update(CategoryDTO dto, @MappingTarget Category entity);

}
