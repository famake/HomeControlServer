package hus;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jws.WebService;

import ninja.NinjaAPI;
import fjernkontroll.Arduino;
import fjernkontroll.SerialException;

/**
 * Session Bean implementation class VentilationController
 */
@Singleton
@LocalBean
@WebService(endpointInterface = "hus.VentilationControllerRemote")
public class VentilationController implements VentilationControllerRemote {

	private static final int FAN_SWITCH = 0;
	private boolean automatic;
	private boolean fanPower;
	
	private int n_sensors = 1;
	private double[] 
			lowPoint = new double[] {27.0},
			highPoint = new double[] {30.0};
	
	@EJB
	private Arduino arduino;
	@EJB
	private NinjaAPI ninjaApi;
	
	@PostConstruct
	public void init() {
		fanPower = false;
		automatic = true;
	}
	
	@Schedule(hour="*", minute="*/2")
	public void timeout() {
		if (automatic) {
			for (int i=0; i<n_sensors; ++i) {
				try {
					double temp = arduino.getTemperature(i);
					if (fanPower && temp < lowPoint[i]) {
						ninjaApi.settBryter(FAN_SWITCH, false);
						fanPower = false;
					}
					else if (!fanPower && temp > highPoint[i]) {
						ninjaApi.settBryter(FAN_SWITCH, true);
						fanPower = true;
					}
				} catch (SerialException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public double getSensorTemperature(int sensor) 
			throws SerialException {
		if (sensor < n_sensors) 
			return arduino.getTemperature(sensor);
		else
			throw new IllegalArgumentException("Invalid sensor ID");
	}
	
	@Override
	public void setFanManual(boolean fanState) {
		automatic = false;
		fanPower = fanState;
		try {
			ninjaApi.settBryter(FAN_SWITCH, fanState);
		} catch (IOException e) {
		}
	}

	@Override
	public void setFanAuto() {
		automatic = true;
		timeout();
	}
	
	@Override
	public boolean isFanAuto() {
		return automatic;
	}
	
	@Override
	public boolean getFanState() {
		return fanPower;
	}
	
	
}
