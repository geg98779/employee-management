package com.supermarket.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.supermarket.common.Result;
import com.supermarket.entity.Supplier;
import com.supermarket.service.SupplierService;
import com.supermarket.service.impl.SupplierServiceImpl;

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

@WebServlet("/api/supplier/*")
public class SupplierServlet extends HttpServlet {
    private SupplierService supplierService = new SupplierServiceImpl();
    
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
            // 获取所有供应商
            getAllSuppliers(req, resp);
        } else {
            // 根据ID获取供应商
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                getSupplierById(id, resp);
            } catch (NumberFormatException e) {
                sendErrorResponse(resp, "无效的供应商ID");
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 添加供应商
        addSupplier(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 更新供应商
        updateSupplier(req, resp);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendErrorResponse(resp, "缺少供应商ID");
            return;
        }
        
        // 删除供应商
        String idStr = pathInfo.substring(1);
        try {
            int id = Integer.parseInt(idStr);
            deleteSupplier(id, resp);
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "无效的供应商ID");
        }
    }
    
    private void getAllSuppliers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 获取搜索参数
        String name = req.getParameter("name");
        String contact = req.getParameter("contact");
        String statusStr = req.getParameter("status");
        
        Integer status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = Integer.parseInt(statusStr);
            } catch (NumberFormatException e) {
                // 忽略无效的状态值
            }
        }
        
        // 获取所有供应商
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        
        // 根据搜索条件过滤
        if ((name != null && !name.isEmpty()) || (contact != null && !contact.isEmpty()) || status != null) {
            List<Supplier> filteredSuppliers = new ArrayList<>();
            
            for (Supplier supplier : suppliers) {
                boolean nameMatch = name == null || name.isEmpty() || 
                    supplier.getName().toLowerCase().contains(name.toLowerCase());
                boolean contactMatch = contact == null || contact.isEmpty() || 
                    (supplier.getContactPerson() != null && supplier.getContactPerson().toLowerCase().contains(contact.toLowerCase()));
                boolean statusMatch = status == null || supplier.getStatus() == status;
                
                if (nameMatch && contactMatch && statusMatch) {
                    filteredSuppliers.add(supplier);
                }
            }
            
            suppliers = filteredSuppliers;
        }
        
        sendSuccessResponse(resp, suppliers);
    }
    
    private void getSupplierById(int id, HttpServletResponse resp) throws IOException {
        Supplier supplier = supplierService.getSupplierById(id);
        if (supplier != null) {
            sendSuccessResponse(resp, supplier);
        } else {
            sendErrorResponse(resp, "供应商不存在");
        }
    }
    
    private void addSupplier(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("开始处理添加供应商请求");
        
        Supplier supplier = parseSupplierFromRequest(req);
        if (supplier == null) {
            System.out.println("解析供应商数据失败，返回无效数据错误");
            sendErrorResponse(resp, "无效的供应商数据");
            return;
        }
        
        // 打印关键字段进行校验
        System.out.println("准备添加供应商: 名称=" + supplier.getName() + ", 联系人=" + supplier.getContactPerson() + ", 电话=" + supplier.getPhone());
        
        int result = supplierService.addSupplier(supplier);
        if (result > 0) {
            System.out.println("供应商添加成功: ID=" + result);
            sendSuccessResponse(resp, "添加成功");
        } else {
            System.out.println("供应商添加失败: 返回结果=" + result);
            sendErrorResponse(resp, "添加失败");
        }
    }
    
    private void updateSupplier(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Supplier supplier = parseSupplierFromRequest(req);
        if (supplier == null || supplier.getId() <= 0) {
            sendErrorResponse(resp, "无效的供应商数据");
            return;
        }
        
        int result = supplierService.updateSupplier(supplier);
        if (result > 0) {
            sendSuccessResponse(resp, "更新成功");
        } else {
            sendErrorResponse(resp, "更新失败");
        }
    }
    
    private void deleteSupplier(int id, HttpServletResponse resp) throws IOException {
        int result = supplierService.deleteSupplier(id);
        if (result > 0) {
            sendSuccessResponse(resp, "删除成功");
        } else {
            sendErrorResponse(resp, "删除失败");
        }
    }
    
    private Supplier parseSupplierFromRequest(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        String json = sb.toString();
        System.out.println("收到的供应商JSON数据: " + json);
        
        try {
            Supplier supplier = gson.fromJson(json, Supplier.class);
            
            // 检查必要字段
            if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
                System.out.println("解析后的供应商对象为空或名称为空");
                return null;
            }
            
            // 确保状态有默认值
            if (supplier.getStatus() != 0 && supplier.getStatus() != 1) {
                supplier.setStatus(1); // 默认为正常状态
            }
            
            return supplier;
        } catch (JsonSyntaxException e) {
            System.out.println("解析供应商JSON数据出错: " + e.getMessage());
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