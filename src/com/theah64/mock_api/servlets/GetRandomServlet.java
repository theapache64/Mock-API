package com.theah64.mock_api.servlets;

import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = AdvancedBaseServlet.VERSION_CODE + "/get_random")
public class GetRandomServlet extends AdvancedBaseServlet {

    private static final String KEY_RANDOM_WHAT = "random_what";
    private static final String KEY_COUNT = "count";

    private static final String TYPE_WORDS = "words";
    private static final String TYPE_PARAGRAPHS = "paragraphs";
    private static final String TYPE_NAME = "name";
    private static final String TYPE_PHONE = "phone";
    private static final String TYPE_CITY = "city";
    private static final String TYPE_STATE = "state";
    private static final String TYPE_COUNTRY = "country";
    private static final String TYPE_MALE_NAME = "male_name";
    private static final String TYPE_FEMALE_NAME = "female_name";
    private static final String TYPE_FIRST_NAME = "first_name";
    private static final String TYPE_LAST_NAME = "last_name";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{
                KEY_RANDOM_WHAT,
                KEY_COUNT
        };
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {
        final String randomWhat = getStringParameter(KEY_RANDOM_WHAT);
        final int count = getIntParameter(KEY_COUNT, 1);
        final String output;

        Lorem lorem = LoremIpsum.getInstance();

        switch (randomWhat) {
            case TYPE_WORDS:
                output = capitalize(lorem.getWords(count, count));
                break;
            case TYPE_PARAGRAPHS:
                output = lorem.getParagraphs(count, count);
                break;
            case TYPE_NAME:
                output = lorem.getName();
                break;
            case TYPE_PHONE:
                output = lorem.getPhone();
                break;
            case TYPE_CITY:
                output = lorem.getCity();
                break;
            case TYPE_STATE:
                output = lorem.getStateFull();
                break;
            case TYPE_COUNTRY:
                output = lorem.getCountry();
                break;
            case TYPE_MALE_NAME:
                output = lorem.getNameMale();
                break;
            case TYPE_FEMALE_NAME:
                output = lorem.getNameFemale();
                break;
            case TYPE_FIRST_NAME:
                output = lorem.getFirstName();
                break;
            case TYPE_LAST_NAME:
                output = lorem.getLastName();
                break;
            default:
                throw new RequestException("Invalid randomWhat: " + randomWhat);
        }

        getWriter().write(new APIResponse("Done", "random_output", output).getResponse());
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
