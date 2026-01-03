package com.supermarket.servlet;

import com.google.gson.Gson;
import com.supermarket.common.Result;
import com.supermarket.entity.Order;
import com.supermarket.service.OrderService;
import com.supermarket.service.impl.OrderServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/api/order/*")
public class OrderServlet extends HttpServlet {
    private OrderService orderService = new OrderServiceImpl();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有订单
            getAllOrders(req, resp);
        } else if (pathInfo.equals("/user")) {
            // 根据用户ID获取订单
            String userIdStr = req.getParameter("userId");
            if (userIdStr != null) {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    getOrdersByUserId(userId, resp);
                } catch (NumberFormatException e) {
                    sendErrorResponse(resp, "无效的用户ID");
                }
            } else {
                sendErrorResponse(resp, "缺少用户ID参数");
            }
        } else {
            // 根据ID获取订单
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                getOrderById(id, resp);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "无效的订单ID");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 创建订单
        createOrder(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/status")) {
            // 更新订单状态
            updateOrderStatus(req, resp);
        } else {
            // 更新订单
            updateOrder(req, resp);
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, "缺少订单ID");
            return;
        }
        
        // 删除订单
        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            deleteOrder(id, resp);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "无效的订单ID");
        }
    }
    
    private void getAllOrders(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取搜索参数
        String orderNo = req.getParameter("orderNo");
        String statusStr = req.getParameter("status");
        
        Integer status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = Integer.parseInt(statusStr);
            } catch (NumberFormatException e) {
                // 忽略无效的状态值
            }
        }
        
        // 获取所有订单
        List<Order> orders = orderService.getAllOrders();
        
        // 根据搜索条件过滤
        if ((orderNo != null && !orderNo.isEmpty()) || status != null) {
            List<Order> filteredOrders = new ArrayList<>();
            
            for (Order order : orders) {
                boolean orderNoMatch = orderNo == null || orderNo.isEmpty() || 
                    (order.getOrderNo() != null && order.getOrderNo().toLowerCase().contains(orderNo.toLowerCase()));
                boolean statusMatch = status == null || order.getStatus() == status;
                
                if (orderNoMatch && statusMatch) {
                    filteredOrders.add(order);
                }
            }
            
            orders = filteredOrders;
        }
        
        sendSuccessResponse(resp, orders);
    }
    
    private void getOrderById(int id, HttpServletResponse resp) throws IOException {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            sendSuccessResponse(resp, order);
        } else {
            sendErrorResponse(resp, "订单不存在");
        }
    }
    
    private void getOrdersByUserId(int userId, HttpServletResponse resp) throws IOException {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        sendSuccessResponse(resp, orders);
    }
    
    private void createOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Order order = parseOrderFromRequest(req);
        if (order == null) {
            sendErrorResponse(resp, "无效的订单数据");
            return;
        }
        
        Map<String, Object> result = orderService.createOrder(order);
        if ((Boolean) result.get("success")) {
            sendSuccessResponse(resp, result.get("data"));
        } else {
            sendErrorResponse(resp, (String) result.get("message"));
        }
    }
    
    private void updateOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Order order = parseOrderFromRequest(req);
        if (order == null || order.getId() <= 0) {
            sendErrorResponse(resp, "无效的订单数据");
            return;
        }
        
        int result = orderService.updateOrder(order);
        if (result > 0) {
            sendSuccessResponse(resp, "更新成功");
        } else {
            sendErrorResponse(resp, "更新失败");
        }
    }
    
    private void updateOrderStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        String statusStr = req.getParameter("status");
        
        if (idStr == null || statusStr == null) {
            sendErrorResponse(resp, "缺少订单ID或状态参数");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            int status = Integer.parseInt(statusStr);
            
            int result = orderService.updateOrderStatus(id, status);
            if (result > 0) {
                sendSuccessResponse(resp, "状态更新成功");
            } else {
                sendErrorResponse(resp, "状态更新失败");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "无效的订单ID或状态值");
        }
    }
    
    private void deleteOrder(int id, HttpServletResponse resp) throws IOException {
        int result = orderService.deleteOrder(id);
        if (result > 0) {
            sendSuccessResponse(resp, "删除成功");
        } else {
            sendErrorResponse(resp, "删除失败");
        }
    }
    
    private Order parseOrderFromRequest(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        try {
            return gson.fromJson(sb.toString(), Order.class);
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
} 