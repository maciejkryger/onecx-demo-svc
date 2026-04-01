package org.tkit.onecx.demo.rs.external.v1.mappers;

import org.mapstruct.Mapper;
import org.tkit.onecx.demo.domain.models.Product;

import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductDTO;

@Mapper(componentModel = "jakarta")
public interface ProductMapper {

    ProductDTO toDto(Product entity);

    Product fromDto(ProductDTO dto);
}
