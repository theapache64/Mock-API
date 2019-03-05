package com.theah64.mock_api.utils

import com.theah64.mock_api.database.Images
import com.theah64.mock_api.database.TinifyKeys
import com.theah64.mock_api.models.Image
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.exceptions.MailException
import com.theah64.webengine.utils.MailHelper
import com.theah64.webengine.utils.Request
import com.theah64.webengine.utils.WebEngineConfig
import com.tinify.AccountException
import com.tinify.Tinify

import java.io.File
import java.sql.SQLException

/**
 * Created by theapache64 on 12/12/17.
 */
object TinifyUtils {

    @Throws(QueryBuilderException::class, SQLException::class)
    fun manage(projectId: String, imageUrl: String, thumbUrl: String, filePath: String, isCompress: Boolean): String {

        val tinifyTable = TinifyKeys.instance
        val tinifyKey = tinifyTable.leastUsedKey
        Tinify.setKey(tinifyKey!!.key)

        //Adding to db
        val image1 = Image(null, projectId, tinifyKey.id!!, imageUrl, thumbUrl, filePath, false)
        val imagesTable = Images.instance
        val id = imagesTable.addv3(image1)
        image1.id = id

        Thread {


            try {


                //Compressing in another thread
                if (isCompress) {

                    try {

                        val tempCmpPath = filePath + "_cmp"
                        //Tinify.fromUrl("https://images-na.ssl-images-amazon.com/images/I/91-k8Ex-KCL._RI_SX200_.jpg").toFile(tempCmpPath);
                        Tinify.fromUrl(imageUrl).toFile(tempCmpPath)

                        val cmpFile = File(tempCmpPath)

                        val imageFile = File(filePath)

                        if (!cmpFile.renameTo(imageFile)) {
                            throw Request.RequestException("Failed to replace compressed file with original")
                        } else {
                            imageFile.setReadable(true, false)
                            imageFile.setExecutable(true, false)
                            imageFile.setWritable(true, false)
                        }

                        //Checking if image still exists in database
                        val isImgExistInDb = imagesTable.get(Images.COLUMN_ID, image1.id!!, Images.COLUMN_ID, false) != null
                        if (isImgExistInDb) {

                            //Changing image url if it's from external website
                            if (!imageUrl.startsWith(WebEngineConfig.baseURL!!)) {


                                val fileDownloadPath = imageFile.absolutePath.split("/html".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                val newImageUrl = (if (WebEngineConfig.baseURL!!.startsWith("http://localhost")) "http://localhost:8090" else "http://theapache64.com:8090") + fileDownloadPath
                                image1.imageUrl = newImageUrl
                                image1.isCompressed = true
                            }

                            //Setting compression finished
                            imagesTable.update(image1)
                        } else {
                            //Delete the file

                            imageFile.delete()
                        }


                    } catch (e: Exception) {

                        e.printStackTrace()

                        if (e is AccountException) {
                            //Disabling key
                            tinifyTable.update(TinifyKeys.COLUMN_ID, tinifyKey.id, TinifyKeys.COLUMN_IS_ACTIVE, TinifyKeys.FALSE)
                        }


                        throw Request.RequestException("Compression failed, Please try again: " + e.message)
                    } catch (e: QueryBuilderException) {
                        e.printStackTrace()
                        throw Request.RequestException("Compression failed, Please try again: " + e.message)
                    }

                    //Update usage
                    tinifyTable.update(TinifyKeys.COLUMN_KEY, Tinify.key(), TinifyKeys.COLUMN_USAGE, Tinify.compressionCount().toString())
                }

            } catch (e: Request.RequestException) {
                e.printStackTrace()

                try {
                    MailHelper.sendMail("theapache64@gmail.com", "Compression failed", e.message + tinifyKey!!, "Mock-API")
                } catch (e1: MailException) {
                    e1.printStackTrace()
                }

            } catch (e: SQLException) {
                e.printStackTrace()
                try {
                    MailHelper.sendMail("theapache64@gmail.com", "Compression failed", e.message + tinifyKey!!, "Mock-API")
                } catch (e1: MailException) {
                    e1.printStackTrace()
                }

            }
        }.start()


        return id
    }
}
