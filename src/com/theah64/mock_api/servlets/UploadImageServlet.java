package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.Images;
import com.theah64.mock_api.database.TinifyKeys;
import com.theah64.mock_api.models.Image;
import com.theah64.mock_api.models.TinifyKey;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.exceptions.MailException;
import com.theah64.webengine.utils.*;
import com.tinify.AccountException;
import com.tinify.Tinify;
import org.json.JSONException;

import javax.imageio.ImageIO;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
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

                        String fileDownloadPath = imageFile.getAbsolutePath().split("/html")[1];
                        String downloadLink = (WebEngineConfig.getBaseURL().contains("http://localhost") ? "http://localhost:8090" : "http://theapache64.com:8090") + fileDownloadPath;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                TinifyKey tinifyKey = null;
                                try {

                                    TinifyKeys tinifyTable = TinifyKeys.getInstance();
                                    tinifyKey = tinifyTable.getLeastUsedKey();
                                    Tinify.setKey(tinifyKey.getKey());

                                    //Compressing in another thread
                                    try {
                                        System.out.println("Download link : " + downloadLink);
                                        //Tinify.fromUrl("https://images-na.ssl-images-amazon.com/images/I/91-k8Ex-KCL._RI_SX200_.jpg").toFile(imageFile.getAbsolutePath());
                                        Tinify.fromUrl(downloadLink).toFile(imageFile.getAbsolutePath());
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                        if (e instanceof AccountException) {
                                            //Disabling key
                                            tinifyTable.update(TinifyKeys.COLUMN_ID, tinifyKey.getId(), TinifyKeys.COLUMN_IS_ACTIVE, TinifyKeys.FALSE);
                                        }


                                        throw new Request.RequestException("Compression failed, Please try again: " + e.getMessage());
                                    }

                                    //Update usage
                                    tinifyTable.update(TinifyKeys.COLUMN_KEY, Tinify.key(), TinifyKeys.COLUMN_USAGE, String.valueOf(Tinify.compressionCount()));

                                    //Adding to db
                                    Images.getInstance().add(new Image(null, getHeaderSecurity().getProjectId(), tinifyKey.getId(), downloadLink, downloadLink, imageFile.getAbsolutePath()));

                                } catch (QueryBuilderException | Request.RequestException | SQLException e) {
                                    e.printStackTrace();

                                    try {
                                        MailHelper.sendMail("theapache64@gmail.com", "Compression failed", e.getMessage() + tinifyKey, "Mock-API");
                                    } catch (MailException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }).start();

                        getWriter().write(new Response("File uploaded", "download_link", downloadLink).getResponse());

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

    private byte[] getByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }


}
