package com.supermarket.servlet;

import com.google.gson.Gson;
import com.supermarket.common.Result;
import com.supermarket.entity.User;
import com.supermarket.service.UserService;
import com.supermarket.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    private UserService userService = new UserServiceImpl();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有用户
            getAllUsers(req, resp);
        } else if (pathInfo.startsWith("/page/")) {
            // 分页获取用户
            getUsersByPage(req, resp);
        } else {
            // 根据ID获取用户
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                getUserById(id, resp);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "无效的用户ID");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 添加用户
            addUser(req, resp);
        } else if (pathInfo.equals("/login")) {
            // 用户登录
            login(req, resp);
        } else if (pathInfo.equals("/register")) {
            // 用户注册
            register(req, resp);
        } else {
            sendErrorResponse(resp, "无效的请求路径");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 更新用户
            updateUser(req, resp);
        } else if (pathInfo.equals("/profile")) {
            // 更新个人信息
            updateProfile(req, resp);
        } else {
            sendErrorResponse(resp, "无效的请求路径");
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, "缺少用户ID");
            return;
        }
        
        // 删除用户
        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            deleteUser(id, resp);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "无效的用户ID");
        }
    }
    
    private void getAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取搜索参数
        String username = req.getParameter("username");
        String roleStr = req.getParameter("role");
        
        Integer role = null;
        if (roleStr != null && !roleStr.isEmpty()) {
            try {
                role = Integer.parseInt(roleStr);
            } catch (NumberFormatException e) {
                // 忽略无效的角色值
            }
        }
        
        // 获取所有用户
        List<User> users = userService.getAllUsers();
        
        // 根据搜索条件过滤
        if ((username != null && !username.isEmpty()) || role != null) {
            List<User> filteredUsers = new ArrayList<>();
            
            for (User user : users) {
                boolean usernameMatch = username == null || username.isEmpty() || 
                    user.getUsername().toLowerCase().contains(username.toLowerCase());
                boolean roleMatch = role == null || user.getRole() == role;
                
                if (usernameMatch && roleMatch) {
                    filteredUsers.add(user);
                }
            }
            
            users = filteredUsers;
        }
        
        sendSuccessResponse(resp, users);
    }
    
    private void getUsersByPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pageStr = req.getParameter("page");
        String limitStr = req.getParameter("limit");
        
        int page = 1;
        int limit = 10;
        
        try {
            if (pageStr != null) {
                page = Integer.parseInt(pageStr);
            }
            if (limitStr != null) {
                limit = Integer.parseInt(limitStr);
            }
        } catch (NumberFormatException e) {
            // 使用默认值
        }
        
        Map<String, Object> result = userService.getUsersByPage(page, limit);
        sendSuccessResponse(resp, result);
    }
    
    private void getUserById(int id, HttpServletResponse resp) throws IOException {
        User user = userService.getUserById(id);
        if (user != null) {
            sendSuccessResponse(resp, user);
        } else {
            sendErrorResponse(resp, "用户不存在");
        }
    }
    
    private void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = parseUserFromRequest(req);
        if (user == null) {
            sendErrorResponse(resp, "无效的用户数据");
            return;
        }
        
        Map<String, Object> result = userService.addUser(user);
        if ((Boolean) result.get("success")) {
            sendSuccessResponse(resp, result.get("message"));
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
    
    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = parseUserFromRequest(req);
        if (user == null || user.getId() <= 0) {
            sendErrorResponse(resp, "无效的用户数据");
            return;
        }
        
        Map<String, Object> result = userService.updateUser(user);
        if ((Boolean) result.get("success")) {
            sendSuccessResponse(resp, result.get("message"));
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
    
    private void deleteUser(int id, HttpServletResponse resp) throws IOException {
        Map<String, Object> result = userService.deleteUser(id);
        if ((Boolean) result.get("success")) {
            sendSuccessResponse(resp, result.get("message"));
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
    
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        if (username == null || password == null) {
            sendErrorResponse(resp, "用户名和密码不能为空");
            return;
        }
        
        Map<String, Object> result = userService.login(username, password);
        if ((Boolean) result.get("success")) {
            // 登录成功，将用户信息存入session
            HttpSession session = req.getSession();
            session.setAttribute("user", result.get("user"));
            
            sendSuccessResponse(resp, result.get("user"));
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
    
    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = parseUserFromRequest(req);
        if (user == null) {
            sendErrorResponse(resp, "无效的用户数据");
            return;
        }
        
        Map<String, Object> result = userService.register(user);
        if ((Boolean) result.get("success")) {
            sendSuccessResponse(resp, result.get("message"));
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
    
    private User parseUserFromRequest(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        try {
            return gson.fromJson(sb.toString(), User.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(gson.toJson(Result.success(data)));
        out.flush();
    }
    
    private void sendErrorResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(gson.toJson(Result.error(message)));
        out.flush();
    }
    
    /**
     * 更新个人信息
     * @param req 请求
     * @param resp 响应
     * @throws IOException IO异常
     */
    private void updateProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = parseUserFromRequest(req);
        if (user == null || user.getId() <= 0) {
            sendErrorResponse(resp, "无效的用户数据");
            return;
        }
        
        // 获取数据库中的用户信息
        User dbUser = userService.getUserById(user.getId());
        if (dbUser == null) {
            sendErrorResponse(resp, "用户不存在");
            return;
        }
        
        // 如果没有提供新密码，则保留原密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(dbUser.getPassword());
        }
        
        // 保持原有角色不变
        user.setRole(dbUser.getRole());
        
        Map<String, Object> result = userService.updateUser(user);
        if ((Boolean) result.get("success")) {
            sendSuccessResponse(resp, "个人信息更新成功");
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
} 