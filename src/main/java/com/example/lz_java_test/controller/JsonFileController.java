package com.example.lz_java_test.controller;

import com.example.lz_java_test.dto.NovelRequest;
import com.example.lz_java_test.dto.ThreeBodyRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/lz-api")
public class JsonFileController {

    @Autowired
    private ObjectMapper objectMapper; // Spring Boot 自动配置的 Jackson ObjectMapper

    /**
     * 方法一：直接读取 JSON 文件并返回字符串（最简单）
     */
    @GetMapping("/trisomy/raw")
    public String getUsersRaw() throws IOException {
        // 从 resources 目录下读取文件
        ClassPathResource resource = new ClassPathResource("data/trisomy.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 方法二：读取为 Map 列表（灵活，适合结构不固定）
     */
    @GetMapping("/trisomy/map")
    public Map<String, Object> getUsersMap() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/trisomy.json");
        System.out.println("文件是否存在: " + resource.exists()); // 应该输出 true
        if (!resource.exists()) {
            System.err.println("文件不存在: data/users.json");
            return Map.of();
        }
        try (InputStream inputStream = resource.getInputStream()) {
            // ✅ 显式指定泛型类型，避免 unchecked 警告
            return objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            System.err.println("读取 JSON 失败: " + e.getMessage());
            e.printStackTrace();
            return Map.of();
        }
    }

    /**
     *  读取 三体小说 第一部 数据
     */
    @GetMapping("/threeBody.one")
    public Map<String, Object> threeBody1() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ClassPathResource resource = new ClassPathResource("data/threeBody/threeBody1.json");
            System.out.println("文件是否存在: " + resource.exists());
            
            if (!resource.exists()) {
                System.err.println("文件不存在: data/threeBody/threeBody1.json");
                
                // 返回标准错误格式
                response.put("success", false);
                response.put("code", 404);
                response.put("message", "文件不存在");
                response.put("timestamp", System.currentTimeMillis());
                return response;
            }
            
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, Object> data = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<Map<String, Object>>() {}
                );
                
                // 构建标准成功响应
                // Map<String, Object> result = new HashMap<>();
                // result.put("content", data);
                // result.put("fileInfo", Map.of(
                //     "fileName", "threeBody1.json",
                //     "totalItems", data.size()
                // ));
                
                response.put("success", true);
                response.put("code", 200);
                response.put("message", "数据获取成功");
                response.put("data", data);
                // response.put("timestamp", System.currentTimeMillis());
                response.put("time", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                
                return response;
            }
            
        } catch (IOException e) {
            System.err.println("读取 JSON 失败: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取数据失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
        }
    }

    /**
     *  读取 三体小说 数据
     */
    @PostMapping("/threeBody")
    public Map<String, Object> threeBody(
            @RequestBody ThreeBodyRequest request
    ) {

        Map<String, Object> response = new HashMap<>();

        Integer part = request.getPart();
        if (part == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "part 不能为空");
            return response;
        }

        Map<Integer, String> fileMap = Map.of(
                1, "threeBody1.json",
                2, "threeBody2.json",
                3, "threeBody3.json"
        );

        String fileName = fileMap.get(part);
        if (fileName == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "非法 part 参数");
            return response;
        }

        String path = "data/threeBody/" + fileName;

        try {
            ClassPathResource resource = new ClassPathResource(path);
            System.out.println("文件是否存在: " + resource.exists());
            
            if (!resource.exists()) {
                System.err.println("文件不存在: " + path);
                
                // 返回标准错误格式
                response.put("success", false);
                response.put("code", 404);
                response.put("message", "文件不存在");
                response.put("timestamp", System.currentTimeMillis());
                return response;
            }

            try (InputStream inputStream = resource.getInputStream()) {

                Map<String, Object> data = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<Map<String, Object>>() {}
                );
                
                response.put("message", "数据获取成功");
                response.put("data", data);
                // 这里先简单返回文件名，方便你测试
                response.put("success", true);
                response.put("code", 200);
                response.put("file", fileName);
                response.put("time", LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                return response;
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取失败：" + e.getMessage());
            return response;
        }
    }

    /**
     *  读取 小说 数据
     */
    @PostMapping("/getNovelData")
    public Map<String, Object> getNovelData(
            @RequestBody NovelRequest request
    ) {

        Map<String, Object> response = new HashMap<>();

        String novelName = request.getNovelName(); // 小说名
        String folderName = request.getFolderName(); // 文件夹名

        // System.out.println("novelName: " + novelName);
        // System.out.println("folderName: " + folderName);

        if (novelName == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "novelName 不能为空");
            return response;
        }

        String folderPath = folderName != null
            ? "data/" + folderName + '/'
            : "data/";

        String path = folderPath + novelName + ".json";

        try {
            ClassPathResource resource = new ClassPathResource(path);
            // System.out.println("文件是否存在: " + resource.exists());
            
            if (!resource.exists()) {
                System.err.println("文件不存在: " + path);
                
                // 返回标准错误格式
                response.put("success", false);
                response.put("code", 404);
                response.put("message", "文件不存在");
                response.put("timestamp", System.currentTimeMillis());
                return response;
            }

            try (InputStream inputStream = resource.getInputStream()) {

                Map<String, Object> data = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<Map<String, Object>>() {}
                );
                
                response.put("message", "数据获取成功");
                response.put("data", data);
                // 这里先简单返回文件名，方便你测试
                response.put("success", true);
                response.put("code", 200);
                response.put("file", novelName);
                response.put("time", LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                return response;
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取失败：" + e.getMessage());
            return response;
        }
    }

    /**
     *  每日弹窗 数据
     */
    @GetMapping("/pop.daily")
    public Map<String, Object> popDaily() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ClassPathResource resource = new ClassPathResource("data/daily.json");
            System.out.println("文件是否存在: " + resource.exists());
            
            if (!resource.exists()) {
                System.err.println("文件不存在: data/daily.json");
                
                // 返回标准错误格式
                response.put("success", false);
                response.put("code", 404);
                response.put("message", "文件不存在");
                response.put("timestamp", System.currentTimeMillis());
                return response;
            }
            
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, Object> data = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<Map<String, Object>>() {}
                );
                
                response.put("success", true);
                response.put("code", 200);
                response.put("message", "数据获取成功");
                response.put("data", data);
                response.put("time", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                return response;
            }
            
        } catch (IOException e) {
            System.err.println("读取 JSON 失败: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取数据失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
        }
    }
}