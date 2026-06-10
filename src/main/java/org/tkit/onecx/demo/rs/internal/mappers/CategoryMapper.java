package org.tkit.onecx.demo.rs.internal.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.CategoryPageResultDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface CategoryMapper {

    @BeanMapping(ignoreByDefault = true)
    CategoryDTO toDto(Category entity);

    @BeanMapping(ignoreByDefault = true)
    CategoryPageResultDTO toPageResultDto(PageResult<Category> page);

    @BeanMapping(ignoreByDefault = true)
    Category fromDto(CategoryDTO dto);

    @BeanMapping(ignoreByDefault = true)
    void update(CategoryDTO dto, @MappingTarget Category entity);

}
