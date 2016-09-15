package main;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import sonos.TestSenderRemote;
import dmx.DmxRemote;

@ManagedBean
public class Main {

	@EJB
	private DmxRemote dmx;
	@EJB
	private TestSenderRemote sonos;
	
	public String quitLocalController() {
		dmx.quit();
		return null;
	}
	
	public String sonosPlay() {
		try {
			sonos.play();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
