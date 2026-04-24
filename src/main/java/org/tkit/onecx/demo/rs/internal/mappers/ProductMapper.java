package org.tkit.onecx.demo.rs.internal.mappers;

import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductPageResultDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface ProductMapper {

    ProductDTO toDto(Product entity);

    default ProductPageResultDTO toPageResultDto(PageResult<Product> page) {
        ProductPageResultDTO dto = new ProductPageResultDTO();
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
    Product fromDto(ProductDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    void update(ProductDTO dto, @MappingTarget Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    org.tkit.onecx.demo.domain.models.Category fromDto(gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    void update(gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO dto,
            @org.mapstruct.MappingTarget org.tkit.onecx.demo.domain.models.Category entity);

}
