package org.tkit.onecx.demo.domain.services;

import java.util.NoSuchElementException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.demo.domain.daos.CategoryDAO;
import org.tkit.onecx.demo.domain.daos.ProductDAO;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.onecx.demo.rs.internal.mappers.ProductMapper;
import org.tkit.quarkus.jpa.daos.PageResult;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductDAO dao;

    @Inject
    ProductMapper mapper;

    @Inject
    CategoryDAO categoryDAO;

    public PageResult<Product> findByCriteria(ProductSearchCriteriaDTO criteria) {
        return dao.findByCriteria(criteria);
    }

    public Product findById(String id) {
        Product entity = dao.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        return entity;
    }

    @Transactional
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

    @Transactional
    public Product update(String id, ProductDTO dto) {
        Product entity = findById(id);
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

    @Transactional
    public void delete(String id) {
        Product entity = findById(id);
        dao.delete(entity);
    }
}
