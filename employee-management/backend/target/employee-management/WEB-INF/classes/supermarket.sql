create database supermarket;
use  supermarket;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `parent_id` int NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
                               `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                               `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                               `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `order_id` int NOT NULL,
                                `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                `product_id` int NOT NULL,
                                `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                `product_price` decimal(10, 2) NOT NULL,
                                `quantity` int NOT NULL,
                                `total_price` decimal(10, 2) NOT NULL,
                                `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `order_id`(`order_id` ASC) USING BTREE,
                                INDEX `product_id`(`product_id` ASC) USING BTREE,
                                CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                           `user_id` int NOT NULL,
                           `total_amount` decimal(10, 2) NOT NULL,
                           `status` int NULL DEFAULT 0 COMMENT '状态：0-待付款，1-已付款，2-已发货，3-已完成，4-已取消',
                           `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           `payment_time` datetime NULL DEFAULT NULL,
                           `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                           `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
                           INDEX `user_id`(`user_id` ASC) USING BTREE,
                           CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                             `category_id` int NOT NULL,
                             `price` decimal(10, 2) NOT NULL,
                             `cost_price` decimal(10, 2) NULL DEFAULT NULL,
                             `stock` int NULL DEFAULT 0,
                             `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `status` int NULL DEFAULT 1 COMMENT '状态：1-上架，0-下架',
                             `supplier_id` int NULL DEFAULT NULL,
                             `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                             `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `category_id`(`category_id` ASC) USING BTREE,
                             INDEX `supplier_id`(`supplier_id` ASC) USING BTREE,
                             CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                             CONSTRAINT `products_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for suppliers
-- ----------------------------
DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE `suppliers`  (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                              `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `status` int NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
                              `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                          `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                          `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                          `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                          `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                          `role` int NULL DEFAULT 2 COMMENT '1-管理员，2-普通用户',
                          `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                          `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`) USING BTREE,
                          UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
