package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Images;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


@WebServlet(urlPatterns = AdvancedBaseServlet.VERSION_CODE + "/delete_image")
public class DeleteProjectImageServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{Images.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException, QueryBuilderException {
        final String imageId = getStringParameter(Images.COLUMN_ID);
        final String projectId = getHeaderSecurity().getProjectId();

        final String filePath = Images.getInstance().get(Images.COLUMN_ID, imageId, Images.COLUMN_PROJECT_ID, projectId, Images.COLUMN_FILE_PATH);
        if (filePath != null) {
            final File file = new File(filePath);
            //noinspection ResultOfMethodCallIgnored
            file.delete();

            //Delete from db also
            Images.getInstance().delete(Images.COLUMN_ID, imageId, Images.COLUMN_PROJECT_ID, projectId);
            getWriter().write(new APIResponse("Image deleted", null).getResponse());

        } else {
            throw new Request.RequestException("Invalid request");
        }
    }
}
