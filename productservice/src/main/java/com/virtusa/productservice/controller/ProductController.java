package com.virtusa.productservice.controller;

import com.virtusa.productservice.entity.Product;
import com.virtusa.productservice.exceptions.ProductNotFoundException;
import com.virtusa.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository repo;

    @GetMapping("/check-stock/{id}")
    public boolean checkStock(@PathVariable Long id) throws ProductNotFoundException{
        Optional<Product> product =repo.findById(id);
        if(product.isPresent())
           return true;
        throw new ProductNotFoundException("Product not found");
    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        return repo.save(product);
    }

    @GetMapping()
    public List<Product> getAllProducts(){
        List<Product> lists = repo.findAll();
        return lists;
    }
}
