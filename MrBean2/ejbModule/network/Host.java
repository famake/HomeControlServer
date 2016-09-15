package network;

public class Host {
	final String mac, ip, name;
	public Host(String name, String ip, String mac) {
		this.name = name;
		this.ip = ip;
		this.mac = mac;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Host) {
			Host h = (Host)o;
			return mac != null && mac.equals(h.mac)
					&& ip != null && ip.equals(h.ip);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return mac.hashCode() + ip.hashCode();
	}
}