package org.example.controllers;

import org.example.daos.OrderDao;
import org.example.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for orders.
 * This class is responsible for handling all HTTP requests related to orders.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderController {
    /**
     * The order data access object
     */
    @Autowired
    private OrderDao orderDao;

    /**
     * Gets all orders.
     *
     * @return A list of all orders.
     */
    @GetMapping
    public List<Order> getAll() {
        return orderDao.getOrders();
    }

    /**
     * Returns order matching the id
     *
     * @param id
     * @return Order
     */
    @GetMapping(path = "/{id}")
    public Order get(@PathVariable String id) {
        Order order = orderDao.getOrderById(Integer.parseInt(id));
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return order;
    }

    /**
     * Creates a new order.
     *
     * @param order The Order to create.
     * @return The created order.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Order create(@RequestBody Order order) {
        return orderDao.createOrder(order);
    }

    /**
     * Updates a order.
     *
     * @param newOrder The Order's updates.
     * @return The updated order.
     */
    @PutMapping(path = "/{id}")
    public Order update(@RequestBody Order newOrder, @PathVariable String id) {
        int orderId = Integer.parseInt(id);
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        return orderDao.updateOrder(orderId, newOrder);
    }

    /**
     * Updates an order.
     *
     * @param id
     * @return The updated order.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable String id) {
        int orderId = Integer.parseInt(id);
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        return orderDao.deleteOrder(orderId);
    }
}
