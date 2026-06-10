package org.tkit.onecx.demo.rs.internal.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductPageResultDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapper {

    @BeanMapping(ignoreByDefault = true)
    ProductDTO toDto(Product entity);

    @BeanMapping(ignoreByDefault = true)
    ProductPageResultDTO toPageResultDto(PageResult<Product> page);

    @BeanMapping(ignoreByDefault = true)
    Product fromDto(ProductDTO dto);

    @BeanMapping(ignoreByDefault = true)
    void update(ProductDTO dto, @MappingTarget Product entity);

    @BeanMapping(ignoreByDefault = true)
    Category fromDto(CategoryDTO dto);

    @BeanMapping(ignoreByDefault = true)
    void update(CategoryDTO dto,
            @org.mapstruct.MappingTarget Category entity);

}
