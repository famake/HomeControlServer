package fjernkontroll;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jws.WebService;

import fjernkontroll.ArduinoRemote;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Session Bean implementation class Arduino
 */
@Singleton(mappedName = "Arduino")
@LocalBean
@WebService(endpointInterface = "fjernkontroll.ArduinoRemote")

public class Arduino implements ArduinoRemote {

	private static int TV_POWER_CODE = 0x122430CF;
	@EJB SerialManager serialManager;
	String comPort;
	private boolean override;
	
    /**
     * Default constructor. 
     */
    public Arduino() {
    	comPort = "COM7";
    }
    

    SerialPort getSerialPort() throws SerialPortException
    {
    	return serialManager.getSerialPort(comPort, 9600);
    }
    
    @PostConstruct
    public void create() {
    	try {
			getSerialPort();
	    	override = false;
		} catch (SerialPortException e) {
		}
    }
    
	@Override
	public boolean setScreenUpOverride(boolean on)
						throws SerialException  {
		try {
			if (on) {
				getSerialPort().writeBytes("t".getBytes());
			}
			else {
				getSerialPort().writeBytes("f".getBytes());
			}
			override = on;
		}
		catch (SerialPortException e) {
			throw new SerialException(e);
		}
		return on;
	}


	@Override
	public boolean getScreenUpOverride() {
		return override;
	}
	
	@Override
	public void toggleCableBoxOn() throws SerialException {
		try {
			getSerialPort().writeBytes(("i "+TV_POWER_CODE+"\n").getBytes());
		}
		catch (SerialPortException e) {
			throw new SerialException(e);
		}
	}
	
	@Override
	public double getTemperature(int sensor) 
			throws SerialException {
		try {
			SerialPort sp = getSerialPort();
			StringBuilder cmd = new StringBuilder();
			cmd.append("c ");
			cmd.append(sensor);
			cmd.append("\n");
			sp.readBytes();
			sp.writeBytes(cmd.toString().getBytes());
			byte[] buffer = new byte[10];
			byte[] tmpbuffer = new byte[1];
			int i = 0, attempt = 0;
			while (sp.getInputBufferBytesCount() == 0 && ++attempt < 10) {
				Thread.sleep(10);
			}
			if (attempt == 10) {
				throw new SerialPortException("No reply", "COM7", "COM7");
			}
			tmpbuffer = sp.readBytes(1);
			while (tmpbuffer != null && tmpbuffer.length == 1 &&
					tmpbuffer[0] != '\n') {
				buffer[i++] = tmpbuffer[0];
				tmpbuffer = sp.readBytes(1);
			}
			return Double.parseDouble(new String(buffer, 0, i, "UTF-8").trim());
		} catch(SerialPortException | NumberFormatException |
				UnsupportedEncodingException | InterruptedException e){
			e.printStackTrace();
			throw new SerialException(e);
		}
	}

}
