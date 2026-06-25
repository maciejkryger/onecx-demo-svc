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
@Table(name = "PRODUCT")
@jakarta.persistence.AttributeOverrides({
        @jakarta.persistence.AttributeOverride(name = "creationDate", column = @jakarta.persistence.Column(name = "CREATION_DATE")),
        @jakarta.persistence.AttributeOverride(name = "creationUser", column = @jakarta.persistence.Column(name = "CREATION_USER")),
        @jakarta.persistence.AttributeOverride(name = "modificationDate", column = @jakarta.persistence.Column(name = "MODIFICATION_DATE")),
        @jakarta.persistence.AttributeOverride(name = "modificationUser", column = @jakarta.persistence.Column(name = "MODIFICATION_USER"))
})

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
    @JoinColumn(name = "CATEGORY_ID")
    private org.tkit.onecx.demo.domain.models.Category category;

}
