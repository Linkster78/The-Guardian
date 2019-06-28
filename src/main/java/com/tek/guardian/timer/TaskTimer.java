package com.tek.guardian.timer;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class TaskTimer {
	
	private Timer timer;
	
	public TaskTimer() {
		timer = new Timer("Task Timer");
	}
	
	public void start() {
		timer.scheduleAtFixedRate(new TemporaryActionChecker(), 0, TimeUnit.SECONDS.toMillis(30));
		timer.scheduleAtFixedRate(new VoiceChannelChecker(), 0, TimeUnit.MINUTES.toMillis(1));
		timer.scheduleAtFixedRate(new ServerProfileChecker(), 0, TimeUnit.MINUTES.toMillis(5));
	}
	
	public Timer getTimer() {
		return timer;
	}
	
}
