package com.supermarket.dao.impl;

import com.supermarket.dao.OrderDao;
import com.supermarket.entity.Order;
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
 * 订单DAO实现类
 */
public class OrderDaoImpl implements OrderDao {

    @Override
    public int add(Order order) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO `orders` (order_no, user_id, total_amount, status, payment_method, payment_time, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, order.getOrderNo());
            pstmt.setInt(2, order.getUserId());
            pstmt.setBigDecimal(3, order.getTotalAmount());
            pstmt.setInt(4, order.getStatus());
            pstmt.setString(5, order.getPaymentMethod());
            
            if (order.getPaymentTime() != null) {
                pstmt.setTimestamp(6, new Timestamp(order.getPaymentTime().getTime()));
            } else {
                pstmt.setNull(6, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setTimestamp(7, new Timestamp(order.getCreateTime().getTime()));
            pstmt.setTimestamp(8, new Timestamp(order.getUpdateTime().getTime()));
            
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
    public int update(Order order) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE `orders` SET user_id = ?, total_amount = ?, status = ?, payment_method = ?, " +
                    "payment_time = ?, update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, order.getUserId());
            pstmt.setBigDecimal(2, order.getTotalAmount());
            pstmt.setInt(3, order.getStatus());
            pstmt.setString(4, order.getPaymentMethod());
            
            if (order.getPaymentTime() != null) {
                pstmt.setTimestamp(5, new Timestamp(order.getPaymentTime().getTime()));
            } else {
                pstmt.setNull(5, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setTimestamp(6, new Timestamp(order.getUpdateTime().getTime()));
            pstmt.setInt(7, order.getId());
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    @Override
    public int updateStatus(int id, int status) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE `orders` SET status = ?, update_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, status);
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

    @Override
    public int deleteById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM `orders` WHERE id = ?";
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
    public Order findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT o.*, u.username FROM `orders` o LEFT JOIN users u ON o.user_id = u.id WHERE o.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
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
    public List<Order> findAll() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT o.*, u.username FROM `orders` o LEFT JOIN users u ON o.user_id = u.id ORDER BY o.id DESC";
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
            
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return orders;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Order> findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT o.*, u.username FROM `orders` o LEFT JOIN users u ON o.user_id = u.id WHERE o.user_id = ? ORDER BY o.id DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
            
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return orders;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT o.*, u.username FROM `orders` o LEFT JOIN users u ON o.user_id = u.id WHERE o.order_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderNo);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractOrderFromResultSet(rs);
            }
            
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
    
    /**
     * 从ResultSet中提取订单信息
     * @param rs ResultSet
     * @return 订单对象
     * @throws SQLException SQL异常
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        
        order.setId(rs.getInt("id"));
        order.setOrderNo(rs.getString("order_no"));
        order.setUserId(rs.getInt("user_id"));
        order.setUsername(rs.getString("username"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getInt("status"));
        order.setPaymentMethod(rs.getString("payment_method"));
        
        Timestamp paymentTime = rs.getTimestamp("payment_time");
        if (paymentTime != null) {
            order.setPaymentTime(new Date(paymentTime.getTime()));
        }
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            order.setCreateTime(new Date(createTime.getTime()));
        }
        
        Timestamp updateTime = rs.getTimestamp("update_time");
        if (updateTime != null) {
            order.setUpdateTime(new Date(updateTime.getTime()));
        }
        
        return order;
    }
} 