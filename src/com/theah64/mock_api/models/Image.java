package com.theah64.mock_api.models;

public class Image {
    private final String projectId;
    private final String tinifyKeyId;
    private final String thumbUrl;
    private final String filePath;
    private String id;
    private String imageUrl;
    private boolean isCompressed;

    public Image(String id, String projectId, String tinifyKeyId, String imageUrl, String thumbUrl, String filePath, boolean isCompressed) {
        this.id = id;
        this.projectId = projectId;
        this.tinifyKeyId = tinifyKeyId;
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
        this.filePath = filePath;
        this.isCompressed = isCompressed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getTinifyKeyId() {
        return tinifyKeyId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isCompressed() {
        return isCompressed;
    }

    public void setCompressed(boolean isCompressed) {
        this.isCompressed = isCompressed;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", projectId='" + projectId + '\'' +
                ", tinifyKeyId='" + tinifyKeyId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
