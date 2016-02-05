package com.winksys.fortress;

public class ContextFactory {

	public static IServerContext newContext() {
		return new ServerContext();
	}

}
