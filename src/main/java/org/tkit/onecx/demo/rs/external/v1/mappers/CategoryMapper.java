package org.tkit.onecx.demo.rs.external.v1.mappers;

import org.mapstruct.Mapper;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.demo.rs.external.v1.model.CategoryDTOV1;
import gen.org.tkit.onecx.demo.rs.external.v1.model.CategorySearchCriteriaDTOV1;
import gen.org.tkit.onecx.demo.rs.internal.model.CategorySearchCriteriaDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface CategoryMapper {

    CategoryDTOV1 toDto(Category entity);

    CategorySearchCriteriaDTO toCriteria(CategorySearchCriteriaDTOV1 criteria);
}
