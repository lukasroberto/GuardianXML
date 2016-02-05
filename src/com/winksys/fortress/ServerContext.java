package com.winksys.fortress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.winksys.fortress.ui.vo.MensagemVO;

public class ServerContext implements IServerContext {
	
	private static final Logger LOG = LogManager.getLogger();

	private Properties props;

	private List<MensagemVO> mensagens;
	private long fid;
	private long rid;
	
	private Config config;

	private Date lastFwdCheck;

	private Date lastRetCheck;

	public ServerContext() { 
		
		mensagens = new ArrayList<MensagemVO>();
		initConfig();
		
	}

	private void initConfig() {
		props = new Properties();
		try {
			props.load(new FileInputStream("config.properties"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String url1 = props.getProperty("server.url1", "http://xml1.guardiancar.com.br:8080/xml3");
		String url2 = props.getProperty("server.url2", "http://xml2.guardiancar.com.br:8080/xml3");
		String user = props.getProperty("server.user");
		String pass = props.getProperty("server.password");
		String retPath = props.getProperty("server.ret_path", "ret_msg");
		String fwdPath = props.getProperty("server.fwd_path", "fwd_msg");
		String databaseClass = props.getProperty("database.driver.class");
		String urlDatabase = props.getProperty("database.url");
		String userDatabase = props.getProperty("database.user");
		String passDatabase = props.getProperty("database.pass");
				
		LOG.info(String.format("Init config: \nURL1: %s\nURL2: %s\nUser: %s\nRet.Path:%s", url1, url2, user, retPath));
		
		File file = new File(retPath);
		if (!file.isDirectory()) {
			file.mkdir();
		}
		
		file = new File(fwdPath);
		if (!file.isDirectory()) {
			file.mkdir();
		}
		
		LOG.info(String.format("Init config: \nURL1: %s\nURL2: %s\nUser: %s\nRet.Path: %s", url1, url2, user, file.getAbsolutePath()));
		
		this.fid = Integer.parseInt(props.getProperty("fid", "0"));
		this.rid = Integer.parseInt(props.getProperty("rid", "0"));
		
		this.config = new Config(url1, url2, user, pass, retPath, fwdPath, databaseClass, urlDatabase, userDatabase, passDatabase);
	}

	@Override
	public IConfig getConfig() {
		return config;
	}

	@Override
	public long getFid() {
		return fid;
	}

	@Override
	public long getRid() {
		return rid;
	}

	@Override
	public IMessageHandler getMessageHandler() {
		return new MessageHandler(this);
	}

	@Override
	public IServerGateway getGateway() {
		return new ServerGateway(this);
	}

	public synchronized void updateRid(Long rid) {
		if (rid > this.rid) {
			this.rid = rid;
			props.setProperty("rid", rid.toString());
			saveConfig();
			lastRetCheck = Calendar.getInstance().getTime();
		}
	}

	@Override
	public synchronized void updateFid(Long fid) {
		if (fid > this.fid) {
			this.fid = fid;
			props.setProperty("fid", fid.toString());
			saveConfig();
			lastFwdCheck = Calendar.getInstance().getTime();
		}
	}

	private synchronized void saveConfig() {
		try {
			FileOutputStream fos = new FileOutputStream("config.properties");
			props.store(fos, "");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	@Override
	public List<MensagemVO> getMensagens() {
		ArrayList<MensagemVO> ret = new ArrayList<MensagemVO>();
		ret.addAll(mensagens);
		synchronized (mensagens) {
			mensagens.clear();
		}
		return ret;
	}

	@Override
	public void putMensagem(MensagemVO mensagem) {
		synchronized (mensagens) {
			mensagens.add(mensagem);
		}
	}

	public void setMensagens(List<MensagemVO> mensagens) {
		this.mensagens = mensagens;
	}

	@Override
	public Date getLastRetCheck() {
		return lastRetCheck;
	}

	@Override
	public Date getLastFwdCheck() {
		return lastFwdCheck;
	}

	
	
}

