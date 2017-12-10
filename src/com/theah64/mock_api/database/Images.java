package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Image;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class Images extends BaseTable<Image> {

    private static final Images instance = new Images();
    public static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_TINIFY_KEY_ID = "tinify_key_id";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_THUMB_URL = "thumb_url";
    public static final String COLUMN_FILE_PATH = "file_path";

    private Images() {
        super("images");
    }

    public static Images getInstance() {
        return instance;
    }

    //id, projectId, tinifyKeyId, imageUrl, thumbUrl;
    @Override
    public boolean add(Image image) throws SQLException, QueryBuilderException {
        return new AddQueryBuilder.Builder(getTableName())
                .add(COLUMN_PROJECT_ID, image.getProjectId())
                .add(COLUMN_TINIFY_KEY_ID, image.getTinifyKeyId())
                .add(COLUMN_IMAGE_URL, image.getImageUrl())
                .add(COLUMN_THUMB_URL, image.getThumbUrl())
                .add(COLUMN_FILE_PATH, image.getFilePath())
                .done();
    }

    @Override
    public List<Image> getAll(String whereColumn, String whereColumnValue) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder<>(
                getTableName(),
                rs -> new Image(
                        rs.getString(COLUMN_ID),
                        rs.getString(COLUMN_PROJECT_ID),
                        rs.getString(COLUMN_TINIFY_KEY_ID),
                        rs.getString(COLUMN_IMAGE_URL),
                        rs.getString(COLUMN_THUMB_URL),
                        rs.getString(COLUMN_FILE_PATH)),
                new String[]{COLUMN_ID, COLUMN_PROJECT_ID, COLUMN_TINIFY_KEY_ID, COLUMN_IMAGE_URL, COLUMN_THUMB_URL,COLUMN_FILE_PATH},
                new String[]{whereColumn}, new String[]{whereColumnValue}, SelectQueryBuilder.UNLIMITED, COLUMN_ID + " DESC"
        ).getAll();
    }
}
