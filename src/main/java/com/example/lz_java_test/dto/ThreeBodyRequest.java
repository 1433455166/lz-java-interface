package com.example.lz_java_test.dto;

public class ThreeBodyRequest {

    /**
     * 第几部（三体1 / 三体2 / 三体3）
     */
    private Integer part;

    // --- 一定要有 getter / setter ---
    public Integer getPart() {
        return part;
    }

    public void setPart(Integer part) {
        this.part = part;
    }
}

