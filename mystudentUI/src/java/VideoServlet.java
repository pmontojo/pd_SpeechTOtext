/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.STSSessionCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author diana and paula
 */
@WebServlet(urlPatterns = {"/VideoServlet"})
public class VideoServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
        static AmazonDynamoDBClient dynamoDBClient;
        public static String TABLENAME = "finalTables";
        private static final String bucketName     = "pdsphinxbucket";
        private static final String dbName = "mydb";
        private static final String userName = "pmontojo";
        private static final String password = "pmontojo";
        private static final String hostname = "mydb.cued7orr1q2t.us-west-2.rds.amazonaws.com";
        private static final String port = "3306";
        private static final String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
          port + "/" + dbName + "?user=" + userName + "&password=" + password;
        
       // private static String[]hola;
            protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");

            String url = request.getParameter("action");

            System.out.println("url"+ url);

            Connection conn = null;
            Statement setupStatement;
            Statement readStatement = null;
            ResultSet resultSet = null;
            String results = "";
            int numresults = 0;
            String statement = null;
            List<Video> result = new ArrayList<>();
            
            try{

              // Create connection to RDS instance
              conn = DriverManager.getConnection(jdbcUrl);
              setupStatement = conn.createStatement();
 
              String retrieveVideoName = "select id from metadata where url like '"+url+"%';";

              ResultSet rs =  setupStatement.executeQuery(retrieveVideoName);
              rs.next();
              String ids = rs.getString(1);
              String retrieveUrl = "select url from metadata where id ="+ids+";";
              ResultSet ru = setupStatement.executeQuery(retrieveUrl);
              ru.next();
              String urls = ru.getString(1);
              System.out.println("este es el id:"+ ids);
              System.out.println("esta es la url:"+ urls);
              String[] urlIdeal = urls.split(".mp4");
              String finalUrl = urlIdeal[0] + ".mp4";
              System.out.println(finalUrl);
 
              
   AWSCredentials credentials = new PropertiesCredentials(new File("/Users/diana/Desktop/AwsCredentials.properties"));

        dynamoDBClient = new AmazonDynamoDBClient(new STSSessionCredentialsProvider(credentials));
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        String s = getItem(String.valueOf(ids));
        System.out.println(s);
        String[]textArray = s.split("\\{");
        

        String textFinal = textArray[3];
        String[]newTextArray = textFinal.split(",}");
        String textFinalFinal = newTextArray[0];
        String[]last = textFinalFinal.split("S:");

        System.out.println(last[1].trim());

           
           try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Mp4Servlet</title>");
            out.println(" <script src=\"jquery-1.11.3.min.js\"></script>\n" +
            "           <script type=\"text/javascript\" src=\"../libs/base64.js\"></script>\n" +
            "   <script type=\"text/javascript\" src=\"../libs/sprintf.js\"></script>\n" +
            "   <script type=\"text/javascript\" src=\"../jspdf.js\"></script>");
                          out.println(" <style type=\"text/css\">\n" +
            "      body {\n" +
            "        padding-top: 20px;\n" +
            "        padding-bottom: 40px;\n" +
            "      }\n" +
            "\n" +
            "        table{\n" +
            "        display: table;\n" +
            "        border:black 5px solid;\n" +
            "        border-collapse: separate;\n" +
            "        border-spacing: 7px;\n" +
            "        border-color: gray;\n" +
            "        padding:2px;\n" +
            "\n" +
            "      }\n" +
            "      \n" +
            "      #logo{\n" +
            "          height: 130px;\n" +
            "          width: 728px;\n" +
            "      }\n" +
            "      hr{\n" +
            "        /*border-color:#357EC7;*/\n" +
            "        border-color:#B6B6B4;\n" +
            "      }\n" +
            "\n" +
            "      /* Custom container */\n" +
            "      .container-narrow {\n" +
            "        margin: 0 auto;\n" +
            "        max-width: 700px;\n" +
            "      }\n" +
            "      .container-narrow > hr {\n" +
            "        margin: 30px 0;\n" +
            "      }\n" +
            "\n" +
            "      /* Main marketing messages */\n" +
            "      .jumbotron {\n" +
            "        margin: 60px 0;\n" +
            "        text-align: center;\n" +
            "        color: grey;\n" +
            "      }\n" +
            "      .jumbotron h1 {\n" +
            "        font-size: 72px;\n" +
            "        line-height: 1;\n" +
            "\n" +
            "      }\n" +
            "      .jumbotron .btn {\n" +
            "        font-size: 21px;\n" +
            "        padding: 14px 24px;\n" +
            "      }\n" +
            "\n" +
            "      /* Supporting marketing content */\n" +
            "      .marketing {\n" +
            "        margin: 60px 0;\n" +
            "      }\n" +
            "      .marketing p + h4 {\n" +
            "        margin-top: 28px;\n" +
            "      }\n" +
            "      \n" +
            "      \n" +
            "      </style>\n" +
            "      \n" +
            "\n" +
            "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\">\n" +
            "");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("<div class=\"container-narrow\">\n" +
            "       <img id = \"logo\" src=\"http://opennebula.org/wp-content/uploads/2015/05/IIT_Logo_stack_186_blk.png\">\n" +
            "       </div>");
                        out.println("<div class='jumbotron'></div>");
                        out.println("<div class='container-narrow'> <video id='idtry'  controls preload='auto' width='500' height='200'></div>\n" +
            "\n" +
            "   \n" +
            "      </video>");
                        out.println("<div class='container-narrow'><br><button id ='button' type='button' class='btn btn-primary btn-lg' onclick='getText()'>EDIT</button></div>\n" +
            "      <div>\n" +
            "            <div class='container-narrow'><form id='usrform'><textarea id='text' class = 'container-narrow' name='comment' style='width: 400px; height: 200px;' form='usrform'></textarea>\n" +
            "           <br><button id ='button2' type='button' onclick='submitToDynamo()'>Submit changes</button>\n" +
            "            </form></div>\n" +
            "       </div>");

                        out.println("<script>    \n" +
            "    var textA = "+"\""+last[1].trim()+"\""+";\n" +
            "\n" +
            "    document.getElementById('usrform').style.visibility = 'hidden';\n" +
            "    \n" +
            "    function getText() {\n" +
            "\n" +
            "       var t = document.createTextNode(textA);\n" +
            "       document.getElementById('text').appendChild(t);\n" +
            "       document.getElementById('button').style.visibility = 'hidden';\n" +
            "       document.getElementById('usrform').style.visibility = 'visible';\n" +
            "\n" +
            "    }");

                        out.println("  var x;\n" +
            "    function submitToDynamo() {\n" +
            "        \n" +
            "       document.getElementById('usrform').style.visibility = 'hidden';\n" +
            "       document.getElementById('button').style.visibility = 'visible';\n" +
            "        x = document.getElementById('text').value;\n" +
            "      download(f,x);\n" +
            "    }\n" +
            "       ");

                        out.println("  function download(filename, text) {\n" +
            "    var pom = document.createElement('a');\n" +
            "    pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));\n" +
            "    pom.setAttribute('download', filename);\n" +
            "\n" +
            "    if (document.createEvent) {\n" +
            "        var event = document.createEvent('MouseEvents');\n" +
            "        event.initEvent('click', true, true);\n" +
            "        pom.dispatchEvent(event);\n" +
            "    }\n" +
            "    else {\n" +
            "        pom.click();\n" +
            "    }\n" +
            "}\n" +
            "    \n" +
            "    \n" +
            "var f = '/Users/diana/Desktop/texto.txt';");
                        out.println("var code = '<source src=\"" +finalUrl+ "\" type=\"video/mp4\" />';\n" +
            "    document.getElementById(\"idtry\").innerHTML=code;");

                        out.println("</script>");
                        out.println("<div>");
        
              
           
            out.println("</div><br>");
            out.println("<a href='http://localhost:8080/studentUI/Uiservlet'>BACK TO LIST </a>"); 

            out.println("</body>");
            out.println("</html>");
        }
  
            } catch (SQLException ex) {
              // handle any errors
              System.out.println("SQLException: " + ex.getMessage());
              System.out.println("SQLState: " + ex.getSQLState());
              System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
              System.out.println("Closing the connection.");
              if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
            }

 
    }
            
     private static String getItem(String keyVal) {
        
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("ID", new AttributeValue(keyVal));
        
        GetItemRequest getItemRequest = new GetItemRequest()
            .withTableName(TABLENAME)
            .withKey(key);
        
        GetItemResult item = dynamoDBClient.getItem(getItemRequest);
        
        System.out.println("Get Result: " + item);
        return item.toString();
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
