package com.example.lz_java_test.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
}