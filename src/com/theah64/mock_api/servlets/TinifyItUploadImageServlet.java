package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.TinifyKeys;
import com.theah64.mock_api.models.TinifyKey;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.FilePart;
import com.theah64.webengine.utils.Request;
import com.tinify.Tinify;
import org.json.JSONException;

import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/tinify_it_upload_image"})
@MultipartConfig
public class TinifyItUploadImageServlet extends AdvancedBaseServlet {


    private static final String KEY_IMAGE = "image";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setGETMethodNotSupported(resp);
    }


    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{

        };

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    protected void doAdvancedPost() throws IOException, JSONException, Request.RequestException {

        try {
            //Yes,it's a valid data type
            final Part dataFilePart = getHttpServletRequest().getPart(KEY_IMAGE);

            if (dataFilePart != null) {

                //Saving file
                final Part filePart = getHttpServletRequest().getPart(KEY_IMAGE);

                if (filePart != null) {

                    FilePart fp = new FilePart(filePart);
                    final String ext = fp.getFileExtensionFromContentType();
                    if (ext.equals(FilePart.FILE_EXTENSION_JPG) || ext.equals(FilePart.FILE_EXTENSION_PNG)) {

                        //Double check if it's an image
                        BufferedImage image = ImageIO.read(filePart.getInputStream());
                        if (image == null) {
                            throw new Request.RequestException("Invalid image : double check");
                        }

                        /*
                        final int fileSizeInKb = filePart.getInputStream().available() / 1024;
                        if (fileSizeInKb > MAX_FILE_SIZE_IN_KB) {
                            throw new Request.RequestException("File size should be less than " + MAX_FILE_SIZE_IN_KB + "kb");
                        }
                        */

                        try {
                            final TinifyKey tinifyKey = TinifyKeys.getInstance().getLeastUsedKey();

                            Tinify.setKey(tinifyKey.getKey());
                            Tinify.fromBuffer(filePart.g)

                        } catch (QueryBuilderException | SQLException e) {
                            e.printStackTrace();
                        }


                    } else {
                        throw new Request.RequestException("Invalid image type: " + filePart.getContentType() + ":" + ext);
                    }

                }

            } else {
                throw new Request.RequestException("image missing from request");
            }
        } catch (javax.servlet.ServletException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }

    }


}
