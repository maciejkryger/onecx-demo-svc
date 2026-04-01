package org.tkit.onecx.demo.domain.daos;

import jakarta.enterprise.context.ApplicationScoped;

import org.tkit.onecx.demo.domain.models.Product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ProductDAO implements PanacheRepository<Product> {
}
