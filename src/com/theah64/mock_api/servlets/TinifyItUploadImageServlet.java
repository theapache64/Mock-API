package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.TinifyKeys;
import com.theah64.mock_api.models.TinifyKey;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.FilePart;
import com.theah64.webengine.utils.Request;
import com.tinify.Tinify;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/tinify_it_upload_image"})
@MultipartConfig
public class TinifyItUploadImageServlet extends HttpServlet {


    private static final String KEY_IMAGE = "image";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            //Yes, It's a valid data type
            final Part dataFilePart = req.getPart(KEY_IMAGE);

            if (dataFilePart != null) {

                //Saving file
                final Part filePart = req.getPart(KEY_IMAGE);

                if (filePart != null) {

                    FilePart fp = new FilePart(filePart);
                    final String ext = fp.getFileExtensionFromContentType();
                    //if (ext.equals(FilePart.FILE_EXTENSION_JPG) || ext.equals(FilePart.FILE_EXTENSION_PNG)) {

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
                        final byte[] result = Tinify.fromBuffer(IOUtils.toByteArray(filePart.getInputStream())).toBuffer();
                        resp.setContentType(filePart.getContentType());
                        resp.getOutputStream().write(result);

                        TinifyKeys.getInstance().update(TinifyKeys.COLUMN_KEY, Tinify.key(), TinifyKeys.COLUMN_USAGE, String.valueOf(Tinify.compressionCount()));
                    } catch (QueryBuilderException | SQLException e) {
                        e.printStackTrace();
                        throw new Request.RequestException(e.getMessage());
                    }


                    /*} else {
                        throw new Request.RequestException("Invalid image type: " + filePart.getContentType() + ":" + ext);
                    }*/

                }

            } else {
                throw new Request.RequestException("image missing from request");
            }
        } catch (ServletException | Request.RequestException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }

    }
}
