package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Order;
import org.example.models.Product;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class OrderDao {
    private JdbcTemplate jdbcTemplate;

    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all orders.
     *
     * @return List of Orders
     */
    public List<Order> getOrders() {
        return jdbcTemplate.query("SELECT * FROM orders ORDER BY id;", this::mapToOrders);
    }

    /**
     * Get order by id.
     *
     * @param id
     * @return Order
     */
    public Order getOrderById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", this::mapToOrders, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return Order The created order, including its generated ID.
     */
    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (username) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, order.getUsername());
                return statement;
            }, keyHolder);

            Number generatedId = keyHolder.getKey();
            if (generatedId == null) {
                throw new DaoException("Failed to retrieve the generated order ID.");
            }

            order.setId(generatedId.intValue());
            return order;
        } catch (DataAccessException e) {
            throw new DaoException("Failed to create order.");
        }
    }

    /**
     * Updates an existing order
     *
     * @param id The order's id
     * @param order The updated order
     * @return The updated order
     */
    public Order updateOrder(int id, Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, order.getUsername(), id);
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getOrderById(id);
        }
    }

    /**
     * Deletes an order.
     *
     * @param id The id of the order.
     */
    public int deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ? ";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Maps a row in the ResultSet to an Order object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Order The order object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Order mapToOrders(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        return new Order(
                id,
                username
        );
    }
}
