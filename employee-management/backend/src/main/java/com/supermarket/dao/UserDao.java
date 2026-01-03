package com.supermarket.dao;

import com.supermarket.entity.User;
import java.util.List;

public interface UserDao {
    /**
     * 添加用户
     * @param user 用户对象
     * @return 影响的行数
     */
    int add(User user);
    
    /**
     * 根据ID更新用户信息
     * @param user 用户对象
     * @return 影响的行数
     */
    int update(User user);
    
    /**
     * 根据ID删除用户
     * @param id 用户ID
     * @return 影响的行数
     */
    int deleteById(int id);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(int id);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 分页查询用户
     * @param start 开始位置
     * @param limit 每页数量
     * @return 用户列表
     */
    List<User> findByPage(int start, int limit);
    
    /**
     * 查询用户总数
     * @return 用户总数
     */
    int count();
} 