package com.example.lz_java_test.dto;

public class NovelRequest {

    /**
     * 小说名称
     */
    private String novelName;

    /**
     * 文件夹名称（可为空）
     */
    private String folderName;

    // --- getter / setter ---

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
