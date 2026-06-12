package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Order;
import org.example.models.OrderItem;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class OrderItemDao {
    private JdbcTemplate jdbcTemplate;

    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all orderItems.
     *
     * @return List of OrderItems
     */
    public List<OrderItem> getOrderItems() {
        return jdbcTemplate.query("SELECT * FROM order_items ORDER BY id;", this::mapToOrderItems);
    }

    /**
     * Get orderItem by id.
     *
     * @param id
     * @return OrderItem
     */
    public OrderItem getOrderItemById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM order_items WHERE id = ?", this::mapToOrderItems, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Get orderItems by orderId
     *
     * @param orderId
     * @return List of order items
     */
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return jdbcTemplate.query(
                "SELECT * FROM order_items WHERE order_id = ? ORDER BY id;",
                this::mapToOrderItems,
                orderId
        );
    }

    /**
     * Creates a new orderItem.
     *
     * @param orderItem The orderItem to create.
     * @return OrderItem The created orderItem, including its generated ID.
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, orderItem.getOrderId());
                statement.setInt(2, orderItem.getProductId());
                statement.setInt(3, orderItem.getQuantity());
                return statement;
            }, keyHolder);

            Number generatedId = keyHolder.getKey();
            if (generatedId == null) {
                throw new DaoException("Failed to retrieve the generated orderItem ID.");
            }

            orderItem.setId(generatedId.intValue());
            return orderItem;
        } catch (DataAccessException e) {
            throw new DaoException("Failed to create orderItem.");
        }
    }

    /**
     * Updates an existing orderItem
     *
     * @param id The orderItem's id
     * @param orderItem The updated orderItem
     * @return The updated orderItem
     */
    public OrderItem updateOrderItem(int id, OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity(), id);
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getOrderItemById(id);
        }
    }

    /**
     * Deletes an order item.
     *
     * @param id The id of the order item.
     */
    public int deleteOrderItem(int id) {
        String sql = "DELETE FROM order_items WHERE id = ? ";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * Maps a row in the ResultSet to an OrderItem object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return OrderItem The orderItem object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private OrderItem mapToOrderItems(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        int orderId = resultSet.getInt("order_id");
        int productId = resultSet.getInt("product_id");
        int quantity = resultSet.getInt("quantity");
        return new OrderItem(
                id,
                orderId,
                productId,
                quantity
        );
    }
}
