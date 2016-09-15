package ninja;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.jws.WebService;

/**
 * Session Bean implementation class NinjaInit
 */
@Singleton(mappedName = "NinjaAPI")
@Startup
@LocalBean
@WebService(endpointInterface = "ninja.NinjaAPIRemote")
public class NinjaAPI implements NinjaAPILocal, NinjaAPIRemote {

	private static final String CALLBACK_URL = "http://dev.famake.com:9090/MikaelWeb/NinjaAPICallback";

	@EJB hus.BatmanLocal main;
	
	private static final String ACCESS_TOKEN = "b6KyhuB1vBM6h4Mjkqnp94dZvIecZvC43muBYQ";
	private static final String RF_GUID = "1313BB000705_0_0_11";
	private static final String TEMP_SENSOR_GUID = "1313BB000705_0101_0_31";

	private static final String REGISTER_CALLBACK_URL = "https://api.ninja.is/rest/v0/device/"+RF_GUID+"/callback?user_access_token=" + ACCESS_TOKEN;
	//private static final String REGISTER_CALLBACK_URL = "https://api.ninja.is/rest/v0/device/"+TEMP_SENSOR_GUID+"/callback?user_access_token=" + ACCESS_TOKEN;
	//private static final int NINJA_KNAPP_ID = 0x415D30;
	
	private static final String DEVICE_SEND_URL = "https://api.ninja.is/rest/v0/device/"+RF_GUID+"?user_access_token=" + ACCESS_TOKEN;

	private static final String bryterSluttKode = "000101010101010101010111";
	
	private static final String [] bryterPÂKode = {
				"000101010001010101010111",
				"000101010100010101010111",
				"000101010101000101010111"
		};
	private static final String [] bryterAvKode = {
				"000101010001010101010100",
				"000101010100010101010100",
				"000101010101000101010100"
		};
	public boolean switchState[] = new boolean [bryterPÂKode.length];
	
	private final static String PIR_SENSOR = "010101010101010101010101";
	
    /**
     * Default constructor. 
     * @throws IOException 
     */
	
    public NinjaAPI() {
    }
    
    @PostConstruct
    public void init() {
		if (!checkCallback())
			try {
				setCallback(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    
    private boolean checkCallback()
    {
        try {
            URL url = new URL(REGISTER_CALLBACK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
             
            connection.setInstanceFollowRedirects(false);
           	connection.setRequestMethod("GET");

            int code = connection.getResponseCode();
            boolean has_callback = false;

            if (code == 200)
            {
	            InputStream in = (connection.getInputStream());
	            // {"result":0,"error":"A callback for that device already exists","id":409}
	            JsonReader jr = Json.createReader(in);
	            JsonObject jo = jr.readObject();
	            if (jo.getInt("result", 0) == 1)
	            {
	        		JsonObject data = jo.getJsonObject("data");
	            	if (data != null)
	            	{
	            		has_callback = data.containsKey("url");
	            	}
	            }
            }
            connection.disconnect();
            return has_callback;
        } catch(Exception e) { 
            return false; 
        }
    }
    
    private String getCallbackUrl()
    {
    	return CALLBACK_URL;
    }
    
    private void setCallback(boolean register) throws IOException
    {
        URL url = new URL(REGISTER_CALLBACK_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
         
        connection.setInstanceFollowRedirects(false);
        if (register)
        {
        	connection.setDoOutput(true);
        	connection.setRequestMethod("POST");
        	connection.setRequestProperty("Content-Type", "application/json"); 

            OutputStream os = connection.getOutputStream();
            String jsonOut = "{ \"url\" : \"" + getCallbackUrl() + "\" }";
            os.write(jsonOut.getBytes(Charset.forName("UTF-8"))); 
            os.flush();
        }
        else {
        	connection.setRequestMethod("DELETE");
        }

        int code = connection.getResponseCode();
        boolean success = false;
        
        if (code == 200)
        {
            InputStream in = (connection.getInputStream());
            // {"result":0,"error":"A callback for that device already exists","id":409}
            JsonReader jr = Json.createReader(in);
            JsonObject jo = jr.readObject();
            success = jo.getInt("result", 0) == 1;
            if (!success) {
            	System.out.println("JSON: " + jo);
            }
            connection.disconnect();
        }
        if (!success)
        	throw new RuntimeException("Mottok ugyldig svar fra NinjaBlocks ved oppsett av callback -- code is " + code);
    }
    
    public void settBryter(int bryterId, boolean pÂ) throws IOException
    {

    	
    	if (bryterId < bryterPÂKode.length)
    	{
    		String kode;
    		if (pÂ)
    		{
    			kode = bryterPÂKode[bryterId];
    		}
    		else {
    			kode = bryterAvKode[bryterId];
    		} 
    		switchState[bryterId] = pÂ;
    		sendKode(kode);
    	}
    }
    
    @Override
    public boolean getSwitchState(int id) {
    	return switchState[id];
    }
    
    private void sendKode(String kode) throws IOException {
        URL url = new URL(DEVICE_SEND_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
         
        connection.setInstanceFollowRedirects(false);
        
    	connection.setDoOutput(true);
    	connection.setRequestMethod("PUT");
    	connection.setRequestProperty("Content-Type", "application/json"); 

        OutputStream os = connection.getOutputStream();
        String jsonOut = "{ \"DA\" : \"" + kode + "\" }";
        os.write(jsonOut.getBytes(Charset.forName("UTF-8"))); 
        os.flush();

        int code = connection.getResponseCode();
        boolean success = false;
        
        if (code == 200)
        {
            InputStream in = (connection.getInputStream());
            JsonReader jr = Json.createReader(in);
            JsonObject jo = jr.readObject();
            success = jo.getInt("result", 0) == 1;
            connection.disconnect();
        }

        if (!success)
        	throw new RuntimeException("Mottok ugyldig svar fra NinjaBlocks ved sending av kode");
	}

    @PreDestroy
	protected void destroy()
    {
		try {
			setCallback(false);
		} catch (IOException e) {
		}
    }

	@Override
	public void Callback(String deviceGuid, String id, long timestamp) {
		if (RF_GUID.equals(deviceGuid))
		{
			System.out.println("callback id "+id);
			
			boolean bryterKode = false;
			
			for (int i=0; i<bryterPÂKode.length; i++)
			{
				bryterKode = bryterKode || bryterPÂKode[i].equals(id) || bryterAvKode[i].equals(id);  
			}
			bryterKode = bryterKode || bryterSluttKode.equals(id);
			
			if (!bryterKode)
			{
				if ("010111011111010101010000".equals(id))      main.dorVinduSensor();
				else if ("010000010101110100110000".equals(id)) main.ninjaKnapp();
				else if ("__".equals(id)) main.dorAdminKodeUtside();
				else if ("000000000000000000110000".equals(id)) main.dorFinger();
				else if (PIR_SENSOR.equals(id)) main.pirSensor();
				else 
					System.out.println("NinjaBlocks ukjent id " + id);
			}
		}
	}
	
	
    private JsonObject deviceGetRequest(String deviceGuid) throws IOException
    {
        URL url = new URL("https://api.ninja.is/rest/v0/device/" + deviceGuid + "?user_access_token=" + ACCESS_TOKEN);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
         
        connection.setInstanceFollowRedirects(false);
       	connection.setRequestMethod("GET");

        int code = connection.getResponseCode();

        try {
            if (code == 200)
            {
	            InputStream in = (connection.getInputStream());
	            // {"result":0,"error":"A callback for that device already exists","id":409}
	            JsonReader jr = Json.createReader(in);
	            return jr.readObject();
            }
        } finally {
        	connection.disconnect();
        }
		throw new IOException("Invalid result");
    }

    @Override
	public double getTemperature() throws IOException {
		JsonObject response = deviceGetRequest(TEMP_SENSOR_GUID);
		JsonObject data = response.getJsonObject("data");
		JsonObject lastData = data.getJsonObject("last_data");
		System.out.println(lastData);
		System.out.println(lastData.getJsonNumber("DA").doubleValue());
		return lastData.getJsonNumber("DA").doubleValue();
	}
	
}
