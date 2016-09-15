package sonos;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.UDAServiceTypeHeader;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;


/**
 * Session Bean implementation class Sonos
 */
@Stateless
@LocalBean
@WebService(endpointInterface = "sonos.SonosRemote")
public class SonosBean implements SonosRemote {

	private UpnpService us;
	
	@Override
	public void play() {

		us =  new UpnpServiceImpl();
		final UDAServiceType udaType = new UDAServiceType("AVTransport");
        us.getControlPoint().search(new UDAServiceTypeHeader(udaType), 1000);
	}
	


}
