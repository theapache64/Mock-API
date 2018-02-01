package com.theah64.mock_api.utils;

import com.theah64.mock_api.database.Images;
import com.theah64.mock_api.database.TinifyKeys;
import com.theah64.mock_api.models.Image;
import com.theah64.mock_api.models.TinifyKey;
import com.theah64.webengine.database.querybuilders.QueryBuilderException;
import com.theah64.webengine.exceptions.MailException;
import com.theah64.webengine.utils.MailHelper;
import com.theah64.webengine.utils.Request;
import com.theah64.webengine.utils.WebEngineConfig;
import com.tinify.AccountException;
import com.tinify.Tinify;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by theapache64 on 12/12/17.
 */
public class TinifyUtils {

    public static String manage(final String projectId, final String imageUrl, String thumbUrl, String filePath, boolean isCompress) throws QueryBuilderException, SQLException {

        TinifyKeys tinifyTable = TinifyKeys.getInstance();
        final TinifyKey tinifyKey = tinifyTable.getLeastUsedKey();
        Tinify.setKey(tinifyKey.getKey());

        //Adding to db
        final Image image1 = new Image(null, projectId, tinifyKey.getId(), imageUrl, thumbUrl, filePath, false);
        Images imagesTable = Images.getInstance();
        final String id = imagesTable.addv3(image1);
        image1.setId(id);


        new Thread(() -> {


            try {


                //Compressing in another thread
                if (isCompress) {

                    try {

                        final String tempCmpPath = filePath + "_cmp";
                        //Tinify.fromUrl("https://images-na.ssl-images-amazon.com/images/I/91-k8Ex-KCL._RI_SX200_.jpg").toFile(tempCmpPath);
                        Tinify.fromUrl(imageUrl).toFile(tempCmpPath);

                        File cmpFile = new File(tempCmpPath);

                        final File imageFile = new File(filePath);

                        if (!cmpFile.renameTo(imageFile)) {
                            throw new Request.RequestException("Failed to replace compressed file with original");
                        } else {
                            imageFile.setReadable(true, false);
                            imageFile.setExecutable(true, false);
                            imageFile.setWritable(true, false);
                        }

                        //Checking if image still exists in database
                        final boolean isImgExistInDb = imagesTable.get(Images.COLUMN_ID, image1.getId(), Images.COLUMN_ID, false) != null;
                        if (isImgExistInDb) {

                            //Changing image url if it's from external website
                            if (!imageUrl.startsWith(WebEngineConfig.getBaseURL())) {


                                String fileDownloadPath = imageFile.getAbsolutePath().split("/html")[1];
                                String newImageUrl = (
                                        WebEngineConfig.getBaseURL().startsWith("http://localhost") ? "http://localhost:8090" : "http://theapache64.com:8090")
                                        + fileDownloadPath;
                                image1.setImageUrl(newImageUrl);
                                image1.setCompressed(true);
                            }

                            //Setting compression finished
                            imagesTable.update(image1);
                        } else {
                            //Delete the file

                            imageFile.delete();
                        }


                    } catch (Exception | QueryBuilderException e) {

                        e.printStackTrace();

                        if (e instanceof AccountException) {
                            //Disabling key
                            tinifyTable.update(TinifyKeys.COLUMN_ID, tinifyKey.getId(), TinifyKeys.COLUMN_IS_ACTIVE, TinifyKeys.FALSE);
                        }


                        throw new Request.RequestException("Compression failed, Please try again: " + e.getMessage());
                    }

                    //Update usage
                    tinifyTable.update(TinifyKeys.COLUMN_KEY, Tinify.key(), TinifyKeys.COLUMN_USAGE, String.valueOf(Tinify.compressionCount()));
                }

            } catch (Request.RequestException | SQLException e) {
                e.printStackTrace();

                try {
                    MailHelper.sendMail("theapache64@gmail.com", "Compression failed", e.getMessage() + tinifyKey, "Mock-API");
                } catch (MailException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();


        return id;
    }
}
