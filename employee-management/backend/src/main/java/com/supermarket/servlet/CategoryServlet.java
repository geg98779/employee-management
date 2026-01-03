package com.supermarket.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.supermarket.common.Result;
import com.supermarket.entity.Category;
import com.supermarket.service.CategoryService;
import com.supermarket.service.impl.CategoryServiceImpl;

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

@WebServlet("/api/category/*")
public class CategoryServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryServiceImpl();
    
    // 创建一个自定义的TypeAdapter处理Integer类型，能够处理空字符串
    private static final TypeAdapter<Integer> INTEGER_TYPE_ADAPTER = new TypeAdapter<Integer>() {
        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value);
            }
        }

        @Override
        public Integer read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (in.peek() == JsonToken.STRING) {
                String stringValue = in.nextString();
                if (stringValue.isEmpty()) {
                    return null;
                }
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return in.nextInt();
        }
    };
    
    // 使用GsonBuilder配置自定义适配器
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Integer.class, INTEGER_TYPE_ADAPTER)
            .create();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // 获取所有分类
            getAllCategories(req, resp);
        } else {
            // 根据ID获取分类
            String idStr = pathInfo.substring(1);
            try {
                Integer id = Integer.parseInt(idStr);
                getCategoryById(id, resp);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "无效的分类ID");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 添加分类
        addCategory(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 更新分类
        updateCategory(req, resp);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, "缺少分类ID");
            return;
        }
        
        // 删除分类
        String idStr = pathInfo.substring(1);
        try {
            Integer id = Integer.parseInt(idStr);
            deleteCategory(id, resp);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "无效的分类ID");
        }
    }
    
    private void getAllCategories(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取搜索参数
        String name = req.getParameter("name");
        String parentIdStr = req.getParameter("parentId");
        
        Integer parentId = null;
        if (parentIdStr != null && !parentIdStr.isEmpty()) {
            try {
                parentId = Integer.parseInt(parentIdStr);
            } catch (NumberFormatException e) {
                // 忽略无效的父分类ID
            }
        }
        
        // 获取所有分类
        List<Category> categories = categoryService.getAllCategories();
        
        // 根据搜索条件过滤
        if ((name != null && !name.isEmpty()) || parentId != null) {
            List<Category> filteredCategories = new ArrayList<>();
            
            for (Category category : categories) {
                boolean nameMatch = name == null || name.isEmpty() || 
                    category.getName().toLowerCase().contains(name.toLowerCase());
                boolean parentIdMatch = parentId == null || 
                    (category.getParentId() != null && category.getParentId().equals(parentId));
                
                if (nameMatch && parentIdMatch) {
                    filteredCategories.add(category);
                }
            }
            
            categories = filteredCategories;
        }
        
        sendSuccessResponse(resp, categories);
    }
    
    private void getCategoryById(Integer id, HttpServletResponse resp) throws IOException {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            sendSuccessResponse(resp, category);
        } else {
            sendErrorResponse(resp, "分类不存在");
        }
    }
    
    private void addCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("开始处理添加分类请求");
        
        Category category = parseCategoryFromRequest(req);
        if (category == null) {
            System.out.println("解析分类数据失败，返回无效数据错误");
            sendErrorResponse(resp, "无效的分类数据");
            return;
        }
        
        // 打印关键字段进行校验
        System.out.println("准备添加分类: 名称=" + category.getName() + ", 父分类ID=" + category.getParentId());
        
        int result = categoryService.addCategory(category);
        if (result > 0) {
            System.out.println("分类添加成功: ID=" + result);
            sendSuccessResponse(resp, "添加成功");
        } else {
            System.out.println("分类添加失败: 返回结果=" + result);
            sendErrorResponse(resp, "添加失败");
        }
    }
    
    private void updateCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Category category = parseCategoryFromRequest(req);
        if (category == null || category.getId() == null || category.getId() <= 0) {
            sendErrorResponse(resp, "无效的分类数据");
            return;
        }
        
        int result = categoryService.updateCategory(category);
        if (result > 0) {
            sendSuccessResponse(resp, "更新成功");
        } else {
            sendErrorResponse(resp, "更新失败");
        }
    }
    
    private void deleteCategory(Integer id, HttpServletResponse resp) throws IOException {
        // 检查是否有子分类
        List<Category> children = categoryService.getCategoriesByParentId(id);
        if (children != null && !children.isEmpty()) {
            sendErrorResponse(resp, "该分类下有子分类，不能删除");
            return;
        }
        
        int result = categoryService.deleteCategory(id);
        if (result > 0) {
            sendSuccessResponse(resp, "删除成功");
        } else {
            sendErrorResponse(resp, "删除失败");
        }
    }
    
    private Category parseCategoryFromRequest(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        String json = sb.toString();
        System.out.println("收到的分类JSON数据: " + json);
        
        try {
            Category category = gson.fromJson(json, Category.class);
            
            // 检查必要字段
            if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
                System.out.println("解析后的分类对象为空或名称为空");
                return null;
            }
            
            // 确保父分类ID有默认值
            if (category.getParentId() == null || category.getParentId() < 0) {
                category.setParentId(0); // 默认为顶级分类
            }
            
            // 确保状态有默认值
            if (category.getStatus() == null || (category.getStatus() != 0 && category.getStatus() != 1)) {
                category.setStatus(1); // 默认为正常状态
            }
            
            return category;
        } catch (JsonSyntaxException e) {
            System.out.println("解析分类JSON数据出错: " + e.getMessage());
            e.printStackTrace();
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