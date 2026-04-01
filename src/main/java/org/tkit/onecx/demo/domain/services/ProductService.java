package org.tkit.onecx.demo.domain.services;

import java.util.List;
import java.util.NoSuchElementException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.onecx.demo.domain.daos.ProductDAO;
import org.tkit.onecx.demo.domain.models.Product;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductDAO dao;

    public List<Product> listAll() {
        return dao.listAll();
    }

    public Product findById(Long id) {
        Product entity = dao.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        return entity;
    }

    @Transactional
    public Product create(Product entity) {
        dao.persist(entity);
        return entity;
    }

    @Transactional
    public Product update(Long id, Product entity) {
        findById(id);
        entity.setId(id);
        return dao.getEntityManager().merge(entity);
    }

    @Transactional
    public void delete(Long id) {
        dao.deleteById(id);
    }
}
