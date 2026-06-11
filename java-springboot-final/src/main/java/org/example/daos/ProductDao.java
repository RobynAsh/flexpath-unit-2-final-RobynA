package org.example.daos;

import org.example.models.Product;
import org.example.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all products.
     *
     * @return List of Products
     */
    public List<Product> getProducts() {
        return jdbcTemplate.query("SELECT * FROM products ORDER BY name;", this::mapToProduct);
    }

    /**
     * Maps a row in the ResultSet to a Product object.
     *
     * @param resultSet The result set to map.
     * @param rowNumber The row number.
     * @return Product The product object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Product mapToProduct(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        BigDecimal price = resultSet.getBigDecimal("price");
        return new Product(
                id,
                name,
                price
        );
    }
}
