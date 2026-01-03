package com.supermarket.service;

import com.supermarket.entity.User;
import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册结果
     */
    Map<String, Object> register(User user);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Map<String, Object> login(String username, String password);
    
    /**
     * 添加用户
     * @param user 用户对象
     * @return 添加结果
     */
    Map<String, Object> addUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新结果
     */
    Map<String, Object> updateUser(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    Map<String, Object> deleteUser(int id);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(int id);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 分页查询用户
     * @param page 页码
     * @param limit 每页数量
     * @return 分页结果
     */
    Map<String, Object> getUsersByPage(int page, int limit);
} 