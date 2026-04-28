package org.tkit.onecx.demo.domain.daos;

import static org.tkit.quarkus.jpa.utils.QueryCriteriaUtil.addSearchStringPredicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.Predicate;

import org.tkit.onecx.demo.domain.models.Product;
import org.tkit.onecx.demo.domain.models.Product_;
import org.tkit.quarkus.jpa.daos.AbstractDAO;
import org.tkit.quarkus.jpa.daos.Page;
import org.tkit.quarkus.jpa.daos.PageResult;
import org.tkit.quarkus.jpa.models.AbstractTraceableEntity_;

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
                addSearchStringPredicate(predicates, cb, root.get(Product_.NAME), criteria.getName());
            }

            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }
            cq.orderBy(cb.desc(root.get(AbstractTraceableEntity_.CREATION_DATE)));

            return createPageQuery(cq, Page.of(criteria.getPageNumber(), criteria.getPageSize())).getPageResult();
        } catch (Exception ex) {
            throw handleConstraint(ex, ErrorKeys.ERROR_FIND_BY_CRITERIA);
        }
    }

    public enum ErrorKeys {
        ERROR_FIND_BY_CRITERIA
    }
}
