package com.winksys.fortress;

import com.winksys.fortress.ui.MainUI;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		final IServerContext context = ContextFactory.newContext();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						new Thread(new CheckMessagesJob(context)).start(); 
						new Thread(new SendMessagesJob(context)).start();
						
						
						Thread.sleep(30000);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
		 
		new MainUI(context).setVisible(true);
		
	}

}
