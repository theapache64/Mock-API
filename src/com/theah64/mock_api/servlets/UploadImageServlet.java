package com.theah64.mock_api.servlets;


import com.theah64.mock_api.database.TinifyKeys;
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


    private static final String KEY_IMAGE = "image";
    private static final int MAX_FILE_SIZE_IN_KB = 900;


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
                        BufferedImage image = ImageIO.read(filePart.getInputStream());
                        if (image == null) {
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

                        TinifyKeys tinifyTable = TinifyKeys.getInstance();
                        final TinifyKey tinifyKey = tinifyTable.getLeastUsedKey();
                        Tinify.setKey(tinifyKey.getKey());
                        try {
                            Tinify.fromBuffer(getByteArray(filePart.getInputStream())).toFile(imageFile.getAbsolutePath());
                        } catch (AccountException e) {

                            //Disabling key
                            tinifyTable.update(TinifyKeys.COLUMN_ID, tinifyKey.getId(), TinifyKeys.COLUMN_IS_ACTIVE, TinifyKeys.FALSE);

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //Sending report
                                    try {
                                        MailHelper.sendMail("theapache64@gmail.com", "Tinify key failed", tinifyKey.toString(), "Mock-API");
                                    } catch (MailException e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            }).start();

                            throw new Request.RequestException("Compression failed, Please try again");
                        }

                        //Update usage
                        tinifyTable.update(TinifyKeys.COLUMN_KEY, Tinify.key(), TinifyKeys.COLUMN_USAGE, String.valueOf(Tinify.compressionCount()));

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
        } catch (javax.servlet.ServletException | SQLException | QueryBuilderException e) {
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
