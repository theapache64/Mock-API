package com.theah64.mock_api.servlets;

import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.PathInfo;
import com.theah64.webengine.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Created by theapache64 on 12/12/17.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/search_images"})
public class SearchImagesServlet extends AdvancedBaseServlet {

    private static final String KEY_KEYWORD = "keyword";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                KEY_KEYWORD
        };
    }

    @Override
    protected void doAdvancedPost() throws IOException, JSONException, SQLException, Request.RequestException, PathInfo.PathInfoException, QueryBuilderException {
        final String keyword = getStringParameter(KEY_KEYWORD);
        final String url = String.format("http://theapache64.com/gpix/v1/search?keyword=%s&limit=100&Authorization=GoZNYVeK9O", URLEncoder.encode(keyword, "UTF-8"));
        getWriter().write(getResponseFromURL(url));
    }


    private static String getResponseFromURL(final String url) throws IOException {

        final URL theURL = new URL(url);
        final HttpURLConnection urlCon = (HttpURLConnection) theURL.openConnection();
        final BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getResponseCode() == 200 ? urlCon.getInputStream() : urlCon.getErrorStream()));
        final StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }

        br.close();

        return sb.toString();
    }
}
