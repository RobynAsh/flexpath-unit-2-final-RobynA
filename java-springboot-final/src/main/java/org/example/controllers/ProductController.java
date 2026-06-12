package org.example.controllers;

import org.example.daos.ProductDao;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for products.
 * This class is responsible for handling all HTTP requests related to products.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/products")
public class ProductController {
    /**
     * The product data access object
     */
    @Autowired
    private ProductDao productDao;

    /**
     * Gets all products.
     *
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAll() {
        return productDao.getProducts();
    }

    @GetMapping(path = "/{id}")
    public Product get(@PathVariable String id) { return productDao.getProductById(Integer.parseInt(id)); }
}
