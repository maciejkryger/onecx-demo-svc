package org.tkit.onecx.demo.rs.internal.controllers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.demo.domain.services.ProductService;
import org.tkit.onecx.demo.rs.internal.mappers.InternalExceptionMapper;
import org.tkit.onecx.demo.rs.internal.mappers.ProductMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.demo.rs.internal.ProductsInternalApi;
import gen.org.tkit.onecx.demo.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductDTO;
import gen.org.tkit.onecx.demo.rs.internal.model.ProductSearchCriteriaDTO;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductController implements ProductsInternalApi {

    @Inject
    ProductService service;

    @Inject
    ProductMapper mapper;

    @Inject
    InternalExceptionMapper exceptionMapper;

    @Override
    public Response createProduct(ProductDTO dto) {
        var created = service.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(mapper.toDto(created))
                .build();
    }

    @Override
    public Response getProductById(String id) {
        return Response.ok(mapper.toDto(service.findById(id))).build();
    }

    @Override
    public Response updateProduct(String id, ProductDTO dto) {
        var updated = service.update(id, dto);
        return Response.ok(mapper.toDto(updated)).build();
    }

    @Override
    public Response deleteProduct(String id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @Override
    public Response searchProducts(
            @Valid ProductSearchCriteriaDTO criteria) {
        List<ProductDTO> result = service.findByCriteria(criteria)
                .stream()
                .map(mapper::toDto)
                .toList();
        return Response.ok(result).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> daoException(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }
}
