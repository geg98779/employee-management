package com.supermarket.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.supermarket.common.Result;
import com.supermarket.entity.Product;
import com.supermarket.service.ProductService;
import com.supermarket.service.impl.ProductServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/api/product/*")
public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductServiceImpl();
    private Gson gson = new Gson();
    
    // 创建一个自定义的Gson实例，用于处理空字符串转数字的情况
    private Gson customGson = new GsonBuilder()
        .registerTypeAdapter(int.class, new JsonDeserializer<Integer>() {
            @Override
            public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonNull() || json.getAsString().isEmpty()) {
                    return 0; // 返回默认值0
                }
                try {
                    return json.getAsInt();
                } catch (NumberFormatException e) {
                    return 0; // 解析失败时返回默认值0
                }
            }
        })
        .registerTypeAdapter(BigDecimal.class, new JsonDeserializer<BigDecimal>() {
            @Override
            public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonNull() || json.getAsString().isEmpty()) {
                    return BigDecimal.ZERO; // 返回默认值0
                }
                try {
                    return new BigDecimal(json.getAsString());
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO; // 解析失败时返回默认值0
                }
            }
        })
        .create();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有商品
            getAllProducts(req, resp);
        } else if (pathInfo.equals("/category")) {
            // 根据分类ID获取商品
            String categoryIdStr = req.getParameter("categoryId");
            if (categoryIdStr != null) {
                try {
                    int categoryId = Integer.parseInt(categoryIdStr);
                    getProductsByCategoryId(categoryId, resp);
                } catch (NumberFormatException e) {
                    sendErrorResponse(resp, "无效的分类ID");
                }
            } else {
                sendErrorResponse(resp, "缺少分类ID参数");
            }
        } else {
            // 根据ID获取商品
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                getProductById(id, resp);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "无效的商品ID");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 添加商品
        addProduct(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 更新商品
        updateProduct(req, resp);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, "缺少商品ID");
            return;
        }
        
        // 删除商品
        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            deleteProduct(id, resp);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "无效的商品ID");
        }
    }
    
    private void getAllProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取搜索参数
        String name = req.getParameter("name");
        String categoryIdStr = req.getParameter("categoryId");
        
        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                // 忽略无效的分类ID
            }
        }
        
        // 获取所有商品
        List<Product> products = productService.getAllProducts();
        
        // 根据搜索条件过滤
        if ((name != null && !name.isEmpty()) || categoryId != null) {
            List<Product> filteredProducts = new ArrayList<>();
            
            for (Product product : products) {
                boolean nameMatch = name == null || name.isEmpty() || 
                    product.getName().toLowerCase().contains(name.toLowerCase());
                boolean categoryMatch = categoryId == null || product.getCategoryId() == categoryId;
                
                if (nameMatch && categoryMatch) {
                    filteredProducts.add(product);
                }
            }
            
            products = filteredProducts;
        }
        
        sendSuccessResponse(resp, products);
    }
    
    private void getProductById(int id, HttpServletResponse resp) throws IOException {
        Product product = productService.getProductById(id);
        if (product != null) {
            sendSuccessResponse(resp, product);
        } else {
            sendErrorResponse(resp, "商品不存在");
        }
    }
    
    private void getProductsByCategoryId(int categoryId, HttpServletResponse resp) throws IOException {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        sendSuccessResponse(resp, products);
    }
    
    private void addProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Product product = parseProductFromRequest(req);
            if (product == null) {
                sendErrorResponse(resp, "无效的商品数据");
                return;
            }
            
            // 验证商品数据
            String validationError = validateProduct(product);
            if (validationError != null) {
                sendErrorResponse(resp, validationError);
                return;
            }
            
            // 打印接收到的商品数据，便于调试
            System.out.println("接收到的商品数据: " + product.toString());
            
            int result = productService.addProduct(product);
            if (result > 0) {
                sendSuccessResponse(resp, "添加成功");
            } else {
                sendErrorResponse(resp, "添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(resp, "添加商品异常: " + e.getMessage());
        }
    }
    
    /**
     * 验证商品数据的完整性
     * @param product 商品对象
     * @return 如果验证通过返回null，否则返回错误信息
     */
    private String validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return "商品名称不能为空";
        }
        if (product.getCategoryId() <= 0) {
            return "请选择商品分类";
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "商品价格必须大于0";
        }
        if (product.getCostPrice() == null) {
            product.setCostPrice(BigDecimal.ZERO); // 设置默认值
        }
        if (product.getStock() < 0) {
            return "商品库存不能为负数";
        }
        if (product.getUnit() == null || product.getUnit().trim().isEmpty()) {
            return "商品单位不能为空";
        }
        if (product.getStatus() != 0 && product.getStatus() != 1) {
            product.setStatus(1); // 默认上架
        }
        if (product.getSupplierId() <= 0) {
            return "请选择供应商";
        }
        return null;
    }
    
    private void updateProduct(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Product product = parseProductFromRequest(req);
        if (product == null || product.getId() <= 0) {
            sendErrorResponse(resp, "无效的商品数据");
            return;
        }
        
        int result = productService.updateProduct(product);
        if (result > 0) {
            sendSuccessResponse(resp, "更新成功");
        } else {
            sendErrorResponse(resp, "更新失败");
        }
    }
    
    private void deleteProduct(int id, HttpServletResponse resp) throws IOException {
        int result = productService.deleteProduct(id);
        if (result > 0) {
            sendSuccessResponse(resp, "删除成功");
        } else {
            sendErrorResponse(resp, "删除失败");
        }
    }
    
    private Product parseProductFromRequest(HttpServletRequest req) throws IOException {
        // 设置请求编码
        req.setCharacterEncoding("UTF-8");
        
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        String jsonData = sb.toString();
        System.out.println("接收到的JSON数据: " + jsonData);
        
        try {
            // 使用自定义的Gson实例解析JSON
            return customGson.fromJson(jsonData, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("JSON解析异常: " + e.getMessage());
            return null;
        }
    }
    
    private void sendSuccessResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(gson.toJson(Result.success(data)));
        out.flush();
    }
    
    private void sendErrorResponse(HttpServletResponse resp, String message) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(gson.toJson(Result.error(message)));
        out.flush();
    }
} 