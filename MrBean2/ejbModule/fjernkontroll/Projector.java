package fjernkontroll;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jws.WebService;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Session Bean implementation class Projector
 */
@Singleton(mappedName = "Projector")
@LocalBean
@WebService(endpointInterface = "fjernkontroll.ProjectorRemote")
public class Projector implements ProjectorRemote {

	private static final int TIMEOUT_MS = 50;
	private static final int N_TRIES = 100, N_TRIES_FAST = 2;
	@EJB SerialManager serialManager;
	
    /**
     * Default constructor. 
     */
    public Projector() {
    }
    
    private SerialPort getSerialPort() throws SerialPortException {
		return serialManager.getSerialPort("COM3", 115200);
    }
    
    private void command(String cmd, String expectedReply)
    		throws SerialException 
    {
    	try {
			SerialPort port = getSerialPort();
			port.readBytes();

			//System.out.println("Sending command");
			if (port.writeBytes(cmd.getBytes())) {
				int waiting = 0;
				while (port.getInputBufferBytesCount() < expectedReply.length()  
						&& waiting < N_TRIES) {
					try {
						Thread.sleep(TIMEOUT_MS);
					} catch (InterruptedException e) {
					}
					++waiting;
				}
				if (waiting == N_TRIES) {
					throw new SerialException("Serial communication failed");
				}
				//System.out.println("Sent bytes to projector");
				readReply(expectedReply, port);
				//System.out.println("Read reply");
			}
			else {
		    	System.err.println("Could not send to projector");
				throw new SerialException("Could not send to projector");
			}
    	} catch (SerialPortException e) {
			e.printStackTrace();
			throw new SerialException(e);
		}
    }

	private void readReply(String expectedReply, SerialPort port)
			throws SerialPortException, SerialException {
		for (char c : expectedReply.toCharArray()) {
			byte[] in = port.readBytes(1);
			if (in == null || in.length == 0 || in[0] != c) {
				throw new SerialException("Invalid response from projector");
			}
		}
	}
    
    @Override
    public void setPower(boolean power) throws SerialException {
		//System.out.println("Setting power: "+power);
    	if (power) {
	    	command("X001X", "X0_1X");
    	}
    	else {
    		command("X002X", "X002XX0_2X");
    	}
    }
    

    @Override
    public void sleepTimer(int time) throws SerialException {
    	if(time == 30)
    		command("Z031W+0001Z", "Z031W+0001ZZ0_31+00002");
    	else if(time == 60)
    		command("Z031W+0002Z", "Z031W+0002ZZ0_31+0002Z");
    	else if(time == 90)
    		command("Z031W+0003Z", "Z031W+0003ZZ0_31+0003Z");
    	else if(time == 120)
    		command("Z031W+0004Z", "Z031W+0004ZZ0_31+0004Z");	
    }
    
    public void sleepTimerOff() throws SerialException{
    	command("Z031W+0000Z", "Z031W+0000ZZ0_31+00000");
    }
    
    @Override
    public void SetPictureMode(String mode) throws SerialException
    {
    	if(mode.equals("day"))
    		command("X048X", "X048XX0_48X");
    		//command("Z010W+0005Z", "Z010W+0005ZZ0_10+0005Z");
    	else if(mode.equals("night"))
    		command("X047X", "X047XX0_47X");
    		//command("Z010W+0004Z", "Z010W+0004ZZ0_10+0004Z");
    }
    
    @Override
    public void sleepDisabled() throws SerialException
    {
    	command("Z031W+001Z", "Z031W+0000ZZ0_31+0000Z");
    }
    
    @Override
    public void refreshPicture() throws SerialException
    {
    	command("Z022W+0006Z", "Z022W+0006ZZ0_22+0006Z");
    }
    
    
    @Override
    public boolean getPower() throws SerialException {
    	try {
	    	SerialPort port = getSerialPort();
			port.readBytes();
			port.writeBytes(("X000X").getBytes());
			String expected = "X000XRS232_OKX0_0X";
			int waiting = 0;
			while (port.getInputBufferBytesCount() < expected.length()  
					&& waiting < N_TRIES_FAST) {
				try {
					Thread.sleep(TIMEOUT_MS);
				} catch (InterruptedException e) {
				}
				++waiting;
			}
			if (waiting == N_TRIES_FAST) {
				return false;
			}
			else {
				readReply(expected, port);
				return true;
			}
    	} catch (SerialPortException e) {
    		throw new SerialException("Error in communication", e);
    	}
    }

}
