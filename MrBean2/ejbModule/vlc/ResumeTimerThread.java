package vlc;

import java.io.IOException;

import ninja.NinjaAPI;


public class ResumeTimerThread extends Thread {

	private long timeout;
	private boolean cancelled, running;
	private VlcControl vlc;
	private NinjaAPI ninja;
	
	public ResumeTimerThread(VlcControl vlc,
			NinjaAPI ninja,
			long timeout) {
		this.timeout = timeout;
		this.cancelled = false;
		this.ninja = ninja;
		this.vlc = vlc;
	}
	
	@Override
	public void run() {
		running = true;
		try {
			Thread.sleep(timeout);
			if (!cancelled) {
				vlc.resume();
				ninja.settBryter(0, false);
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		running = false;
	}
	
	public void cancel() {
		cancelled = true;
		running = false;
	}

	public boolean isRunning() {
		return running;
	}
	
}
