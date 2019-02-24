package com.theah64.mock_api.servlets


import com.theah64.mock_api.database.TinifyKeys
import com.theah64.mock_api.models.TinifyKey
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.FilePart
import com.theah64.webengine.utils.Request
import com.tinify.Tinify
import org.apache.commons.io.IOUtils

import javax.imageio.ImageIO
import javax.servlet.ServletException
import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.Part
import java.awt.image.BufferedImage
import java.io.IOException
import java.sql.SQLException

@WebServlet(urlPatterns = ["/v1/tinify_it_upload_image"])
@MultipartConfig
class TinifyItUploadImageServlet : HttpServlet() {

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {

        try {
            //Yes, It's a valid data type
            val dataFilePart = req.getPart(KEY_IMAGE)

            if (dataFilePart != null) {

                //Saving file
                val filePart = req.getPart(KEY_IMAGE)

                if (filePart != null) {

                    val fp = FilePart(filePart)
                    val ext = fp.fileExtensionFromContentType
                    //if (ext.equals(FilePart.FILE_EXTENSION_JPG) || ext.equals(FilePart.FILE_EXTENSION_PNG)) {

                    //Double check if it's an image
                    val image = ImageIO.read(filePart.inputStream)
                            ?: throw Request.RequestException("Invalid image : double check")

                    /*
                        final int fileSizeInKb = filePart.getInputStream().available() / 1024;
                        if (fileSizeInKb > MAX_FILE_SIZE_IN_KB) {
                            throw new Request.RequestException("File size should be less than " + MAX_FILE_SIZE_IN_KB + "kb");
                        }
                        */

                    try {
                        val tinifyKey = TinifyKeys.INSTANCE.leastUsedKey!!
                        Tinify.setKey(tinifyKey.key)
                        val result = Tinify.fromBuffer(IOUtils.toByteArray(filePart.inputStream)).toBuffer()
                        resp.contentType = filePart.contentType
                        resp.outputStream.write(result)

                        TinifyKeys.INSTANCE.update(TinifyKeys.COLUMN_KEY, Tinify.key(), TinifyKeys.COLUMN_USAGE, Tinify.compressionCount().toString())
                    } catch (e: QueryBuilderException) {
                        e.printStackTrace()
                        throw Request.RequestException(e.message)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                        throw Request.RequestException(e.message)
                    }


                    /*} else {
                        throw new Request.RequestException("Invalid image type: " + filePart.getContentType() + ":" + ext);
                    }*/

                }

            } else {
                throw Request.RequestException("image missing from request")
            }
        } catch (e: ServletException) {
            e.printStackTrace()
            throw IOException(e.message)
        } catch (e: Request.RequestException) {
            e.printStackTrace()
            throw IOException(e.message)
        }

    }

    companion object {


        private val KEY_IMAGE = "image"
    }
}
