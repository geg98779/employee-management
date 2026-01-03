package com.supermarket.service.impl;

import com.supermarket.dao.OrderDao;
import com.supermarket.dao.OrderItemDao;
import com.supermarket.dao.ProductDao;
import com.supermarket.dao.impl.OrderDaoImpl;
import com.supermarket.dao.impl.OrderItemDaoImpl;
import com.supermarket.dao.impl.ProductDaoImpl;
import com.supermarket.entity.Order;
import com.supermarket.entity.OrderItem;
import com.supermarket.entity.Product;
import com.supermarket.service.OrderService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao = new OrderDaoImpl();
    private OrderItemDao orderItemDao = new OrderItemDaoImpl();
    private ProductDao productDao = new ProductDaoImpl();
    
    @Override
    public Map<String, Object> createOrder(Order order) {
        Map<String, Object> result = new HashMap<>();
        
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(0); // 初始状态：待付款
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        
        // 保存订单
        int orderId = orderDao.add(order);
        if (orderId <= 0) {
            result.put("success", false);
            result.put("message", "创建订单失败");
            return result;
        }
        
        // 保存订单项并更新库存
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                // 设置订单ID和订单号
                item.setOrderId(orderId);
                item.setOrderNo(orderNo);
                
                // 获取商品信息
                Product product = productDao.findById(item.getProductId());
                if (product != null) {
                    item.setProductName(product.getName());
                    item.setProductPrice(product.getPrice());
                    
                    // 计算总价
                    item.setTotalPrice(product.getPrice().multiply(new java.math.BigDecimal(item.getQuantity())));
                    
                    // 保存订单项
                    orderItemDao.add(item);
                    
                    // 更新库存
                    int newStock = product.getStock() - item.getQuantity();
                    if (newStock < 0) {
                        result.put("success", false);
                        result.put("message", "商品 " + product.getName() + " 库存不足");
                        return result;
                    }
                    productDao.updateStock(product.getId(), newStock);
                }
            }
        }
        
        // 返回成功结果
        result.put("success", true);
        result.put("message", "创建订单成功");
        result.put("data", orderDao.findById(orderId));
        return result;
    }
    
    @Override
    public int updateOrder(Order order) {
        order.setUpdateTime(new Date());
        return orderDao.update(order);
    }
    
    @Override
    public int updateOrderStatus(int id, int status) {
        return orderDao.updateStatus(id, status);
    }
    
    @Override
    public int deleteOrder(int id) {
        // 先删除订单项，再删除订单
        orderItemDao.deleteByOrderId(id);
        return orderDao.deleteById(id);
    }
    
    @Override
    public Order getOrderById(int id) {
        Order order = orderDao.findById(id);
        if (order != null) {
            // 获取订单项
            List<OrderItem> orderItems = orderItemDao.findByOrderId(id);
            order.setOrderItems(orderItems);
        }
        return order;
    }
    
    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderDao.findAll();
        // 获取每个订单的订单项
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemDao.findByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }
        return orders;
    }
    
    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = orderDao.findByUserId(userId);
        // 获取每个订单的订单项
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemDao.findByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }
        return orders;
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成格式：ORD + 年月日 + 6位随机数
        String date = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        String random = String.format("%06d", (int)(Math.random() * 1000000));
        return "ORD" + date + random;
    }
} 