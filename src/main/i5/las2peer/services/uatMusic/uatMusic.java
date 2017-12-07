package i5.las2peer.services.uatMusic;

import java.io.Serializable;
import java.net.HttpURLConnection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import i5.las2peer.api.Context;
import i5.las2peer.api.ManualDeployment;
import i5.las2peer.api.ServiceException;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.services.uatMusic.database.DatabaseManager;
import java.sql.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;  
import java.io.Serializable;

/**
 *
 * uat-music
 *
 * This microservice was generated by the CAE (Community Application Editor). If you edit it, please
 * make sure to keep the general structure of the file and only add the body of the methods provided
 * in this main file. Private methods are also allowed, but any "deeper" functionality should be
 * outsourced to (imported) classes.
 *
 */
@ServicePath("musicApp")
@ManualDeployment
public class uatMusic extends RESTService {


  /*
   * Database configuration
   */
  private String jdbcDriverClassName;
  private String jdbcLogin;
  private String jdbcPass;
  private String jdbcUrl;
  private static DatabaseManager dbm;



  public uatMusic() {
	super();
    // read and set properties values
    setFieldValues();
        // instantiate a database manager to handle database connection pooling and credentials
    dbm = new DatabaseManager(jdbcDriverClassName, jdbcLogin, jdbcPass, jdbcUrl);
  }

  @Override
  public void initResources() {
	getResourceConfig().register(RootResource.class);
  }

  // //////////////////////////////////////////////////////////////////////////////////////
  // REST methods
  // //////////////////////////////////////////////////////////////////////////////////////

  @Api
  @SwaggerDefinition(
      info = @Info(title = "uat-music", version = "1",
          description = "Music Service for UAT Test",
          termsOfService = "LICENSE.txt",
          contact = @Contact(name = "Melisa Cecilia", email = "CAEAddress@gmail.com") ,
          license = @License(name = "BSD",
              url = "https://github.com/testcae/microservice-uat-music/blob/master/LICENSE.txt") ) )
  @Path("/")
  public static class RootResource {

    private final uatMusic service = (uatMusic) Context.getCurrent().getService();

      /**
   * 
   * getMusic
   *
   * 
   *
   * 
   * @return Response Response node get
   * 
   */
  @GET
  @Path("/get")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiResponses(value = {
       @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Response node get")
  })
  @ApiOperation(value = "getMusic", notes = "Get music from app ")
  public Response getMusic() {

    try {
      Object returnServiceGetImage = Context.getCurrent().invoke(
          "i5.las2peer.services.uatTestImage.uatTestImage@1.0", "getImage"); 
      HashMap<Integer, classes.image> imageMap = new HashMap<Integer, classes.image>();
      // put into array
      JSONParser parser = new JSONParser();
      JSONArray jsonArray = (JSONArray)parser.parse((String) returnServiceGetImage);
      Iterator i = jsonArray.iterator(); 
 
      // put into map of id and image object
      while (i.hasNext())
      {
          JSONObject jsonObj = (JSONObject) i.next();
          classes.image imageObj = new classes().new image();
          imageObj.fromJSON(jsonObj.toJSONString());
          imageMap.put(imageObj.getimageId(), imageObj);
          System.out.println(jsonObj);
      }

      // now process from music database
      Connection conn = service.dbm.getConnection();
      PreparedStatement query = conn.prepareStatement("SELECT * FROM uatTest.tblMusic");
      ResultSet result = query.executeQuery();  
 
            JSONArray jsonResult = new JSONArray();
      while(result.next()) {
        
        // music object
        classes.music musicResult = new classes().new music();
        musicResult.setmusicName(result.getString("musicName"));
        musicResult.setmusicUrl(result.getString("musicUrl"));
        musicResult.setmusicId(result.getInt("musicId"));
        musicResult.setimageId(result.getInt("imageId"));

        // music + image
        classes.image imageResult = imageMap.get(musicResult.getimageId());
        classes.imageMusic imageMusicResult = new classes().new imageMusic();

        if(imageResult != null) {
          imageMusicResult.setimageName(imageResult.getimageName());
          imageMusicResult.setimageUrl(imageResult.getimageUrl());
        }
        imageMusicResult.setmusicName(musicResult.getmusicName());
        imageMusicResult.setmusicUrl(musicResult.getmusicUrl());

        jsonResult.add(imageMusicResult.toJSON());
      }
      // responseGetMusic
      return Response.status(HttpURLConnection.HTTP_OK).entity(jsonResult.toJSONString()).build();
 

    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  /**
   * 
   * postMusic
   *
   * 
   * @param payloadPost Payload post node a JSONObject
   * 
   * @return Response 
   * 
   */
  @POST
  @Path("/post")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @ApiResponses(value = {
       @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "responsePost")
  })
  @ApiOperation(value = "postMusic", notes = " ")
  public Response postMusic(String payloadPost) {
    JSONObject payloadPost_JSON = (JSONObject) JSONValue.parse(payloadPost);

    try {
      Object returnServicePostImage = Context.getCurrent().invoke(
          "i5.las2peer.services.uatTestImage.uatTestImage@1.0", "postImage");
    } catch (Exception e) {
        e.printStackTrace();
    }
    // responsePost
    boolean responsePost_condition = true;
    if(responsePost_condition) {
      String resultPost = "Some String";
      return Response.status(HttpURLConnection.HTTP_OK).entity(resultPost).build();
    }
    return null;
  }



  }

  // //////////////////////////////////////////////////////////////////////////////////////
  // Service methods (for inter service calls)
  // //////////////////////////////////////////////////////////////////////////////////////
  
  

}
