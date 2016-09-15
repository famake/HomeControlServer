package sonos;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class TestSender
 */
@Stateless
@LocalBean
public class TestSender implements TestSenderRemote {

    /**
     * Default constructor. 
     */
    public TestSender() {
    }
    
    public void play() throws IOException {
        URL url = new URL("http://192.168.1.3:1400/MediaRenderer/AVTransport/Control");
        HttpURLConnection connection = (HttpURLConnection)
        			url.openConnection();
        
    	connection.setDoOutput(true);
    	connection.setRequestMethod("POST");
    	connection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\""); 
    	connection.setRequestProperty("SOAPACTION", "\"urn:schemas-upnp-org:service:AVTransport:1#Play\""); 

        OutputStream os = connection.getOutputStream();
        String s04p0r = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:Play xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\"><InstanceID>0</InstanceID><Speed>1</Speed></u:Play></s:Body></s:Envelope>";
        os.write(s04p0r.getBytes(Charset.forName("UTF-8"))); 
        os.flush();
        
        int code = connection.getResponseCode();
        if (code == 200) {
            connection.disconnect();
        }
        else {
        	throw new IOException("Unexpected HTTP response code: " + code);
        }
    }

}
