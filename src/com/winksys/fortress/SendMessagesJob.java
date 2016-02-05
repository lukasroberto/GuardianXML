package com.winksys.fortress;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.windi.guardiancar.ejb.client.xml.AdCInfo;
import com.windi.guardiancar.ejb.client.xml.AuthenticationInfo;
import com.windi.guardiancar.ejb.client.xml.CategoryType;
import com.windi.guardiancar.ejb.client.xml.Fault;
import com.windi.guardiancar.ejb.client.xml.ForwardMessageInfo;
import com.windi.guardiancar.ejb.client.xml.MessageDataInfo;
import com.windi.guardiancar.ejb.client.xml.StatusReport;
import com.windi.guardiancar.ejb.client.xml.SubmitMessage;
import com.windi.ui.tools.jaxb.JAXBHelper;


public class SendMessagesJob implements Runnable {

	private static final Logger LOG = LogManager.getLogger(SendMessagesJob.class);
	private IServerContext context;

	public SendMessagesJob(IServerContext context) {
		this.context = context;
	}

	@Override
	public void run() {
		
		LOG.info("Verificando mensagens para envio");
		
		IConfig config = context.getConfig();
		String fwdPath = config.getFwdPath();
		
		File path = new File(fwdPath);
		File[] files = path.listFiles();
		
		if (files == null) {
			return;
		}
		
		
		AuthenticationInfo auth = new AuthenticationInfo();
		auth.setId(config.getServerUser());
		auth.setContent(config.getServerPassword());
		
		SubmitMessage sm = new SubmitMessage();
		sm.setAuthentication(auth);
		sm.getForwardMessage();
		
		for (int i = 0; i < files.length; i++) {
			
			try {
				File file = files[i];
				if (file.getName().endsWith("txt")) {
					LOG.debug(file.getAbsolutePath());
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					try {
						reader.readLine();
						String line = reader.readLine();
						
						String[] fields = line.split("\\,");
						String adc = fields[0].trim();
						String message = fields[1].trim();
						
						AdCInfo adcInfo = new AdCInfo();
						adcInfo.setContent(adc);
						
						MessageDataInfo mdInfo = new MessageDataInfo();
						mdInfo.setInfotype("14");
						mdInfo.setContent(message);
												
						ForwardMessageInfo info = new ForwardMessageInfo();
						info.setCategory(CategoryType.DATA);
						info.setAdC(adcInfo);
						info.setMessageData(mdInfo);
						info.setKey(file.getName());
						
						sm.getForwardMessage().add(info);
						
					} finally {
						reader.close();
					}
					
					String name = file.getName();
					int idx = name.lastIndexOf(".");
					if (file.renameTo(new File(file.getParent(), name.substring(0,idx) + "." + "pro"))) {
						LOG.debug("Marked as sent");
					} else {
						LOG.error(String.format("Error to rename file %s", file.getAbsolutePath()));
					}
				}
			} catch (Exception e) {
				LOG.error(e);
				if (LOG.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
			
		}
		
		// 
		if (!sm.getForwardMessage().isEmpty()) {
			try {
				String mensagem = JAXBHelper.marshal(sm);
				mensagem = context.getGateway().sendMessage(mensagem);
				Object obj = JAXBHelper.unmarshal(new ByteArrayInputStream(mensagem.getBytes()), StatusReport.class, Fault.class);
				XMLFactoryVisitor.visit(context, obj);
			} catch (Exception e) {
				LOG.error(e);
				if (LOG.isDebugEnabled()) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

}
