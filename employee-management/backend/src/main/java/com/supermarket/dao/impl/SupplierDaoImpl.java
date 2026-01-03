package com.supermarket.dao.impl;

import com.supermarket.dao.SupplierDao;
import com.supermarket.entity.Supplier;
import com.supermarket.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoImpl implements SupplierDao {

    @Override
    public int add(Supplier supplier) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO suppliers(name, contact_person, phone, email, address, description, status, create_time, update_time) VALUES(?, ?, ?, ?, ?, ?, ?, now(), now())";
            ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson() != null ? supplier.getContactPerson() : "");
            ps.setString(3, supplier.getPhone() != null ? supplier.getPhone() : "");
            ps.setString(4, supplier.getEmail() != null ? supplier.getEmail() : "");
            ps.setString(5, supplier.getAddress() != null ? supplier.getAddress() : "");
            ps.setString(6, supplier.getDescription() != null ? supplier.getDescription() : "");
            ps.setInt(7, supplier.getStatus());
            
            result = ps.executeUpdate();
            
            if (result <= 0) {
                System.out.println("添加供应商失败: 数据库插入返回结果为 " + result);
            }
        } catch (SQLException e) {
            System.out.println("添加供应商SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        
        return result;
    }

    @Override
    public int update(Supplier supplier) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=?, description=?, status=?, update_time=now() WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson() != null ? supplier.getContactPerson() : "");
            ps.setString(3, supplier.getPhone() != null ? supplier.getPhone() : "");
            ps.setString(4, supplier.getEmail() != null ? supplier.getEmail() : "");
            ps.setString(5, supplier.getAddress() != null ? supplier.getAddress() : "");
            ps.setString(6, supplier.getDescription() != null ? supplier.getDescription() : "");
            ps.setInt(7, supplier.getStatus());
            ps.setInt(8, supplier.getId() != null ? supplier.getId() : 0);
            
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("更新供应商SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        
        return result;
    }

    @Override
    public int deleteById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM suppliers WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        
        return result;
    }

    @Override
    public Supplier findById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Supplier supplier = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM suppliers WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setEmail(rs.getString("email"));
                supplier.setAddress(rs.getString("address"));
                supplier.setDescription(rs.getString("description"));
                supplier.setStatus(rs.getInt("status"));
                supplier.setCreateTime(rs.getTimestamp("create_time"));
                supplier.setUpdateTime(rs.getTimestamp("update_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        
        return supplier;
    }

    @Override
    public List<Supplier> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Supplier> supplierList = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM suppliers";
            ps = conn.prepareStatement(sql);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setId(rs.getInt("id"));
                supplier.setName(rs.getString("name"));
                supplier.setContactPerson(rs.getString("contact_person"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setEmail(rs.getString("email"));
                supplier.setAddress(rs.getString("address"));
                supplier.setDescription(rs.getString("description"));
                supplier.setStatus(rs.getInt("status"));
                supplier.setCreateTime(rs.getTimestamp("create_time"));
                supplier.setUpdateTime(rs.getTimestamp("update_time"));
                
                supplierList.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        
        return supplierList;
    }
} 