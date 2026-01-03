package com.supermarket.dao.impl;

import com.supermarket.dao.CategoryDao;
import com.supermarket.entity.Category;
import com.supermarket.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public int add(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO categories(name, description, parent_id, status, create_time, update_time) VALUES(?, ?, ?, ?, now(), now())";
            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription() != null ? category.getDescription() : "");
            ps.setInt(3, category.getParentId() != null ? category.getParentId() : 0);
            ps.setInt(4, category.getStatus() != null ? category.getStatus() : 1);
            
            result = ps.executeUpdate();
            System.out.println("执行插入分类SQL: " + sql);
            System.out.println("插入分类参数: name=" + category.getName() + 
                    ", description=" + (category.getDescription() != null ? category.getDescription() : "") + 
                    ", parent_id=" + (category.getParentId() != null ? category.getParentId() : 0) + 
                    ", status=" + (category.getStatus() != null ? category.getStatus() : 1));
        } catch (SQLException e) {
            System.out.println("添加分类SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        
        return result;
    }

    @Override
    public int update(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE categories SET name=?, description=?, parent_id=?, status=?, update_time=now() WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription() != null ? category.getDescription() : "");
            ps.setInt(3, category.getParentId() != null ? category.getParentId() : 0);
            ps.setInt(4, category.getStatus() != null ? category.getStatus() : 1);
            ps.setInt(5, category.getId() != null ? category.getId() : 0);
            
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("更新分类SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        
        return result;
    }

    @Override
    public int deleteById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int result = 0;
        
        try {
            if (id == null) {
                return 0;
            }
            
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM categories WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("删除分类SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, null);
        }
        
        return result;
    }

    @Override
    public Category findById(Integer id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Category category = null;
        
        try {
            if (id == null) {
                return null;
            }
            
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM categories WHERE id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setParentId(rs.getInt("parent_id"));
                category.setStatus(rs.getInt("status"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                category.setUpdateTime(rs.getTimestamp("update_time"));
            }
        } catch (SQLException e) {
            System.out.println("查询分类SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        
        return category;
    }

    @Override
    public List<Category> findAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categoryList = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM categories";
            ps = conn.prepareStatement(sql);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setParentId(rs.getInt("parent_id"));
                category.setStatus(rs.getInt("status"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                category.setUpdateTime(rs.getTimestamp("update_time"));
                
                categoryList.add(category);
            }
        } catch (SQLException e) {
            System.out.println("查询所有分类SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        
        return categoryList;
    }

    @Override
    public List<Category> findByParentId(Integer parentId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categoryList = new ArrayList<>();
        
        try {
            if (parentId == null) {
                parentId = 0;
            }
            
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM categories WHERE parent_id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, parentId);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                category.setParentId(rs.getInt("parent_id"));
                category.setStatus(rs.getInt("status"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                category.setUpdateTime(rs.getTimestamp("update_time"));
                
                categoryList.add(category);
            }
        } catch (SQLException e) {
            System.out.println("根据父ID查询分类SQL异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, ps, rs);
        }
        
        return categoryList;
    }
} 