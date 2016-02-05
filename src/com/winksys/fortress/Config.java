package com.winksys.fortress;

public class Config implements IConfig {

	private String url1;
	private String url2;
	private String user;
	private String pass;
	private String path;
	private String fwdPath;
	private String driverClass;
	private String urlDatabase;
	private String userDatabase;
	private String passDatabase;
	
	public Config(String url1, String url2, String user, String pass, String path, String fwdPath, String driverClass, String urlDatabase, String userDatabase, String passDatabase) {
		this.url1 = url1;
		this.user = user;
		this.pass = pass;
		this.path = path;
		this.fwdPath = fwdPath;
		this.driverClass = driverClass;
		this.urlDatabase = urlDatabase;
		this.userDatabase = userDatabase;
		this.passDatabase = passDatabase;
	}

	@Override
	public String getServerURL1() {
		return url1;
	}
	
	@Override
	public String getServerURL2() {
		return url2;
	}

	@Override
	public String getServerUser() {
		return user;
	}

	@Override
	public String getServerPassword() {
		return pass;
	}

	@Override
	public String getRetPath() {
		return this.path;
	}

	@Override
	public String getFwdPath() {
		return this.fwdPath;
	}

	@Override
	public String getDriverDatabase() {
		return driverClass;
	}

	@Override
	public String getURLDatabase() {
		return urlDatabase;
	}

	@Override
	public String getUserDatabase() {
		return userDatabase;
	}

	@Override
	public String getPassDatabase() {
		return passDatabase;
	}

}
