package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.Projects;
import com.theah64.webengine.utils.*;
import org.json.JSONException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/upload_image"})
@MultipartConfig
public class UploadImageServlet extends AdvancedBaseServlet {


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

                String fileDownloadPath = null;
                if (filePart != null) {

                    FilePart fp = new FilePart(filePart);
                    final String ext = fp.getFileExtensionFromContentType();
                    if (ext.equals(FilePart.FILE_EXTENSION_JPG) || ext.equals(FilePart.FILE_EXTENSION_PNG)) {

                        //Double check if it's an image
                        if (ImageIO.read(filePart.getInputStream()) == null) {
                            throw new Request.RequestException("Invalid image : double check");
                        }

                        final File dataDir = new File("/var/www/html/mock_api_data");

                        if (!dataDir.exists()) {
                            dataDir.mkdirs();
                            dataDir.setReadable(true, false);
                            dataDir.setExecutable(true, false);
                            dataDir.setWritable(true, false);
                        }


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

                        fileDownloadPath = imageFile.getAbsolutePath().split("/html")[1];
                        getWriter().write(new Response("File uploaded", "download_link", WebEngineConfig.getBaseURL() + fileDownloadPath).getResponse());

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
