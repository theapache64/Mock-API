package com.theah64.mock_api.models;

/**
 * Created by theapache64 on 11/1/18.
 */
public class DiffView {
    private final String title,oldDataId, newDataId, diffOutputId, newData, oldData;

    public DiffView(String title, String oldDataId, String newDataId, String diffOutputId, String newData, String oldData) {
        this.title = title;
        this.oldDataId = oldDataId;
        this.newDataId = newDataId;
        this.diffOutputId = diffOutputId;
        this.newData = newData;
        this.oldData = oldData;
    }

    public String getTitle() {
        return title;
    }

    public String getOldDataId() {
        return oldDataId;
    }

    public String getNewDataId() {
        return newDataId;
    }

    public String getDiffOutputId() {
        return diffOutputId;
    }

    public String getNewData() {
        return newData;
    }

    public String getOldData() {
        return oldData;
    }
}
