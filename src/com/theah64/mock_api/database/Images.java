package com.theah64.mock_api.database;

import com.theah64.mock_api.models.Image;
import com.theah64.webengine.database.BaseTable;
import com.theah64.webengine.database.querybuilders.AddQueryBuilder;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;

import java.sql.SQLException;

public class Images extends BaseTable<Image> {

    private static final Images instance = new Images();
    private static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_TINIFY_KEY_ID = "tinify_key_id";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_THUMB_URL = "thumb_url";

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
                .done();
    }
}
