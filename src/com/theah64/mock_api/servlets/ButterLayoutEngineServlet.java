package com.theah64.mock_api.servlets;

import com.theah64.mock_api.exceptions.RequestException;
import com.theah64.mock_api.utils.APIResponse;
import com.theah64.mock_api.utils.PathInfo;
import com.theah64.mock_api.utils.Request;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

/**
 * Created by theapache64 on 1/11/17.
 */
@WebServlet(urlPatterns = "/butter_layout_engine")
public class ButterLayoutEngineServlet extends AdvancedBaseServlet {

    private static final String KEY_XML_DATA = "xml_data";
    private static final String KEY_R_SERIES = "r_series";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected String[] getRequiredParameters() throws Request.RequestException {
        return new String[]{KEY_XML_DATA, KEY_R_SERIES};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, IOException, JSONException, SQLException, RequestException, PathInfo.PathInfoException {

        final String xmlData = getStringParameter(KEY_XML_DATA);
        final String rSeries = getStringParameter(KEY_R_SERIES);
        System.out.println("Data reached+ " + xmlData);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));
            //Document doc = dBuilder.parse(new File("/home/theapache64/Documents/projects/cybaze/staynodes/android/lakkidi_village/staynodes/src/main/res/layout/content_base_room.xml"));
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nodeList = doc.getElementsByTagName("*");

            StringBuilder butterLayoutBuilder = null;

            for (int i = 0; i < nodeList.getLength(); i++) {


                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    // do something with the current element
                    final Node idNode = node.getAttributes().getNamedItem("android:id");
                    if (idNode != null) {

                        if (butterLayoutBuilder == null) {
                            butterLayoutBuilder = new StringBuilder();
                        }

                        final String id = idNode.getNodeValue().split("/")[1];
                        butterLayoutBuilder.append(String.format("@BindView(%s.id.%s)\n", rSeries, id));
                        String nodeName = node.getNodeName();
                        if (nodeName.contains(".")) {
                            final String[] nodeNameChunks = nodeName.split("\\.");
                            nodeName = nodeNameChunks[nodeNameChunks.length - 1];
                        }
                        butterLayoutBuilder.append(String.format("%s %s;\n\n", nodeName, id));

                    }

                }
            }

            if (butterLayoutBuilder == null) {
                throw new RequestException("No view with id found");
            }

            getWriter().write(new APIResponse("OK", "butter_layout", butterLayoutBuilder.toString()).getResponse());

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            throw new Request.RequestException(e.getMessage());
        }
    }


}
