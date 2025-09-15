package com.example.lz_java_test.model;

public class Trisomy {
    private Long id;
    private String name;
    private String email;
    private Integer age;

    // 空构造函数（JSON 反序列化需要）
    public Trisomy() {
    }

    // 全参构造函数
    public Trisomy(Long id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
