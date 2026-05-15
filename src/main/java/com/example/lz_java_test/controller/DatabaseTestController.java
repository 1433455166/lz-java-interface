// package com.example.lz_java_test.controller;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.jdbc.core.JdbcTemplate;
//
// import javax.sql.DataSource;
// import java.sql.Connection;
// import java.sql.DatabaseMetaData;
// import java.sql.SQLException;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// @RestController
// @RequestMapping("/api/db-test")
// public class DatabaseTestController {
//     
//     @Autowired
//     private JdbcTemplate jdbcTemplate;
//     
//     @Autowired
//     private DataSource dataSource;
//     
//     @GetMapping("/connection")
//     public Map<String, Object> testConnection() {
//         Map<String, Object> result = new HashMap<>();
//         
//         try (Connection connection = dataSource.getConnection()) {
//             DatabaseMetaData metaData = connection.getMetaData();
//             
//             result.put("connected", true);
//             result.put("databaseProductName", metaData.getDatabaseProductName());
//             result.put("databaseVersion", metaData.getDatabaseProductVersion());
//             result.put("url", metaData.getURL());
//             result.put("username", metaData.getUserName());
//             
//             // 查询数据库中的表
//             List<String> tables = jdbcTemplate.query(
//                 "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()",
//                 (rs, rowNum) -> rs.getString("table_name"));
//             result.put("tables", tables);
//             
//         } catch (SQLException e) {
//             result.put("connected", false);
//             result.put("error", e.getMessage());
//         }
//         
//         return result;
//     }
//     
//     @GetMapping("/users-count")
//     public Map<String, Object> getUsersCount() {
//         Map<String, Object> result = new HashMap<>();
//         
//         try {
//             Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
//             result.put("count", count != null ? count : 0);
//         } catch (Exception e) {
//             result.put("count", 0);
//             result.put("error", e.getMessage());
//         }
//         
//         return result;
//     }
// }