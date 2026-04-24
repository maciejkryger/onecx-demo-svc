package org.tkit.onecx.demo.domain.daos;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;

import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;

@ApplicationScoped
public class ProductDAO extends AbstractDAO<Product> {

    public PageResult<Product> findByCriteria(ProductSearchCriteriaDTO criteria) {
        try {
            var cb = getEntityManager().getCriteriaBuilder();
            var cq = cb.createQuery(Product.class);
            var root = cq.from(Product.class);

            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getName() != null && !criteria.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }
            if (criteria.getPrice() != null) {
                predicates.add(cb.equal(root.get("price"), criteria.getPrice()));
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            int pageNumber = criteria.getPageNumber() != null ? criteria.getPageNumber() : 0;
            int pageSize = criteria.getPageSize() != null ? criteria.getPageSize() : 100;
            if (pageNumber < 0)
                pageNumber = 0;
            if (pageSize <= 0)
                pageSize = 100;

            return createPageQuery(cq, Page.of(pageNumber, pageSize)).getPageResult();
        } catch (Exception ex) {
            throw handleConstraint(ex, ErrorKeys.ERROR_FIND_BY_CRITERIA);
        }
    }

    public enum ErrorKeys {
        ERROR_FIND_BY_CRITERIA
    }
}
