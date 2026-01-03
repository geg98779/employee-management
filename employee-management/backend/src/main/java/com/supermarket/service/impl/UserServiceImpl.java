package com.supermarket.service.impl;

import com.supermarket.dao.UserDao;
import com.supermarket.dao.impl.UserDaoImpl;
import com.supermarket.entity.User;
import com.supermarket.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();
    
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户名是否已存在
        User existUser = userDao.findByUsername(user.getUsername());
        if (existUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        
        // 设置默认角色为普通用户
        user.setRole(2);
        
        // 添加用户
        int rows = userDao.add(user);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            result.put("success", false);
            result.put("message", "注册失败");
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        
        // 根据用户名查询用户
        User user = userDao.findByUsername(username);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户名不存在");
            return result;
        }
        
        // 验证密码
        if (!user.getPassword().equals(password)) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }
        
        // 登录成功
        result.put("success", true);
        result.put("message", "登录成功");
        result.put("user", user);
        
        return result;
    }
    
    @Override
    public Map<String, Object> addUser(User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户名是否已存在
        User existUser = userDao.findByUsername(user.getUsername());
        if (existUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        
        // 添加用户
        int rows = userDao.add(user);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "添加成功");
        } else {
            result.put("success", false);
            result.put("message", "添加失败");
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> updateUser(User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 更新用户信息
        int rows = userDao.update(user);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
            result.put("message", "更新失败");
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> deleteUser(int id) {
        Map<String, Object> result = new HashMap<>();
        
        // 删除用户
        int rows = userDao.deleteById(id);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        
        return result;
    }
    
    @Override
    public User getUserById(int id) {
        return userDao.findById(id);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
    
    @Override
    public Map<String, Object> getUsersByPage(int page, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算开始位置
        int start = (page - 1) * limit;
        
        // 查询用户列表
        List<User> userList = userDao.findByPage(start, limit);
        
        // 查询用户总数
        int total = userDao.count();
        
        result.put("users", userList);
        result.put("total", total);
        
        return result;
    }
} 