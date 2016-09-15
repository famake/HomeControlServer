package dmx;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;


@ManagedBean
public class DmxBeane {

	@EJB
	private DmxRemote dmx;

	public String laserPaa() {
		dmx.laserOnSound();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Laser OK", "Laser OK"));
		return null;
	}
	
	public String blackout() {
		dmx.blackout();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Blackout OK", "Blackout OK"));
		return null;
	}
	
}
