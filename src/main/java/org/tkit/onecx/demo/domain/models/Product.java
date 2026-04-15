package org.tkit.onecx.demo.domain.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.TenantId;
import org.tkit.quarkus.jpa.models.TraceableEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product extends TraceableEntity {

    @TenantId
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "NAME")
    private String name;
    @Column(name = "PRICE")
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private org.tkit.onecx.demo.domain.models.Category category;

}
