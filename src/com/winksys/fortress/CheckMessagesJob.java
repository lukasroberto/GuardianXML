package com.winksys.fortress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.windi.guardiancar.ejb.client.xml.AuthenticationInfo;
import com.windi.guardiancar.ejb.client.xml.Fault;
import com.windi.guardiancar.ejb.client.xml.MessageDelivery;
import com.windi.guardiancar.ejb.client.xml.RequestDelivery;
import com.windi.guardiancar.ejb.client.xml.RequestDelivery.ForwardMessage;
import com.windi.guardiancar.ejb.client.xml.RequestDelivery.ReturnMessage;
import com.windi.ui.tools.jaxb.JAXBHelper;

public class CheckMessagesJob implements Runnable {
	
	private static final Logger LOG = LogManager.getLogger(CheckMessagesJob.class);
	
	private IServerContext context;

	public CheckMessagesJob(IServerContext context) {
		this.context = context;
	}

	@Override
	public void run() {
		
		AuthenticationInfo auth = new AuthenticationInfo();
		auth.setId(context.getConfig().getServerUser());
		auth.setContent(context.getConfig().getServerPassword());
		
		ForwardMessage fm = new ForwardMessage();
		fm.setNid(context.getFid());
		
		ReturnMessage rm = new ReturnMessage();
		rm.setRid(context.getRid());

		RequestDelivery req = new RequestDelivery();
		req.setAuthentication(auth);
		req.setForwardMessage(fm);
		req.setReturnMessage(rm);
		
		try {
			String xml = JAXBHelper.marshal(req);
			String ret = context.getGateway().sendMessage(xml);
			Object obj = JAXBHelper.unmarshal(ret, null, Fault.class, MessageDelivery.class);
			
			XMLFactoryVisitor.visit(context, obj);
		} catch (Exception e) {
			LOG.error(e);
			if (LOG.isDebugEnabled()) {
				e.printStackTrace();
			}
		}		
		
	}

}
