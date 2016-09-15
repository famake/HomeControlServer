package hus;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jws.WebService;

import vlc.ResumeTimerThread;
import fjernkontroll.SerialException;

/**
 * Session Bean implementation class Batman
 */
@Singleton(mappedName = "Batman")
@LocalBean
@WebService(endpointInterface = "hus.BatmanRemote")
public class Batman implements BatmanRemote, BatmanLocal {
	
	@EJB fjernkontroll.ForsterkerLocal forsterker;
	@EJB ninja.NinjaAPI ninja;
	@EJB vlc.VlcControl vlc;
	@EJB sonos.TestSender sonos;
	@EJB fjernkontroll.Projector projector;
	@EJB dmx.DmxRemote dmx;
	@EJB lifx.LifxBulbControlRemote lifx;
	
	private boolean present = true;
	private ResumeTimerThread rtt;
	
    /**
     * Default constructor. 
     */
	
    public Batman() {
        // TODO Auto-generated constructor stub
    }
    
    @PostConstruct
    public void init() {
    }

    
    public void altAv()
    {
    	try {
			forsterker.av();
		} catch (SerialException e) {
			e.printStackTrace();
		}
    }
    
    public void volumNed()
    {
    	try {
			forsterker.volumNed();
		} catch (SerialException e) {
			e.printStackTrace();
		}
    }

    public void volumOpp()
    {
    	try {
			forsterker.volumOpp();
		} catch (SerialException e) {
			e.printStackTrace();
		}
    }

	@Override
	public int getVolum() {
		try {
			return forsterker.getVolum();
		} catch (SerialException e) {
			e.printStackTrace();
			return -999;
		}
	}
	


	@Override
	public void forsterkerNesteKilde() {
		try {
			forsterker.nesteKilde();
		} catch (SerialException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dorVinduSensor() {
		
		System.out.println("dør / vindu sensor");
		try {

			ninja.settBryter(0, !ninja.getSwitchState(0));
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ninjaKnapp() {
		try {
			ninja.settBryter(1, !ninja.getSwitchState(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dorAdminKodeUtside() {
		returned();
	}

	@Override
	public void dorFinger() {
		returned();
	}

	@Override
	public void pirSensor() {	
		try {
			String state = vlc.getState();
			// was set to pause, not by this program
			boolean playing = "playing".equals(state);
			boolean playing_or_temp_paused = 
					playing || 
					(rtt != null && rtt.isRunning());
			if (playing)
				ninja.settBryter(0, true);
			vlc.pause();
			if (rtt != null) {
				rtt.cancel();
				rtt = null;
			}
			if (playing_or_temp_paused) {
				rtt = new ResumeTimerThread(vlc, ninja, 20000);
				rtt.start();
			}
		} catch (IOException e) {
		}
	}

	@Override
	public void networkPresenceLeft() {
		try {
			forsterker.av();
			projector.setPower(false);
		} catch (SerialException e) {
			e.printStackTrace();
		}
		dmx.blackout();
		lifx.allOff();
		present = false;
	}

	@Override
	public void networkPresenceReturned() {
		returned();
	}

	void returned() {
		if (!present) {
			try {
				forsterker.paa();
			} catch (SerialException e) {
			}
			lifx.allOn();
			present = true;
		}
	}
}
