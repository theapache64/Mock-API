package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Images;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.CommonUtils;
import com.theah64.mock_api.utils.TinifyUtils;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.RandomString;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by theapache64 on 12/12/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/add_to_project_images"})
public class AddToProjectImagesServlet extends AdvancedBaseServlet {

    private static final String KEY_IS_COMPRESS = "is_compress";


    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                Images.COLUMN_THUMB_URL,
                Images.COLUMN_IMAGE_URL,
                KEY_IS_COMPRESS
        };
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException, QueryBuilderException {
        final String thumbUrl = getStringParameter(Images.COLUMN_THUMB_URL);
        final String imageUrl = getStringParameter(Images.COLUMN_IMAGE_URL);
        final boolean isCompress = getBooleanParameter(KEY_IS_COMPRESS);
        final File dataDir = CommonUtils.getDataDir();
        final File imageFile = new File(dataDir.getAbsolutePath() + File.separator + RandomString.get(10));

        final String id = TinifyUtils.manage(getHeaderSecurity().getProjectId(), imageUrl, thumbUrl, imageFile.getAbsolutePath(), isCompress);

        final JSONObject joImage = new JSONObject();
        joImage.put(Images.COLUMN_IMAGE_URL, imageUrl);
        joImage.put(Images.COLUMN_THUMB_URL, thumbUrl);
        joImage.put(Images.COLUMN_ID, id);

        getWriter().write(new APIResponse("OK", joImage).getResponse());
    }
}
