package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Image;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.database.querybuilders.SelectQueryBuilder;
import com.theah64.webengine.database.querybuilders.UpdateQueryBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Images extends BaseTable<Image> {

    private static final Images instance = new Images();
    public static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_TINIFY_KEY_ID = "tinify_key_id";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_THUMB_URL = "thumb_url";
    public static final String COLUMN_FILE_PATH = "file_path";
    public static final String COLUMN_IS_COMPRESSED = "is_compressed";

    private Images() {
        super("images");
    }

    public static Images getInstance() {
        return instance;
    }

    //id, projectId, tinifyKeyId, imageUrl, thumbUrl;
    @Override
    public boolean add(Image image) throws SQLException, QueryBuilderException {
        return !addv3(image).equals("-1");
    }

    @Override
    public String addv3(Image image) throws QueryBuilderException, SQLException {
        return String.valueOf(new AddQueryBuilder.Builder(getTableName())
                .add(COLUMN_PROJECT_ID, image.getProjectId())
                .add(COLUMN_TINIFY_KEY_ID, image.getTinifyKeyId())
                .add(COLUMN_IMAGE_URL, image.getImageUrl())
                .add(COLUMN_THUMB_URL, image.getThumbUrl())
                .add(COLUMN_FILE_PATH, image.getFilePath())
                .add(COLUMN_IS_COMPRESSED, image.isCompressed())
                .doneAndReturn());
    }

    @Override
    public List<Image> getAll(String whereColumn, String whereColumnValue) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder<>(
                getTableName(),
                this::getImageFromResultSet,
                new String[]{COLUMN_ID, COLUMN_PROJECT_ID, COLUMN_TINIFY_KEY_ID, COLUMN_IMAGE_URL, COLUMN_THUMB_URL, COLUMN_FILE_PATH, COLUMN_IS_COMPRESSED},
                new String[]{whereColumn}, new String[]{whereColumnValue}, SelectQueryBuilder.UNLIMITED, COLUMN_ID + " DESC"
        ).getAll();
    }

    @Override
    public Image get(String column1, String value1, String column2, String value2) throws QueryBuilderException, SQLException {
        return new SelectQueryBuilder<Image>(
                getTableName(),
                this::getImageFromResultSet,
                new String[]{COLUMN_ID, COLUMN_PROJECT_ID, COLUMN_TINIFY_KEY_ID, COLUMN_IMAGE_URL, COLUMN_THUMB_URL, COLUMN_FILE_PATH, COLUMN_IS_COMPRESSED},
                new String[]{column1, column2}, new String[]{value1, value2}, "1", COLUMN_ID + " DESC"

        ).get();
    }

    private Image getImageFromResultSet(ResultSet rs) throws SQLException {
        return new Image(
                rs.getString(COLUMN_ID),
                rs.getString(COLUMN_PROJECT_ID),
                rs.getString(COLUMN_TINIFY_KEY_ID),
                rs.getString(COLUMN_IMAGE_URL),
                rs.getString(COLUMN_THUMB_URL),
                rs.getString(COLUMN_FILE_PATH),
                rs.getBoolean(COLUMN_IS_COMPRESSED));
    }

    @Override
    public boolean update(Image image) throws SQLException, QueryBuilderException {
        return new UpdateQueryBuilder.Builder(getTableName())
                .set(COLUMN_IMAGE_URL, image.getImageUrl())
                .set(COLUMN_IS_COMPRESSED, image.isCompressed())
                .where(COLUMN_ID, image.getId())
                .build()
                .done();
    }
}
