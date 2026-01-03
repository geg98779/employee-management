package com.supermarket.dao.impl;

import com.supermarket.dao.ProductDao;
import com.supermarket.entity.Product;
import com.supermarket.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品DAO实现类
 */
public class ProductDaoImpl implements ProductDao {

    @Override
    public int add(Product product) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO products (name, description, category_id, price, cost_price, stock, unit, barcode, status, supplier_id, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            Date now = new Date();
            
            // 打印SQL和参数，便于调试
            System.out.println("执行SQL: " + sql);
            System.out.println("商品名称: " + product.getName());
            System.out.println("商品分类ID: " + product.getCategoryId());
            System.out.println("商品价格: " + product.getPrice());
            System.out.println("商品成本价: " + product.getCostPrice());
            System.out.println("商品库存: " + product.getStock());
            System.out.println("商品单位: " + product.getUnit());
            System.out.println("商品条码: " + product.getBarcode());
            System.out.println("商品状态: " + product.getStatus());
            System.out.println("供应商ID: " + product.getSupplierId());
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getCategoryId());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setBigDecimal(5, product.getCostPrice());
            pstmt.setInt(6, product.getStock());
            pstmt.setString(7, product.getUnit());
            pstmt.setString(8, product.getBarcode());
            pstmt.setInt(9, product.getStatus());
            pstmt.setInt(10, product.getSupplierId());
            pstmt.setTimestamp(11, new Timestamp(now.getTime()));
            pstmt.setTimestamp(12, new Timestamp(now.getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            
            return generatedId;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("添加商品SQL异常: " + e.getMessage());
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int update(Product product) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE products SET name = ?, description = ?, category_id = ?, price = ?, cost_price = ?, " +
                    "stock = ?, unit = ?, barcode = ?, status = ?, supplier_id = ?, update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setInt(3, product.getCategoryId());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setBigDecimal(5, product.getCostPrice());
            pstmt.setInt(6, product.getStock());
            pstmt.setString(7, product.getUnit());
            pstmt.setString(8, product.getBarcode());
            pstmt.setInt(9, product.getStatus());
            pstmt.setInt(10, product.getSupplierId());
            pstmt.setTimestamp(11, new Timestamp(new Date().getTime()));
            pstmt.setInt(12, product.getId());
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int deleteById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM products WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public Product findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                    "WHERE p.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Product> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                    "ORDER BY p.id DESC";
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
            
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            return products;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Product> findByCategoryId(int categoryId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT p.*, c.name as category_name, s.name as supplier_name " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
                    "WHERE p.category_id = ? " +
                    "ORDER BY p.id DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
            
            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            return products;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int updateStock(int id, int stock) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE products SET stock = ?, update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, stock);
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setInt(3, id);
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    
    /**
     * 从ResultSet中提取商品信息
     * @param rs ResultSet
     * @return 商品对象
     * @throws SQLException SQL异常
     */
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setCostPrice(rs.getBigDecimal("cost_price"));
        product.setStock(rs.getInt("stock"));
        product.setUnit(rs.getString("unit"));
        product.setBarcode(rs.getString("barcode"));
        product.setStatus(rs.getInt("status"));
        product.setSupplierId(rs.getInt("supplier_id"));
        product.setSupplierName(rs.getString("supplier_name"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            product.setCreateTime(new Date(createTime.getTime()));
        }
        
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            product.setUpdateTime(new Date(updateTime.getTime()));
        }
        
        return product;
    }
} 