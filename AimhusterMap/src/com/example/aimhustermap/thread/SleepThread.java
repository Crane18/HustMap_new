package com.example.aimhustermap.thread;

public class SleepThread extends Thread{
	
	private final int SLEEPTIME = 600;
	
	@Override
	public void run(){
		
		try {
			Thread.currentThread().sleep(SLEEPTIME);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
