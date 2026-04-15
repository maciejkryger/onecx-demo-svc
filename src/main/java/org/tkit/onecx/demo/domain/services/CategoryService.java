package org.tkit.onecx.demo.domain.services;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.demo.domain.daos.CategoryDAO;
import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.onecx.demo.rs.internal.mappers.CategoryMapper;

import gen.org.tkit.onecx.demo.rs.internal.model.CategoryDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.CategorySearchCriteriaDTO;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryDAO dao;

    @Inject
    CategoryMapper mapper;

    public List<Category> findByCriteria(CategorySearchCriteriaDTO criteria) {
        return dao.findByCriteria(criteria);
    }

    public Category findById(String id) {
        Category entity = dao.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("Category not found: " + id);
        }
        return entity;
    }

    @Transactional
    public Category create(CategoryDTO dto) {
        Category entity = mapper.fromDto(dto);
        dao.create(entity);
        return entity;
    }

    @Transactional
    public Category update(String id, CategoryDTO dto) {
        Category entity = findById(id);
        mapper.update(dto, entity);
        return dao.update(entity);
    }

    @Transactional
    public void delete(String id) {
        Category entity = findById(id);
        dao.delete(entity);
    }
}
