package com.virtusa.orderservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "product_id")
    private Long productId;

    @Column(name = "order_status")
    private String status;

    public Order() {
    }

    public Order(Long id, Long productId, String status) {
        this.id = id;
        this.productId = productId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", productId=" + productId +
                ", status='" + status + '\'' +
                '}';
    }
}
