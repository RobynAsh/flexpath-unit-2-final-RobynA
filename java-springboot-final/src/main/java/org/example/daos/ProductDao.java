package org.example.daos;

import org.example.exceptions.DaoException;
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
        return jdbcTemplate.query("SELECT * FROM products ORDER BY id;", this::mapToProduct);
    }

    /**
     * Get product by id.
     *
     * @param id
     * @return Product
     */
    public Product getProductById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return Product The created product, including its generated ID.
     */
    public Product createProduct(Product product) {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, product.getName());
                statement.setBigDecimal(2, product.getPrice());
                return statement;
            }, keyHolder);

            Number generatedId = keyHolder.getKey();
            if (generatedId == null) {
                throw new DaoException("Failed to retrieve the generated product ID.");
            }

            product.setId(generatedId.intValue());
            return product;
        } catch (DataAccessException e) {
            throw new DaoException("Failed to create product.");
        }
    }

    /**
     * Updates an existing product
     *
     * @param id The product's id
     * @param product The updated product
     * @return The updated product
     */
    public Product updateProduct(int id, Product product) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, product.getName(), product.getPrice(), id);
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected, expected at least one.");
        } else {
            return getProductById(id);
        }
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     */
    public int deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ? ";
        return jdbcTemplate.update(sql, id);
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
