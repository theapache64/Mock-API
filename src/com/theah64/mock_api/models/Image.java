package com.theah64.mock_api.models;

public class Image {
    private final String id, projectId, tinifyKeyId, imageUrl, thumbUrl, filePath;

    public Image(String id, String projectId, String tinifyKeyId, String imageUrl, String thumbUrl, String filePath) {
        this.id = id;
        this.projectId = projectId;
        this.tinifyKeyId = tinifyKeyId;
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
        this.filePath = filePath;
    }

    public String getId() {
        return id;
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

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getFilePath() {
        return filePath;
    }
}
