package fjernkontroll;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jws.WebService;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Session Bean implementation class Forsterker
 */
@Singleton(mappedName = "Forsterker")
@LocalBean
@WebService(endpointInterface = "fjernkontroll.ForsterkerRemote")
public class Forsterker implements ForsterkerRemote, ForsterkerLocal {

	@EJB SerialManager serialManager;
	String comPort;
	public boolean on;
	
	public final static Kilde[] kilder = {
		new Kilde(1, "PC"),
		new Kilde(2, "XBOX"),
		new Kilde(3, "TV"),
		new Kilde(4, "XBOX PC"),
		new Kilde(6, "PC SONOS"),
		new Kilde(7, "XBOX SONOS")
	};

	
    /**
     * Default constructor. 
     */
    public Forsterker() {
    	comPort = "COM29";
    }
    
    SerialPort getSerialPort() throws SerialPortException
    {
    	return serialManager.getSerialPort(comPort, 112500);
    }
    
    private int askInt(String key) throws SerialException 
    {
    	if (!on && !erPaa()) {
    		throw new PoweredOffException("Forsterker av");
    	}
    	try {
			SerialPort port = getSerialPort();
			port.readBytes();
			port.writeBytes(("\r"+key+"?\r").getBytes());
	    	byte[] data = port.readBytes(1);
			if (data[0] == '\r')
			{
				boolean end = false;
				String answer = "";
				while (!end)
				{
					data = port.readBytes(1);
					if (data == null || data.length == 0 || data[0] == '\r')
						end = true;
					else
						answer += new String(data);
				}
				port.readBytes();
				
				String[] parts = answer.split("=");
				if (parts.length == 2 && 
						key.equals(parts[0]))
				{
					return Integer.parseInt(parts[1]);
				}
				else {
					System.err.println("Ugyldig svar fra forsterker: " + answer);
				}
			}
			else {
				System.err.println("Ugyldig svar fra forsterker: " + data[0]);
			}
			throw new SerialException("Ugyldig svar fra forsterker");
    	} catch (SerialPortException e) {
			e.printStackTrace();
			throw new SerialException(e);
		}
    }
    
    private String askString(String key) throws SerialException {
    	return askString(key, false);
    }
    
    private String askString(String key, boolean ignorePowerState) throws SerialException 
    {
    	if (!on && !ignorePowerState && !erPaa()) {
    		throw new SerialException("Forsterker av");
    	}
    	try {
			SerialPort port = getSerialPort();
			port.readBytes();
			port.writeBytes(("\r"+key+"?\r").getBytes());
			int waiting = 0;
			while (port.getInputBufferBytesCount() == 0 && waiting < 10) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}
				++waiting;
			}
			if (port.getInputBufferBytesCount()== 0) {
				throw new SerialException("Timeout!");
			}
	    	byte[] data = port.readBytes(1);
	    	
			if (data[0] == '\r')
			{
				boolean end = false;
				String answer = "";
				while (!end)
				{
					data = port.readBytes(1);
					if (data == null || data.length == 0 || data[0] == '\r')
						end = true;
					else
						answer += new String(data);
				}
				port.readBytes();
				String[] parts = answer.split("=");
				if (parts.length == 2)
				{
					return parts[1];
				}
				else{
					System.out.println("Mottok feil svar: "+parts[1]);
					
				}
			}
			else {
				System.out.println("Mottok feil byte: "+Integer.toString(data[0]));
			}
			throw new SerialException("Ugyldig svar fra forsterker");
    	} catch (SerialPortException e) {
			e.printStackTrace();
			throw new SerialException(e);
		}
    }

    private String adjustValue(String key, String operator) throws SerialException 
    {
    	if (!on && !erPaa()) {
    		throw new SerialException("Forsterker av");
    	}
    	try {
			SerialPort port = getSerialPort();
			port.readBytes();
			port.writeBytes(("\r"+key+operator+"\r").getBytes());
			
	    	byte[] data = port.readBytes(1);
	    	//System.out.println();
			if (data[0] == '\r')
			{
				boolean end = false;
				String answer = "";
				while (!end)
				{
					data = port.readBytes(1);
					if (data == null || data.length == 0 || data[0] == '\r')
						end = true;
					else
						answer += new String(data);
				}
				port.readBytes();
				
				String[] parts = answer.split("=");
				if (parts.length == 2 && 
						key.equals(parts[0]))
				{
					return parts[1];
				}
				else {

					System.out.println("Mottok feil svar: "+answer);
				}
			}
			else 
			{
				System.out.println("Mottok feil byte: "+Integer.toString(data[0]));
			}
			throw new SerialException("Ugyldig svar fra forsterker");
    	} 
    	catch (SerialPortException e) 
    	{
			e.printStackTrace();
			throw new SerialException(e);
		}
    }
    
    
    private void setValue(String key, String value) throws SerialException 
    {
    	if (!on && !erPaa()) {
    		throw new SerialException("Forsterker av");
    	}
    	try {
			SerialPort port = getSerialPort();
			port.readBytes();
			port.writeBytes(("\r"+key+"="+value+"\r").getBytes());
		
	    	byte[] data = port.readBytes(1);

			if (data[0] == '\r')
			{
				boolean end = false;
				String answer = "";
				while (!end)
				{
					data = port.readBytes(1);
					if (data == null || data.length == 0 || data[0] == '\r')
						end = true;
					else
						answer += new String(data);
				}
				port.readBytes();
				
				String[] parts = answer.split("=");
				if (parts.length != 2 ||
						key.equals(parts[0]))
				{
					throw new SerialException("Ugyldig svar fra forsterker");
				}
			}
			else  {
				System.out.println("Mottok feil byte: "+Integer.toString(data[0]));
				throw new SerialException("Ugyldig svar fra forsterker");	
			}
    	} catch (SerialPortException e) {
			e.printStackTrace();
			throw new SerialException(e);
		}
    }
    
    @Override
    public void av() throws SerialException
    {
    	try {			
    		getSerialPort().readBytes();
			getSerialPort().writeBytes("\rMain.Power=Off\r".getBytes());
			getSerialPort().readBytes();
			on = false;
		} catch (SerialPortException e)
		{
			e.printStackTrace();
			throw new SerialException(e);
		}
    }
    
    @Override
    public void paa() throws SerialException
    {
    	try {
    		int attempts = 0;
    		do {			
    			getSerialPort().readBytes();
    			getSerialPort().writeBytes("\rMain.Power=On\r".getBytes());
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
    			getSerialPort().readBytes();
    		} while (!erPaa() && attempts++ < 3);
		} catch (SerialPortException e)
		{
			e.printStackTrace();
			throw new SerialException(e);
		}
    }

    @Override
    public void mute() throws SerialException
    {
    	setValue("Main.Mute", "On");
    }
    
    @Override
    public void unmute() throws SerialException
    {
    	setValue("Main.Mute", "Off");
    }

    @Override
    public boolean erMuted() throws SerialException {
    	String muted;
		muted = askString("Main.Mute");
    	return "On".equals(muted);
    }
    
    
    @Override
    public String nesteLytteModus() throws SerialException {
    	setValue("Main.ListeningMode", "+");
    	return askString("Main.ListeningMode", true);
    }
    
    @Override
    public String getListeningMode() throws SerialException {
    	return askString("Main.ListeningMode", true);
    }
    
    @Override
    public int volumNed() throws SerialException
    {
    	return Integer.parseInt(adjustValue("Main.Volume", "-"));
    }

    @Override
    public int volumOpp() throws SerialException
    {
    	return Integer.parseInt(adjustValue("Main.Volume", "+"));
    }
    
    @Override
    public int getVolum() throws SerialException
    {
    	return askInt("Main.Volume");
    }
    
    @Override
    public void setVolum(int volum) throws SerialException {
    	setValue("Main.Volume", Integer.toString(volum));
    }
    
    public void nesteKilde() throws SerialException
    {
    	adjustValue("Main.Source", "+");
    }

    @Override
    public void setKilde(int kilde) throws SerialException {
    	setValue("Main.Source", Integer.toString(kilde));
    }

    @Override
    public void setKildePaa(int kilde) throws SerialException {
    	if (!erPaa()) {
    		paa();
    		try {
    			Thread.sleep(1000);
    			int get_kilde = -1, attempts = 10;
    			do {
    				try {
	        			setKilde(kilde);
	        			Thread.sleep(500);
	        			get_kilde = getKilde();
    				} catch (SerialException e) {
    				}
        		} while (get_kilde != kilde && --attempts >= 0);
    		}catch (InterruptedException e) {
    		}
    		   		
    	}
    	else {
    		setKilde(kilde);
    	}
    }
    
    @Override
    public int getKilde() throws SerialException
    {
    	return askInt("Main.Source");
    }
    
    @Override
    public List<Kilde> kildeListe() {
    	return Arrays.asList(kilder);
    }

    @Override
    public boolean erPaa() {
    	String power;
    	try {
			power = askString("Main.Power", true);
		} catch (SerialException e) {
			power = "";
		}
    	on = "On".equals(power);
    	return on;
    }


    
    
}
