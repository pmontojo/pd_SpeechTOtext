/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import java.util.Map.Entry;


/**
 *
 * @author paulamontojo
 * 
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig  
public class FileUploadServlet extends HttpServlet {
 private final static Logger LOGGER = 
            Logger.getLogger(FileUploadServlet.class.getCanonicalName());
     	private static final String bucketName     = "pdsphinxbucket";
	private static final String keyName        = "paula";
	private static final String uploadFileName = "/Users/paulamontojo/Desktop/output.avi";
        public String num;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
 
 protected void processRequest(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException, Exception {
    response.setContentType("text/html;charset=UTF-8");

    // Create path components to save the file
    final String path = "/Users/paulamontojo/Desktop";
    final String f1 = request.getParameter("file");

    final Part filePart = request.getPart("file");
   // final File holi = request.g
    final String prof = request.getParameter("professor");
    final String course = request.getParameter("course");

    final String fileName = getFileName(filePart);
    System.out.println(fileName);
    
    String n = "";          
    String dbName = "mydb";
    String userName = "pmontojo";
    String password = "pmontojo";
    String hostname = "mydb.cued7orr1q2t.us-west-2.rds.amazonaws.com";
    String port = "3306";
    String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
    port + "/" + dbName + "?user=" + userName + "&password=" + password;
  
    // Load the JDBC Driver
    try {
      System.out.println("Loading driver...");
      Class.forName("com.mysql.jdbc.Driver");
      System.out.println("Driver loaded!");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Cannot find the driver in the classpath!", e);
    }

    Connection conn = null;
    Statement setupStatement = null;
    Statement readStatement = null;
    ResultSet resultSet = null;
    String results = "";
    int numresults = 0;
    String statement = null;

    try {
    System.out.println(jdbcUrl);
    // Create connection to RDS instance
    conn = DriverManager.getConnection(jdbcUrl);
    
    // Create a table and write two rows
    setupStatement = conn.createStatement();
    String insertRow1 = "insert into metadata (professor_name, course_name, video_name) values('"+ prof + "','"+ course + "','" + fileName+"')";

    setupStatement.addBatch(insertRow1);
    setupStatement.executeBatch();
    ResultSet rs = setupStatement.getGeneratedKeys();
    rs.next();

    System.out.println("Metadata inserted " + rs.getString(1));
    String numId = rs.getString(1);
    n = numId;
    setupStatement.close();
    sqs(numId);
    
    } catch (SQLException ex) {
      // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    } finally {
      System.out.println("Closing the connection.");
      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
    }

    try {
      conn = DriverManager.getConnection(jdbcUrl);
      readStatement = conn.createStatement();
      conn.close();

    } catch (SQLException ex) {
    // handle any errors
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    } finally {
      System.out.println("Closing the connection.");
      if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
    }

    OutputStream out = null;
    InputStream filecontent = null;
    final PrintWriter writer = response.getWriter();

    try {
        out = new FileOutputStream(new File(path + File.separator
                + "output.avi"));
        filecontent = filePart.getInputStream();

        int read = 0;
        final byte[] bytes = new byte[1024];

        while ((read = filecontent.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        
        System.out.println(path);
        
        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Speech to text</title>");
        writer.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\">");
        writer.println(
        "       <div class='jumbotron3'><img id = \"logo\" src=\"https://my125.iit.edu/gradportal/css/images/iitlogo.gif\">\n" +
        "       </div><br><div style='margin-left:10px'><a href='http://localhost:8080/mystudentUI/myuiServlet'>BACK TO LIST </a></div> <br>");
        writer.println(" <style type=\"text/css\">\n" +
        "      body {\n" +
        "        padding-top: 20px;\n" +
        "        padding-bottom: 40px;\n" +
        "      }\n" +
        "      #logo{\n" +
        "          height: 80px;\n" +
        "          width: 728px;\n" +
        "        margin-right: 28px;\n" +
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
        "          height: 80px;\n" +
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
        "  .jumbotron3 {\n" +
        "        text-align: center;\n" +
        "        color: black;\n" +
        "height:110px;\n"+
        "        background-color: black;\n" +
        "margin-left:0px;"+
        "margin-right:0px;"+
        "      }" +
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
        "      </style>\n");
        
        writer.println("</head>");
        writer.println("<body>");
        writer.println("<div class='jumbotron'><h2>Your file is being processed.</h2><h2>Please be patient, this process may take some minutes.</h2></div>");
        writer.println("</body>");
        writer.println("</html>");
        LOGGER.log(Level.INFO, "File{0}being uploaded to {1}", 
                new Object[]{fileName, path});
        putInBucket(fileName,n);
        //sqs(n);
        String s = getUrl(fileName);
        System.out.println("this is url :" + s);
    } catch (FileNotFoundException fne) {
        writer.println("You either did not specify a file to upload or are "
                + "trying to upload a file to a protected or nonexistent "
                + "location.");
        writer.println("<br/> ERROR: " + fne.getMessage());

        LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", 
                new Object[]{fne.getMessage()});
    } finally {
        if (out != null) {
            out.close();
        }
        if (filecontent != null) {
            filecontent.close();
        }
        if (writer != null) {
            writer.close();
        }
    }
 }
 
 private void putInBucket(String fileName, String id) throws IOException{

     
        AWSCredentials awsCreds = new PropertiesCredentials(new File("/Users/paulamontojo/Desktop/AwsCredentials.properties"));
        AmazonS3 s3client = new AmazonS3Client(awsCreds);  

        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            s3client.putObject(new PutObjectRequest(
            		                 bucketName, fileName, file));
            
            GeneratePresignedUrlRequest generatePresignedUrlRequest = 
            new GeneratePresignedUrlRequest(bucketName, fileName);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.

            URL s = s3client.generatePresignedUrl(generatePresignedUrlRequest); 
            insertUrl(s.toString(),id);
            System.out.println(s);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            	    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    
 }

 private String getFileName(final Part part) {
    final String partHeader = part.getHeader("content-disposition");
    LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
    for (String content : part.getHeader("content-disposition").split(";")) {
        if (content.trim().startsWith("filename")) {
            return content.substring(
                    content.indexOf('=') + 1).trim().replace("\"", "");
        }
    }
    return null;
 }

 private void insertUrl(String url, String id){
  String n = "";          
  String dbName = "mydb";
  String userName = "pmontojo";
  String password = "pmontojo";
  String hostname = "mydb.cued7orr1q2t.us-west-2.rds.amazonaws.com";
  String port = "3306";
  String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
    port + "/" + dbName + "?user=" + userName + "&password=" + password;
  Connection conn = null;
  Statement setupStatement = null;
  Statement readStatement = null;
  ResultSet resultSet = null;
  String results = "";
  int numresults = 0;
  String statement = null;

  try {
  
    conn = DriverManager.getConnection(jdbcUrl);
    setupStatement = conn.createStatement();

    String insertUrl = "update metadata set url ='" + url + "' where id =" + id + ";";

    setupStatement.addBatch(insertUrl);

    setupStatement.executeBatch();

    setupStatement.close();
    
  } catch (SQLException ex) {

    System.out.println("SQLException: " + ex.getMessage());
    System.out.println("SQLState: " + ex.getSQLState());
    System.out.println("VendorError: " + ex.getErrorCode());
  } finally {
    System.out.println("Closing the connection.");
    if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
  }

  
}

 public String getUrl(String fileName) {
        return new String("https://s3.amazonaws.com/" + bucketName + "/" + fileName);
    }
 
 public void sqs(String msg) throws Exception{
 AWSCredentials awsCreds = new PropertiesCredentials(new File("/Users/paulamontojo/Desktop/AwsCredentials.properties"));

        AmazonSQS sqs = new AmazonSQSClient(awsCreds);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        sqs.setRegion(usWest2);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");

        try {

            String myQueueUrl = "https://sqs.us-west-2.amazonaws.com/711690152696/MyQueue";

           // List queues
            System.out.println("Listing all queues in your account.\n");
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }


            // Send a message
            System.out.println("Sending a message to MyQueue.\n");
            sqs.sendMessage(new SendMessageRequest(myQueueUrl, msg));

  
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
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
     try {
         processRequest(request, response);
     } catch (Exception ex) {
         Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
     }
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
     try {
         processRequest(request, response);
     } catch (Exception ex) {
         Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
     }
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