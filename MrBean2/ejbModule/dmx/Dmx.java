package dmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jws.WebService;

import lighting.ColorRailRmi;
import lighting.DmxControlRmi;

@Singleton(mappedName="Dmx")
@LocalBean
@WebService(endpointInterface="dmx.DmxRemote")
public class Dmx implements DmxRemote {

	private int player;
	
	//@EJB private NinjaAPIRemote ninja;
	
	private DmxControlRmi getRmi() throws MalformedURLException, RemoteException, NotBoundException {
		DmxControlRmi dci = (DmxControlRmi) Naming.lookup("//localhost/DmxControl");
		return dci;
	}
	
	@Override
	public void laserOnSound() {
		try {
			getRmi().laserOnSound();
			//ninja.settBryter(2, true);
		} catch (NotBoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void strobeFlash() {
		try {
			getRmi().strobeFlash();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void strobeHoldOn(int speed) {
		try {
			getRmi().strobeHoldOn(speed);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void strobeHoldRelease() {
		try {
			getRmi().strobeHoldRelease();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	@Override 
	public void beerPong() {
		try {
			if (player == 1) {
				player = 0;
				getRmi().beerPong(player);
			}
			else {
				player = 1;
				getRmi().beerPong(player);
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setColorRailRed(int r) {
		try {
			ColorRailRmi cr = (ColorRailRmi) Naming.lookup("//localhost/ColorRail");
			cr.setDimmerLevel(255);
			cr.setRed(r);
			getRmi().startSending();
		} catch(RemoteException | MalformedURLException |
				NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setColorRails(int r, int g, int b) {
		try {
			for (ColorRailRmi cr : getRmi().getColorRails()) {
				cr.setBleu(b);
				cr.setRed(r);
				cr.setGreen(g);
			}
		} catch(RemoteException | MalformedURLException |
				NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void smokeOn() {
		try {
			getRmi().smokeOn();
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void smokeOff() {
		try {
			getRmi().smokeOff();
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setDart(boolean dartOn) {
		try {
			getRmi().setDart(dartOn);
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void blackout() {
		try {
			getRmi().blackout();
			//ninja.settBryter(2, false);
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void quit() {
		try {
			getRmi().quit();
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	

}
