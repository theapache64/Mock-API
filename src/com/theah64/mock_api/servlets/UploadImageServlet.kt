package com.theah64.mock_api.servlets


import com.theah64.mock_api.utils.CommonUtils
import com.theah64.mock_api.utils.TinifyUtils
import com.theah64.webengine.database.querybuilders.QueryBuilderException
import com.theah64.webengine.utils.FilePart
import com.theah64.webengine.utils.Request
import com.theah64.webengine.utils.Response
import com.theah64.webengine.utils.WebEngineConfig
import org.json.JSONException
import org.json.JSONObject

import javax.imageio.ImageIO
import javax.servlet.annotation.MultipartConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.SQLException

@WebServlet(urlPatterns = ["/v1/upload_image"])
@MultipartConfig
class UploadImageServlet : AdvancedBaseServlet() {


    override val isSecureServlet: Boolean
        get() = true

    override val requiredParameters: Array<String>?
        get() = arrayOf()


    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        AdvancedBaseServlet.Companion.setGETMethodNotSupported(resp)
    }

    @Throws(IOException::class, JSONException::class, Request.RequestException::class)
    override fun doAdvancedPost() {

        try {
            //Yes,it's a valid data type
            val dataFilePart = httpServletRequest!!.getPart(KEY_IMAGE)

            if (dataFilePart != null) {

                //Saving file
                val filePart = httpServletRequest!!.getPart(KEY_IMAGE)

                if (filePart != null) {

                    val fp = FilePart(filePart!!)
                    val ext = fp.fileExtensionFromContentType
                    if (ext == FilePart.FILE_EXTENSION_JPG || ext == FilePart.FILE_EXTENSION_PNG) {

                        //Double check if it's an image
                        val image = ImageIO.read(filePart!!.getInputStream())
                                ?: throw Request.RequestException("Invalid image : double check")

                        /*final int fileSizeInKb = filePart.getInputStream().available() / 1024;
                        if (fileSizeInKb > MAX_FILE_SIZE_IN_KB) {
                            throw new Request.RequestException("File size should be less than " + MAX_FILE_SIZE_IN_KB + "kb");
                        }*/

                        val dataDir = CommonUtils.getDataDir()
                        val imageFile = File(dataDir.absolutePath + File.separator + fp.randomFileName)

                        val inputStream = filePart!!.getInputStream()
                        val fos = FileOutputStream(imageFile)
                        var read = 0
                        val buffer = ByteArray(1024)

                        do {
                            read = inputStream.read(buffer)
                            if (read != -1) {
                                fos.write(buffer, 0, read)
                            } else {
                                break
                            }
                        } while (true)


                        fos.flush()
                        fos.close()
                        inputStream.close()

                        imageFile.setReadable(true, false)
                        imageFile.setExecutable(true, false)
                        imageFile.setWritable(true, false)

                        val fileDownloadPath = imageFile.absolutePath.split("/html".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                        val imageUrl = (if (WebEngineConfig.baseURL!!.startsWith("http://localhost")) "http://localhost:8090" else "http://theapache64.com:8090") + fileDownloadPath

                        try {
                            val id = TinifyUtils.manage(headerSecurity!!.projectId, imageUrl, imageUrl, imageFile.absolutePath, true)

                            val joImage = JSONObject()
                            joImage.put("id", id)
                            joImage.put("image_url", imageUrl)

                            writer!!.write(Response("File uploaded", joImage).response)

                        } catch (e: QueryBuilderException) {
                            e.printStackTrace()
                            throw Request.RequestException(e.message)
                        } catch (e: SQLException) {
                            e.printStackTrace()
                            throw Request.RequestException(e.message)
                        }


                    } else {
                        throw Request.RequestException("Invalid image type: " + filePart.getContentType() + ":" + ext)
                    }

                }

            } else {
                throw Request.RequestException("image missing from request")
            }
        } catch (e: javax.servlet.ServletException) {
            e.printStackTrace()
            throw Request.RequestException(e.message)
        }

    }

    companion object {


        const val KEY_IMAGE = "image"
        private val MAX_FILE_SIZE_IN_KB = 2000
    }


}
