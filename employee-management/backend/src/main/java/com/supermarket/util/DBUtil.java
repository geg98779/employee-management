package com.supermarket.util;

import java.sql.*;

public class DBUtil {
    // 数据库连接信息
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/supermarket";
    private static final String USERNAME = "root";

    // 数据库密码
    private static final String PASSWORD = "root";

    // 加载驱动
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        try {
            // 构建带参数的连接URL
            String connectionUrl = URL + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai"
                                   + "&useSSL=false&allowPublicKeyRetrieval=true&connectionCollation=utf8mb4_unicode_ci"
                                   + "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10";

            // 尝试打印连接信息便于调试
            System.out.println("尝试连接数据库: " + connectionUrl);

            Connection conn = DriverManager.getConnection(connectionUrl, USERNAME, PASSWORD);
            System.out.println("数据库连接成功");
            return conn;
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 关闭资源
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 