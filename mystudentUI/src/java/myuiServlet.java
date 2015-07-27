/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author diana and paula
 */
@WebServlet(urlPatterns = {"/myuiServlet"})
public class myuiServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
        private static final String bucketName     = "pdsphinxbucket";
        private static final String dbName = "mydb";
        private static final String userName = "pmontojo";
        private static final String password = "pmontojo";
        private static final String hostname = "mydb.cued7orr1q2t.us-west-2.rds.amazonaws.com";
        private static final String port = "3306";
        private static final String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
          port + "/" + dbName + "?user=" + userName + "&password=" + password;
        
            protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");

            String courseName="";

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

              String retrieveCourseName = "select distinct(course_name) from metadata;";

              ResultSet rs =  setupStatement.executeQuery(retrieveCourseName);
            //pasar lista de items al html
           
           try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
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
                        //out.println("<LINK REL='StyleSheet' HREF='<%=request.getContextPath()%>/util/CSS/Style.css' TYPE='text/css'>"); 
                        out.println("<body>");
                        out.println("<div class=\"container-narrow\">\n" +
                        "       <img id = \"logo\" src=\"http://opennebula.org/wp-content/uploads/2015/05/IIT_Logo_stack_186_blk.png\">\n" +
                        "       </div>");
              while(rs.next()){
                  
                  String course = rs.getString("course_name");
                  out.println("<div class = 'jumbotron'><a href='http://localhost:8080/mystudentUI/StartServlet?param="+course+"'>"+course+"</a><br></div>");

              }
            

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
