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

    /**
     * 卷号
     */
    private Integer volumeNumber;
    
    /**
     * 章节ID（可为空，如果为空则返回整本小说）
     */
    private Integer chapterId;

    // --- getter / setter ---

    public String getNovelName() {
        return novelName;
    }

    public Integer getVolumeNumber() {
        return volumeNumber;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public void setVolumeNumber(Integer volumeNumber) {
        this.volumeNumber = volumeNumber;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }
}