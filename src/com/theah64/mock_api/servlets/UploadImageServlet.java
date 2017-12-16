package com.theah64.mock_api.servlets;


import com.theah64.mock_api.utils.CommonUtils;
import com.theah64.mock_api.utils.TinifyUtils;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.utils.FilePart;
import com.theah64.webengine.utils.Request;
import com.theah64.webengine.utils.Response;
import com.theah64.webengine.utils.WebEngineConfig;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/upload_image"})
@MultipartConfig
public class UploadImageServlet extends AdvancedBaseServlet {


    public static final String KEY_IMAGE = "image";
    private static final int MAX_FILE_SIZE_IN_KB = 2000;


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

                        /*final int fileSizeInKb = filePart.getInputStream().available() / 1024;
                        if (fileSizeInKb > MAX_FILE_SIZE_IN_KB) {
                            throw new Request.RequestException("File size should be less than " + MAX_FILE_SIZE_IN_KB + "kb");
                        }*/

                        final File dataDir = CommonUtils.getDataDir();
                        final File imageFile = new File(dataDir.getAbsolutePath() + File.separator + fp.getRandomFileName());

                        final InputStream is = filePart.getInputStream();
                        final FileOutputStream fos = new FileOutputStream(imageFile);
                        int read = 0;
                        final byte[] buffer = new byte[1024];

                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }

                        fos.flush();
                        fos.close();
                        is.close();

                        imageFile.setReadable(true, false);
                        imageFile.setExecutable(true, false);
                        imageFile.setWritable(true, false);

                        String fileDownloadPath = imageFile.getAbsolutePath().split("/html")[1];
                        String imageUrl = (
                                WebEngineConfig.getBaseURL().startsWith("http://localhost") ? "http://localhost:8090" : "http://theapache64.com:8090")
                                + fileDownloadPath;

                        try {
                            final String id = TinifyUtils.manage(getHeaderSecurity().getProjectId(), imageUrl, imageUrl, imageFile.getAbsolutePath(), true);

                            final JSONObject joImage = new JSONObject();
                            joImage.put("id", id);
                            joImage.put("image_url", imageUrl);

                            getWriter().write(new Response("File uploaded", joImage).getResponse());

                        } catch (QueryBuilderException | SQLException e) {
                            e.printStackTrace();
                            throw new Request.RequestException(e.getMessage());
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
