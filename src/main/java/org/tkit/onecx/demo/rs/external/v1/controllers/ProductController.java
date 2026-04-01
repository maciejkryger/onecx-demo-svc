package org.tkit.onecx.demo.rs.external.v1.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.tkit.onecx.demo.domain.services.ProductService;
import org.tkit.onecx.demo.rs.external.v1.mappers.ProductMapper;

import gen.org.tkit.onecx.demo.rs.external.v1.ProductsApi;
import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductDTO;

@ApplicationScoped
public class ProductController implements ProductsApi {

    @Inject
    ProductService service;

    @Inject
    ProductMapper mapper;

    @Override
    public Response getAllProducts() {
        List<ProductDTO> result = service.listAll()
                .stream()
                .map(mapper::toDto)
                .toList();
        return Response.ok(result).build();
    }

    @Override
    public Response createProduct(ProductDTO dto) {
        var created = service.create(mapper.fromDto(dto));
        return Response.status(Response.Status.CREATED)
                .entity(mapper.toDto(created))
                .build();
    }

    @Override
    public Response getProductById(Long id) {
        return Response.ok(mapper.toDto(service.findById(id))).build();
    }

    @Override
    public Response updateProduct(Long id, ProductDTO dto) {
        var updated = service.update(id, mapper.fromDto(dto));
        return Response.ok(mapper.toDto(updated)).build();
    }

    @Override
    public Response deleteProduct(Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
