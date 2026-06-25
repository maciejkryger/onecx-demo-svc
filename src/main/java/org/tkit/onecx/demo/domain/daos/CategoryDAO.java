package org.tkit.onecx.demo.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.demo.domain.models.Category;
import org.tkit.quarkus.jpa.daos.AbstractDAO;

@ApplicationScoped
public class CategoryDAO extends AbstractDAO<Category> {
}
