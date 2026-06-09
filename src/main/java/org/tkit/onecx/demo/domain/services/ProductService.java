package org.tkit.onecx.demo.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.tkit.onecx.demo.domain.daos.CategoryDAO;
import org.tkit.onecx.demo.domain.daos.ProductDAO;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.onecx.demo.rs.internal.mappers.ProductMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductDAO dao;
    @Inject
    ProductMapper mapper;
    @Inject
    CategoryDAO categoryDAO;

    public Product create(ProductDTO dto) {
        Product entity = mapper.fromDto(dto);
        if (dto.getCategory() != null) {
            if (dto.getCategory().getId() != null && !dto.getCategory().getId().isBlank()) {
                Category resolvedCategory = categoryDAO.findById(dto.getCategory().getId());
                entity.setCategory(resolvedCategory);
            } else {
                Category resolvedCategory = mapper.fromDto(dto.getCategory());
                categoryDAO.create(resolvedCategory);
                entity.setCategory(resolvedCategory);
            }
        }
        dao.create(entity);
        return entity;
    }

    public Product update(String id, ProductDTO dto) {
        Product entity = dao.findById(id);
        if (entity == null) {
            return null;
        }
        mapper.update(dto, entity);
        if (dto.getCategory() != null) {
            entity.setCategory(null);
            if (dto.getCategory().getId() != null && !dto.getCategory().getId().isBlank()) {
                Category resolvedCategory = categoryDAO.findById(dto.getCategory().getId());
                entity.setCategory(resolvedCategory);
            } else {
                Category resolvedCategory = mapper.fromDto(dto.getCategory());
                categoryDAO.create(resolvedCategory);
                entity.setCategory(resolvedCategory);
            }
        }
        return dao.update(entity);
    }
}
