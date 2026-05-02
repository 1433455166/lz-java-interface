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
import java.util.ArrayList;
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
     *  获取小说列表数据（不含章节内容）
     */
    @PostMapping("/getNovelList")
    public Map<String, Object> getNovelList(
            @RequestBody NovelRequest request
    ) {

        Map<String, Object> response = new HashMap<>();

        String novelName = request.getNovelName(); // 小说名
        String folderName = request.getFolderName(); // 文件夹名

        if (novelName == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "novelName 不能为空");
            return response;
        }

        // 如果是unsheathed小说，特殊处理目录中的多个卷文件
        if ("unsheathed".equals(novelName)) {
            return getUnsheathedList(response);
        }

        String folderPath =
            (folderName == null || folderName.isBlank())
            ? "data/"
            : "data/" + folderName + '/';

        String path = folderPath + novelName + ".json";

        try {
            ClassPathResource resource = new ClassPathResource(path);
            
            if (!resource.exists()) {
                System.err.println("文件不存在: " + path);
                
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
                
                // 只返回小说基本信息和章节列表（不包含章节内容）
                Map<String, Object> novelInfo = new HashMap<>();
                
                // 复制基本信息
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String key = entry.getKey().toString();
                    if (!"chapters".equals(key) && !"volume".equals(key)) { // 不复制章节内容或卷信息
                        novelInfo.put(entry.getKey(), entry.getValue());
                    }
                }
                
                // 如果有卷信息，处理卷和章节信息（不包含内容）
                Object volumeObj = data.get("volume");
                if (volumeObj instanceof java.util.List) {
                    java.util.List<?> fullVolumes = (java.util.List<?>) volumeObj;
                    java.util.List<Map<String, Object>> simplifiedVolumes = new ArrayList<>();
                    
                    for (Object volumeItem : fullVolumes) {
                        if (volumeItem instanceof Map) {
                            Map<?, ?> fullVolume = (Map<?, ?>) volumeItem;
                            Map<String, Object> simpleVolume = new HashMap<>();
                            
                            // 复制卷的基本信息
                            for (Map.Entry<?, ?> volumeEntry : fullVolume.entrySet()) {
                                String volumeKey = volumeEntry.getKey().toString();
                                if (!"chapters".equals(volumeKey)) {
                                    simpleVolume.put(volumeKey, volumeEntry.getValue());
                                }
                            }
                            
                            // 处理卷中的章节（不包含内容）
                            Object chaptersObj = fullVolume.get("chapters");
                            if (chaptersObj instanceof java.util.List) {
                                java.util.List<?> fullChapters = (java.util.List<?>) chaptersObj;
                                java.util.List<Map<String, Object>> simplifiedChapters = new ArrayList<>();
                                
                                for (Object chapter : fullChapters) {
                                    if (chapter instanceof Map) {
                                        Map<?, ?> fullChapter = (Map<?, ?>) chapter;
                                        Map<String, Object> simpleChapter = new HashMap<>();
                                        
                                        // 只保留章节ID、标题和其他信息，但不包含内容
                                        for (Map.Entry<?, ?> chapterEntry : fullChapter.entrySet()) {
                                            String keyStr = chapterEntry.getKey().toString();
                                            if (!"content".equalsIgnoreCase(keyStr)) {
                                                simpleChapter.put(keyStr, chapterEntry.getValue());
                                            }
                                        }
                                        
                                        simplifiedChapters.add(simpleChapter);
                                    }
                                }
                                
                                simpleVolume.put("chapters", simplifiedChapters);
                            }
                            
                            simplifiedVolumes.add(simpleVolume);
                        }
                    }
                    
                    novelInfo.put("volume", simplifiedVolumes);
                } else {
                    // 如果没有卷信息，直接处理章节（兼容之前的数据结构）
                    Object chaptersObj = data.get("chapters");
                    if (chaptersObj instanceof java.util.List) {
                        java.util.List<?> fullChapters = (java.util.List<?>) chaptersObj;
                        java.util.List<Map<String, Object>> simplifiedChapters = new ArrayList<>();
                        
                        for (Object chapter : fullChapters) {
                            if (chapter instanceof Map) {
                                Map<?, ?> fullChapter = (Map<?, ?>) chapter;
                                Map<String, Object> simpleChapter = new HashMap<>();
                                
                                // 只保留章节ID、标题和其他信息，但不包含内容
                                for (Map.Entry<?, ?> chapterEntry : fullChapter.entrySet()) {
                                    String keyStr = chapterEntry.getKey().toString();
                                    if (!"content".equalsIgnoreCase(keyStr)) {
                                        simpleChapter.put(keyStr, chapterEntry.getValue());
                                    }
                                }
                                
                                simplifiedChapters.add(simpleChapter);
                            }
                        }
                        
                        novelInfo.put("chapters", simplifiedChapters);
                    }
                }
                
                response.put("message", "小说列表数据获取成功");
                response.put("data", novelInfo);
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
     *  获取小说章节内容信息接口
     */
    @PostMapping("/getNovelChapterContent")
    public Map<String, Object> getNovelChapterContent(
            @RequestBody NovelRequest request
    ) {

        Map<String, Object> response = new HashMap<>();

        String novelName = request.getNovelName(); // 小说名
        String folderName = request.getFolderName(); // 文件夹名
        Integer chapterId = request.getChapterId(); // 章节ID

        if (novelName == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "novelName 不能为空");
            return response;
        }

        if (chapterId == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "chapterId 不能为空");
            return response;
        }

        // 如果是unsheathed小说，特殊处理目录中的多个卷文件
        if ("unsheathed".equals(novelName)) {
            return getUnsheathedChapterContent(response, chapterId);
        }

        String folderPath =
            (folderName == null || folderName.isBlank())
            ? "data/"
            : "data/" + folderName + '/';

        String path = folderPath + novelName + ".json";

        try {
            ClassPathResource resource = new ClassPathResource(path);
            
            if (!resource.exists()) {
                System.err.println("文件不存在: " + path);
                
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
                
                // 查找指定章节的内容
                Object chaptersObj = data.get("chapters");
                if (chaptersObj instanceof java.util.List) {
                    java.util.List<?> chaptersList = (java.util.List<?>) chaptersObj;
                    
                    Object targetChapter = null;
                    for (Object chapter : chaptersList) {
                        if (chapter instanceof Map) {
                            Map<?, ?> chapterMap = (Map<?, ?>) chapter;
                            Object idObj = chapterMap.get("id");
                            if (idObj instanceof Integer && idObj.equals(chapterId)) {
                                targetChapter = chapter;
                                break;
                            }
                        }
                    }
                    
                    if (targetChapter != null) {
                        response.put("message", "章节内容获取成功");
                        response.put("data", targetChapter); // 返回完整章节信息（包含内容）
                        response.put("chapterId", chapterId);
                        response.put("success", true);
                        response.put("code", 200);
                    } else {
                        response.put("success", false);
                        response.put("code", 404);
                        response.put("message", "未找到指定章节");
                        return response;
                    }
                } else {
                    response.put("success", false);
                    response.put("code", 400);
                    response.put("message", "小说数据中不包含章节信息或格式不正确");
                    return response;
                }

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
     *  读取 小说 数据 (保留原有接口兼容旧版)
     */
    @PostMapping("/getNovelData")
    public Map<String, Object> getNovelData(
            @RequestBody NovelRequest request
    ) {

        Map<String, Object> response = new HashMap<>();

        String novelName = request.getNovelName(); // 小说名
        String folderName = request.getFolderName(); // 文件夹名
        Integer chapterId = request.getChapterId(); // 章节ID

        // System.out.println("novelName: " + novelName);
        // System.out.println("folderName: " + folderName);

        if (novelName == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "novelName 不能为空");
            return response;
        }

        String folderPath =
            (folderName == null || folderName.isBlank())
            ? "data/"
            : "data/" + folderName + '/';

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
                
                // 如果指定了章节ID，则只返回该章节的数据
                if (chapterId != null) {
                    // 尝试从data中获取chapters数组
                    Object chaptersObj = data.get("chapters");
                    if (chaptersObj instanceof java.util.List) {
                        java.util.List<?> chaptersList = (java.util.List<?>) chaptersObj;
                        
                        // 查找对应ID的章节
                        Object targetChapter = null;
                        for (Object chapter : chaptersList) {
                            if (chapter instanceof Map) {
                                Map<?, ?> chapterMap = (Map<?, ?>) chapter;
                                Object idObj = chapterMap.get("id");
                                if (idObj instanceof Integer && idObj.equals(chapterId)) {
                                    targetChapter = chapter;
                                    break;
                                }
                            }
                        }
                        
                        if (targetChapter != null) {
                            response.put("message", "章节数据获取成功");
                            response.put("data", targetChapter); // 只返回指定章节
                            response.put("chapterId", chapterId);
                        } else {
                            response.put("success", false);
                            response.put("code", 404);
                            response.put("message", "未找到指定章节");
                            return response;
                        }
                    } else {
                        response.put("success", false);
                        response.put("code", 400);
                        response.put("message", "小说数据中不包含章节信息或格式不正确");
                        return response;
                    }
                } else {
                    // 如果没有指定章节ID，返回整本小说
                    response.put("message", "小说数据获取成功");
                    response.put("data", data);
                }
                
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

    /**
     * 获取剑来小说列表数据（从目录中的多个卷文件）
     */
    private Map<String, Object> getUnsheathedList(Map<String, Object> response) {
        try {
            // 剑来小说包含多个卷，从volume1.json开始自动查找存在的卷文件
            java.util.List<Map<String, Object>> volumes = new ArrayList<>();
            
            // 动态查找卷文件，从第1卷开始
            int volumeNum = 1;
            while (true) {
                String volumePath = "data/unsheathed/volume" + volumeNum + ".json";
                ClassPathResource resource = new ClassPathResource(volumePath);
                
                if (!resource.exists()) {
                    // 如果找不到当前卷文件，则停止查找
                    break;
                }
                
                try (InputStream inputStream = resource.getInputStream()) {
                    Map<String, Object> volumeData = objectMapper.readValue(
                        inputStream, 
                        new TypeReference<Map<String, Object>>() {}
                    );
                    
                    // 处理卷信息，移除章节内容
                    Map<String, Object> simpleVolume = new HashMap<>();
                    
                    // 复制卷的基本信息
                    for (Map.Entry<String, Object> entry : volumeData.entrySet()) {
                        String key = entry.getKey().toString();
                        if (!"chapters".equals(key)) {
                            simpleVolume.put(key, entry.getValue());
                        }
                    }
                    
                    // 处理章节（不包含内容）
                    Object chaptersObj = volumeData.get("chapters");
                    if (chaptersObj instanceof java.util.List) {
                        java.util.List<?> fullChapters = (java.util.List<?>) chaptersObj;
                        java.util.List<Map<String, Object>> simplifiedChapters = new ArrayList<>();
                        
                        for (Object chapter : fullChapters) {
                            if (chapter instanceof Map) {
                                Map<?, ?> fullChapter = (Map<?, ?>) chapter;
                                Map<String, Object> simpleChapter = new HashMap<>();
                                
                                // 只保留章节ID、标题和其他信息，但不包含内容
                                for (Map.Entry<?, ?> chapterEntry : fullChapter.entrySet()) {
                                    String keyStr = chapterEntry.getKey().toString();
                                    if (!"content".equalsIgnoreCase(keyStr)) {
                                        simpleChapter.put(keyStr, chapterEntry.getValue());
                                    }
                                }
                                
                                simplifiedChapters.add(simpleChapter);
                            }
                        }
                        
                        simpleVolume.put("chapters", simplifiedChapters);
                    }
                    
                    volumes.add(simpleVolume);
                }
                
                volumeNum++;
            }
            
            // 构建响应
            Map<String, Object> novelInfo = new HashMap<>();
            novelInfo.put("title", "剑来");
            novelInfo.put("author", "烽火戏诸侯");
            novelInfo.put("volumes", volumes);
            
            response.put("message", "剑来小说列表数据获取成功");
            response.put("data", novelInfo);
            response.put("success", true);
            response.put("code", 200);
            response.put("file", "unsheathed");
            response.put("time", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取剑来小说失败：" + e.getMessage());
            return response;
        }
    }

    /**
     * 获取剑来小说特定章节内容（从目录中的多个卷文件中查找）
     */
    private Map<String, Object> getUnsheathedChapterContent(Map<String, Object> response, Integer chapterId) {
        try {
            // 剑来小说包含多个卷，从volume1.json开始自动查找章节
            int volumeNum = 1;
            while (true) {
                String volumePath = "data/unsheathed/volume" + volumeNum + ".json";
                ClassPathResource resource = new ClassPathResource(volumePath);
                
                if (!resource.exists()) {
                    // 如果找不到当前卷文件，则停止查找
                    break;
                }
                
                try (InputStream inputStream = resource.getInputStream()) {
                    Map<String, Object> volumeData = objectMapper.readValue(
                        inputStream, 
                        new TypeReference<Map<String, Object>>() {}
                    );
                    
                    // 在当前卷中查找指定章节
                    Object chaptersObj = volumeData.get("chapters");
                    if (chaptersObj instanceof java.util.List) {
                        java.util.List<?> fullChapters = (java.util.List<?>) chaptersObj;
                        
                        for (Object chapter : fullChapters) {
                            if (chapter instanceof Map) {
                                Map<?, ?> chapterMap = (Map<?, ?>) chapter;
                                Object idObj = chapterMap.get("id");
                                if (idObj instanceof Integer && idObj.equals(chapterId)) {
                                    // 找到指定章节，返回完整章节信息（包含内容）
                                    response.put("message", "章节内容获取成功");
                                    response.put("data", chapter);
                                    response.put("chapterId", chapterId);
                                    response.put("success", true);
                                    response.put("code", 200);
                                    
                                    response.put("file", "unsheathed");
                                    response.put("time", LocalDateTime.now()
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                    
                                    return response;
                                }
                            }
                        }
                    }
                }
                
                volumeNum++;
            }
            
            // 如果遍历完所有卷都没有找到指定章节
            response.put("success", false);
            response.put("code", 404);
            response.put("message", "未找到指定章节");
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取剑来小说章节失败：" + e.getMessage());
            return response;
        }
    }

        /**
     * 获取特定卷的信息
     */
    @PostMapping("/getNovelVolume")
    public Map<String, Object> getNovelVolume(@RequestBody NovelRequest request) {
        Map<String, Object> response = new HashMap<>();

        String novelName = request.getNovelName();
        Integer volumeNumber = request.getVolumeNumber(); // 卷号

        if (novelName == null) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "novelName 不能为空");
            return response;
        }

        if (volumeNumber == null || volumeNumber <= 0) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "volumeNumber 必须是正整数");
            return response;
        }

        // 仅对 unsheathed 小说提供卷信息获取功能
        if (!"unsheathed".equals(novelName)) {
            response.put("success", false);
            response.put("code", 400);
            response.put("message", "该接口只支持 unsheathed 小说");
            return response;
        }

        String volumePath = "data/unsheathed/volume" + volumeNumber + ".json";
        ClassPathResource resource = new ClassPathResource(volumePath);

        if (!resource.exists()) {
            response.put("success", false);
            response.put("code", 404);
            response.put("message", "卷文件不存在: " + volumePath);
            return response;
        }

        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> volumeData = objectMapper.readValue(
                inputStream,
                new TypeReference<Map<String, Object>>() {}
            );

            // 处理章节（移除内容字段）
            Object chaptersObj = volumeData.get("chapters");
            if (chaptersObj instanceof java.util.List) {
                java.util.List<?> fullChapters = (java.util.List<?>) chaptersObj;
                java.util.List<Map<String, Object>> simplifiedChapters = new ArrayList<>();

                for (Object chapter : fullChapters) {
                    if (chapter instanceof Map) {
                        Map<?, ?> fullChapter = (Map<?, ?>) chapter;
                        Map<String, Object> simpleChapter = new HashMap<>();

                        // 只保留章节ID、标题和其他信息，但不包含内容
                        for (Map.Entry<?, ?> chapterEntry : fullChapter.entrySet()) {
                            String keyStr = chapterEntry.getKey().toString();
                            // if (!"content".equalsIgnoreCase(keyStr)) {
                                simpleChapter.put(keyStr, chapterEntry.getValue());
                            // }
                        }

                        simplifiedChapters.add(simpleChapter);
                    }
                }

                volumeData.put("chapters", simplifiedChapters);
            }

            response.put("message", "卷信息获取成功");
            response.put("data", volumeData);
            response.put("success", true);
            response.put("code", 200);
            response.put("volumeNumber", volumeNumber);
            response.put("time", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("code", 500);
            response.put("message", "读取卷信息失败：" + e.getMessage());
            return response;
        }
    }
}