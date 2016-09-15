package lifx;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jws.WebService;

import lighting.LifxRmi;

/**
 * Session Bean implementation class LifxBulbControl
 */
@Stateless(mappedName = "LifxBulbControl")
@LocalBean
@WebService(endpointInterface = "lifx.LifxBulbControlRemote")
public class LifxBulbControl implements LifxBulbControlRemote {
	
	private LifxRmi lr;
	
	private LifxRmi getRmi() {
		if (lr == null) {
			try {
				lr = (LifxRmi) Naming.lookup("//localhost/Lifx");
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				throw new EJBException(e);
			}
		}
		return lr;
	}

	@Override
	public void allOff() {
		try {
			getRmi().off();
		} catch (RemoteException e) {
			e.printStackTrace();
			lr = null;
		}
	}
	
	@Override
	public void allOn() {
		try {
			getRmi().on();
		} catch (RemoteException e) {
			e.printStackTrace();
			lr = null;
		}
	}

}
