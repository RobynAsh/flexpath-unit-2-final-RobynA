package org.example.controllers;

import org.example.daos.ProductDao;
import org.example.models.Product;
import org.example.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for products.
 * This class is responsible for handling all HTTP requests related to products.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/products")
@PreAuthorize("isAuthenticated()")
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

    /**
     * Returns product matching the id
     *
     * @param id
     * @return Product
     */
    @GetMapping(path = "/{id}")
    public Product get(@PathVariable String id) {
        Product product = productDao.getProductById(Integer.parseInt(id));
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }

    /**
     * Creates a new product.
     *
     * @param product The Product to create.
     * @return The created product.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }

    /**
     * Updates a product.
     *
     * @param newProduct The Product's updates.
     * @return The updated product.
     */
    @PutMapping(path = "/{id}")
    public Product update(@RequestBody Product newProduct, @PathVariable String id) {
        int productId = Integer.parseInt(id);
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        return productDao.updateProduct(productId, newProduct);
    }

    /**
     * Updates a product.
     *
     * @param id
     * @return The updated product.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable String id) {
        int productId = Integer.parseInt(id);
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }

        return productDao.deleteProduct(productId);
    }
}
