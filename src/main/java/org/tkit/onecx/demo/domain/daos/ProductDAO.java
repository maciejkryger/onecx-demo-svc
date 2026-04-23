package org.tkit.onecx.demo.domain.daos;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;

@ApplicationScoped
public class ProductDAO extends AbstractDAO<Product> {

    public List<Product> findByCriteria(ProductSearchCriteriaDTO criteria) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria != null && criteria.getName() != null && !criteria.getName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
        }
        if (criteria != null && criteria.getPrice() != null) {
            predicates.add(cb.equal(root.get("price"), criteria.getPrice()));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Product> query = getEntityManager().createQuery(cq);

        int pageNumber = criteria.getPageNumber() != null ? criteria.getPageNumber() : 0;
        int pageSize = criteria.getPageSize() != null ? criteria.getPageSize() : 100;

        if (pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize <= 0) {
            pageSize = 100;
        }

        int offset = pageNumber * pageSize;

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }
}
