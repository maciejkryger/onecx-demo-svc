package org.tkit.onecx.demo.rs.external.v1.mappers;

import org.mapstruct.Mapper;
import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductDTOV1;
import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductSearchCriteriaDTOV1;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapper {

    ProductDTOV1 toDto(Product entity);

    ProductSearchCriteriaDTO toCriteria(ProductSearchCriteriaDTOV1 criteria);
}
