package org.example.controllers;

import org.example.daos.OrderItemDao;
import org.example.models.Order;
import org.example.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for orderItems.
 * This class is responsible for handling all HTTP requests related to orderItems.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/order-items")
@PreAuthorize("isAuthenticated()")
public class OrderItemController {
    /**
     * The orderItem data access object
     */
    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Gets all orderItems.
     *
     * @return A list of all orderItems.
     */
    @GetMapping
    public List<OrderItem> getAll() {
        return orderItemDao.getOrderItems();
    }

    /**
     * Returns orderItem matching the id
     *
     * @param id
     * @return OrderItem
     */
    @GetMapping(path = "/{id}")
    public OrderItem get(@PathVariable String id) {
        OrderItem orderItem = orderItemDao.getOrderItemById(Integer.parseInt(id));
        if (orderItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return orderItem;
    }

    /**
     * Creates a new orderItem.
     *
     * @param orderItem The OrderItem to create.
     * @return The created orderItem.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.createOrderItem(orderItem);
    }

    /**
     * Updates an orderItem.
     *
     * @param newOrderItem The OrderItem's updates.
     * @return The updated orderItem.
     */
    @PutMapping(path = "/{id}")
    public OrderItem update(@RequestBody OrderItem newOrderItem, @PathVariable String id) {
        int orderItemId = Integer.parseInt(id);
        OrderItem orderItem = orderItemDao.getOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        return orderItemDao.updateOrderItem(orderItemId, newOrderItem);
    }

    /**
     * Updates an orderItem.
     *
     * @param id
     * @return The updated orderItem.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable String id) {
        int orderItemId = Integer.parseInt(id);
        OrderItem orderItem = orderItemDao.getOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        return orderItemDao.deleteOrderItem(orderItemId);
    }
}
