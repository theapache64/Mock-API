package com.theah64.mock_api.servlets;

import com.theah64.mock_api.database.Images;
import com.theah64.mock_api.models.Image;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.ListUtils;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by theapache64 on 16/12/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_project_images"})
public class GetProjectImagesServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[0];
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException, QueryBuilderException {

        List<Image> images = Images.getInstance().getAll(Images.COLUMN_PROJECT_ID, getHeaderSecurity().getProjectId());

        JSONObject joData = new JSONObject();
        joData.put("images", new ListUtils<Image>() {
            @Override
            protected JSONObject getJSONObject(Image item) throws JSONException {
                final JSONObject jo = new JSONObject();
                jo.put("id", item.getId());
                jo.put("image_url", item.getImageUrl());
                jo.put("thumb_url", item.getThumbUrl());
                return jo;
            }
        }.toJSONArray(images));
        getWriter().write(new APIResponse(images.size() + " image(s) found", joData).getResponse());

    }
}
