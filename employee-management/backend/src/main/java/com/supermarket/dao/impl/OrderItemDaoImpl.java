package com.supermarket.dao.impl;

import com.supermarket.dao.OrderItemDao;
import com.supermarket.entity.OrderItem;
import com.supermarket.util.DBUtil;

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
 * 订单项DAO实现类
 */
public class OrderItemDaoImpl implements OrderItemDao {

    @Override
    public int add(OrderItem orderItem) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO order_items (order_id, order_no, product_id, product_name, product_price, quantity, total_price, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            Date now = new Date();
            pstmt.setInt(1, orderItem.getOrderId());
            pstmt.setString(2, orderItem.getOrderNo());
            pstmt.setInt(3, orderItem.getProductId());
            pstmt.setString(4, orderItem.getProductName());
            pstmt.setBigDecimal(5, orderItem.getProductPrice());
            pstmt.setInt(6, orderItem.getQuantity());
            pstmt.setBigDecimal(7, orderItem.getTotalPrice());
            pstmt.setTimestamp(8, new Timestamp(now.getTime()));
            pstmt.setTimestamp(9, new Timestamp(now.getTime()));
            
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
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public int batchAdd(List<OrderItem> orderItems) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            
            String sql = "INSERT INTO order_items (order_id, order_no, product_id, product_name, product_price, quantity, total_price, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            Date now = new Date();
            for (OrderItem orderItem : orderItems) {
                pstmt.setInt(1, orderItem.getOrderId());
                pstmt.setString(2, orderItem.getOrderNo());
                pstmt.setInt(3, orderItem.getProductId());
                pstmt.setString(4, orderItem.getProductName());
                pstmt.setBigDecimal(5, orderItem.getProductPrice());
                pstmt.setInt(6, orderItem.getQuantity());
                pstmt.setBigDecimal(7, orderItem.getTotalPrice());
                pstmt.setTimestamp(8, new Timestamp(now.getTime()));
                pstmt.setTimestamp(9, new Timestamp(now.getTime()));
                
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            conn.commit(); // 提交事务
            
            for (int result : results) {
                if (result > 0) {
                    affectedRows += result;
                }
            }
            
            return affectedRows;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return 0;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int update(OrderItem orderItem) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE order_items SET product_id = ?, product_name = ?, product_price = ?, quantity = ?, total_price = ?, update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, orderItem.getProductId());
            pstmt.setString(2, orderItem.getProductName());
            pstmt.setBigDecimal(3, orderItem.getProductPrice());
            pstmt.setInt(4, orderItem.getQuantity());
            pstmt.setBigDecimal(5, orderItem.getTotalPrice());
            pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            pstmt.setInt(7, orderItem.getId());
            
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
            String sql = "DELETE FROM order_items WHERE id = ?";
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
    public int deleteByOrderId(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM order_items WHERE order_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public OrderItem findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM order_items WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderItemFromResultSet(rs);
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
    public List<OrderItem> findByOrderId(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM order_items WHERE order_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orderItems.add(extractOrderItemFromResultSet(rs));
            }
            
            return orderItems;
        } catch (SQLException e) {
            e.printStackTrace();
            return orderItems;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<OrderItem> findByOrderNo(String orderNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderItem> orderItems = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM order_items WHERE order_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderNo);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orderItems.add(extractOrderItemFromResultSet(rs));
            }
            
            return orderItems;
        } catch (SQLException e) {
            e.printStackTrace();
            return orderItems;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    /**
     * 从ResultSet中提取订单项信息
     * @param rs ResultSet
     * @return 订单项对象
     * @throws SQLException SQL异常
     */
    private OrderItem extractOrderItemFromResultSet(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        
        orderItem.setId(rs.getInt("id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setOrderNo(rs.getString("order_no"));
        orderItem.setProductId(rs.getInt("product_id"));
        orderItem.setProductName(rs.getString("product_name"));
        orderItem.setProductPrice(rs.getBigDecimal("product_price"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setTotalPrice(rs.getBigDecimal("total_price"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            orderItem.setCreateTime(new Date(createTime.getTime()));
        }
        
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            orderItem.setUpdateTime(new Date(updateTime.getTime()));
        }
        
        return orderItem;
    }
} 