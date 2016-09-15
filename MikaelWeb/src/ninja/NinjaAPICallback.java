package ninja;

import java.io.IOException;
import java.io.InputStream;

import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NinjaAPICallback
 */
@WebServlet("/NinjaAPICallback")
public class NinjaAPICallback extends HttpServlet {
	
	@EJB ninja.NinjaAPILocal ninjaapi;
	
	private static final long serialVersionUID = 1L;
       
	/**
     * @see HttpServlet#HttpServlet()
     */
    public NinjaAPICallback() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Got callback (GET)");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        InputStream in = request.getInputStream();

        JsonReader jr = Json.createReader(in);
        JsonObject jo = jr.readObject();
        
        String data = jo.getString("DA");
        String deviceGuid = jo.getString("GUID");
        long timestamp = jo.getJsonNumber("timestamp").longValue();
        
        ninjaapi.Callback(deviceGuid, data, timestamp);        
	}
}
