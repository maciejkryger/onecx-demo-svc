package org.tkit.onecx.demo.rs.external.v1.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.demo.domain.daos.ProductDAO;
import org.tkit.onecx.demo.domain.services.ProductService;
import org.tkit.onecx.demo.rs.external.v1.mappers.ExternalExceptionMapper;
import org.tkit.onecx.demo.rs.external.v1.mappers.ProductMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;

import gen.org.tkit.onecx.demo.rs.external.v1.ProductsV1Api;
import gen.org.tkit.onecx.demo.rs.external.v1.model.ProblemDetailResponseDTOV1;
import gen.org.tkit.onecx.demo.rs.external.v1.model.ProductSearchCriteriaDTOV1;

@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class ProductController implements ProductsV1Api {

    @Inject
    ProductService service;

    @Inject
    ProductMapper mapper;

    @Inject
    ExternalExceptionMapper exceptionMapper;

    @Inject
    ProductDAO dao;

    @Override
    public Response getProductByIdV1(String id) {
        return Response.ok(mapper.toDto(dao.findById(id))).build();
    }

    @Override
    public Response searchProductsV1(ProductSearchCriteriaDTOV1 criteria) {

        var pageResult = dao.findByCriteria(mapper.toCriteria(criteria));

        return Response.ok(mapper.mapPageResult(pageResult)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTOV1> daoException(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }
}
