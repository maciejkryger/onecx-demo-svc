package org.tkit.onecx.demo.domain.daos;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

import gen.org.tkit.onecx.demo.rs.internal.model.CategorySearchCriteriaDTO;

@ApplicationScoped
public class CategoryDAO extends AbstractDAO<Category> {

    public List<Category> findByCriteria(CategorySearchCriteriaDTO criteria) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria != null && criteria.getName() != null && !criteria.getName().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Category> query = getEntityManager().createQuery(cq);

        int pageNumber = criteria.getPageNumber() != null ? criteria.getPageNumber() : 0;
        int pageSize = criteria.getPageSize() != null ? criteria.getPageSize() : 100;

        if (pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize <= 0) {
            pageSize = 100;
        }
        if (pageSize > 1000) {
            pageSize = 1000;
        }

        int offset = pageNumber * pageSize;

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }
}
