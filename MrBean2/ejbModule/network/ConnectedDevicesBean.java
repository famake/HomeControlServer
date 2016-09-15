package network;

import hus.BatmanLocal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import fjernkontroll.ForsterkerLocal;

/**
 * Session Bean implementation class ConnectedDevicesBean
 */
@Singleton
@LocalBean
public class ConnectedDevicesBean implements ConnectedDevicesLocal {

	
	private final Client client;
	public HashSet<Host> connectedHosts;
	public HashMap<Integer, Pattern> namePatterns;
	private boolean in;
	@EJB
	private ForsterkerLocal forsterker;
	@EJB
	private BatmanLocal batman;
	
	private final static String MIKAELS_MOBIL_MAC = "84:38:38:EF:4B:04";
	private boolean first;
	
	public ConnectedDevicesBean() {
		client = ClientBuilder.newClient().register(new Authenticator("admin", "password"));
		connectedHosts = new HashSet<>();
		namePatterns = new HashMap<>();
	}
	
	@Schedule(hour="*", minute="*/5")
	public void checkDevices() {
		//System.out.println("Checking network devices");
		String result = 
				client.target("http://192.168.1.1/DEV_show_device.htm")
				.request(MediaType.TEXT_HTML)
				.get(String.class);

		String rx = "var access_control_device([\\d]+)=\"Allowed\\*([\\d\\.]+)\\*([\\dA-Z:]+)\\*primary\";";
		Pattern pat = Pattern.compile(rx);
		Matcher matcher = pat.matcher(result);
		
		HashSet<Host> newConnectedHosts = new HashSet<>();
		while (matcher.find()) {
			String strIndex = matcher.group(1), ip = matcher.group(2);
			String mac = matcher.group(3);
			int index = Integer.parseInt(strIndex);
			Pattern namePattern = namePatterns.get(index);
			if (namePattern == null) {
				namePattern = Pattern.compile("var access_control_device_name" +index + "=\"([^\"]+)\";");
				namePatterns.put(index, namePattern);
			}
			Matcher mat = namePattern.matcher(result);
			String name = "";
			if (mat.find()) {
				name = mat.group(1);
			}
			
			//System.out.println("IP: " + ip + " | MAC address: " + mac + " | name: " + name);
			newConnectedHosts.add(new Host(name, ip, mac));
		}
		if (first) {
			initEvents(newConnectedHosts);
			first = false;
		}
		triggerEvents(newConnectedHosts);
		connectedHosts = newConnectedHosts;
	}
	
	private static boolean isIn(String mac, Set<Host> searchSet) {
		String maclow = mac.toLowerCase();
		for (Host ho : searchSet) {
			if (maclow.equals(ho.mac.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	void triggerEvents(Set<Host> newConnectedHosts) {
		if (in && !isIn(MIKAELS_MOBIL_MAC, newConnectedHosts)) {
			in = false;
			batman.networkPresenceLeft();
		}
		else if (!in && isIn(MIKAELS_MOBIL_MAC, newConnectedHosts)) {
			in = true;
			batman.networkPresenceReturned();
		}
	}
	
	void initEvents(Set<Host> newConnectedHosts) {
		in = isIn(MIKAELS_MOBIL_MAC, newConnectedHosts);
	}

	
}
