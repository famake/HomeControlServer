package fjernkontroll;

import java.util.HashMap;

import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Session Bean implementation class SerialManager
 */
@Singleton
@LocalBean
public class SerialManager {

	public HashMap<String,SerialPort> ports;

    public SerialManager() {
    	ports = new HashMap<>();
    }
    
	@PreDestroy
    public void destroy() {
		for (SerialPort sp : ports.values()) {
	    	try {
				sp.closePort();
			} catch (SerialPortException e) {
			}
		}
    }
	
	public SerialPort getSerialPort(String com, int baud) 
			throws SerialPortException {
		SerialPort sp = ports.get(com);
    	if (sp != null && !sp.isOpened())
    	{
    		try {
    			sp.closePort();
    		} catch (SerialPortException e) {
    		}
    		sp = null;
    	}
    	if (sp == null)
    	{
    		sp = new SerialPort(com);
    		sp.openPort();
    		sp.setParams(baud, 8, 1, 0);
    		ports.put(com, sp);
    	}
    	return sp;
	}

}
