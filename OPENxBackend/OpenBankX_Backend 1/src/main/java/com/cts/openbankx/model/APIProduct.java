package com.cts.openbankx.model;

import com.cts.openbankx.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "api_product")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class APIProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false,unique=true)
    private String name;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String endpointsJSON;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    public APIProduct() {}

    public APIProduct(Long productId, String name, String description, String endpointsJSON, ProductStatus status) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.endpointsJSON = endpointsJSON;
        this.status = status;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEndpointsJSON() { return endpointsJSON; }
    public void setEndpointsJSON(String endpointsJSON) { this.endpointsJSON = endpointsJSON; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
