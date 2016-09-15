package vlc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.jws.WebService;

import org.apache.commons.codec.binary.Base64;

/**
 * Session Bean implementation class VlcControl
 */
@Stateless
@LocalBean
@WebService(endpointInterface = "vlc.VlcControlRemote")
public class VlcControl implements VlcControlRemote {

	// Port: http://wiki.hobbyistsoftware.com/wiki/VLC_http_not_working_locally
	public final static String VLC_REQUEST_URL = "http://localhost:7979/requests/status.json";
	private final static String VLC_PASSWORD = "wkd";
	
    public VlcControl() {
    }
    
    @Override
    public void pause() throws IOException {
    	vlcRequest("?command=pl_forcepause");
    }
    
    @Override
    public void resume() throws IOException {
		vlcRequest("?command=pl_forceresume");
    }

    public static JsonObject vlcRequest(String request) 
    					throws IOException {
        URL url = new URL(VLC_REQUEST_URL + request);
        HttpURLConnection connection = (HttpURLConnection)
        			url.openConnection();
        String encoded = new String(Base64.encodeBase64((":"+VLC_PASSWORD).getBytes())); 
        connection.setRequestProperty("Authorization", "Basic "+encoded); 
        int code = connection.getResponseCode();
        if (code == 200) {
            InputStream in = (connection.getInputStream());
            // {"result":0,"error":"A callback for that device already exists","id":409}
            JsonReader jr = Json.createReader(in);
            JsonObject jo = jr.readObject();
            connection.disconnect();
            return jo;
        }
        else {
        	throw new IOException("Unexpected HTTP response code: " + code);
        }
    }

	@Override
	public String getState() throws IOException {
		return vlcRequest("").getString("state");
	}
    
}
