package com.example.lz_java_test;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController // 自动将返回值转为 JSON 并响应 HTTP 请求
@RequestMapping("/lz-api") // 狼族所有接口前缀改成使用 /lz-api
public class HelloController {

    // GET http://localhost:8080/api/hello
    @GetMapping("/hello")
    public Map<String, Object> sayHello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, 这是你的第一个 API！");
        response.put("status", "success");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    // GET http://localhost:8080/api/hello/your-name
    @GetMapping("/hello/{name}")
    public Map<String, Object> sayHelloTo(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "你好，" + name + "！");
        response.put("status", "success");
        return response;
    }

    // POST http://localhost:8080/api/hello
    @PostMapping("/hello")
    public Map<String, Object> createHello(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String name = request.get("name");
        response.put("message", "已创建用户：" + name);
        response.put("status", "created");
        return response;
    }
}