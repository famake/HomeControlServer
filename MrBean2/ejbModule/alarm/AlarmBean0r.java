package alarm;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import dmx.DmxRemote;

/**
 * Session Bean implementation class AlarmBean0r
 */
@Stateless
@LocalBean
public class AlarmBean0r {

	@EJB
	private DmxRemote dmx;
	
	//@Schedule(hour="22", minute="51")
	public void timeout() {
		dmx.strobeHoldOn(254);
	}

}
