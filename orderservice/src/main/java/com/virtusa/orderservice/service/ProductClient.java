package com.virtusa.orderservice.service;

import com.virtusa.orderservice.entity.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCTSERVICE")
public interface ProductClient {
    @GetMapping("/products/check-stock/{id}")
    boolean checkStock(@PathVariable("id") Long productId);
}
