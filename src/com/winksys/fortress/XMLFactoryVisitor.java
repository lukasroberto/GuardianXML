package com.winksys.fortress;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import com.windi.guardiancar.ejb.client.xml.Fault;
import com.windi.guardiancar.ejb.client.xml.ForwardMessageStatusInfo;
import com.windi.guardiancar.ejb.client.xml.MessageDelivery;
import com.windi.guardiancar.ejb.client.xml.MessageDelivery.ForwardMessage;
import com.windi.guardiancar.ejb.client.xml.ReturnMessageInfo;
import com.windi.guardiancar.ejb.client.xml.StatusReport;

public class XMLFactoryVisitor {

	private static final Logger LOG = LogManager.getLogger(XMLFactoryVisitor.class);

	public static Message visit(IServerContext context, Object obj) throws MessageHandlerException {
		if (obj instanceof Fault) {
			visit((Fault) obj, context);
		} else if (obj instanceof StatusReport) {
			visit((StatusReport) obj, context);
		} else if (obj instanceof MessageDelivery) {
			visit((MessageDelivery) obj, context);
		}
		return null;
	}

	private static void visit(MessageDelivery obj, IServerContext context) throws MessageHandlerException {
		
		List<Object> mensagens = obj.getForwardMessageOrReturnMessage();
		
		for (Object object : mensagens) {
			if (object instanceof ReturnMessageInfo) {
				visit((ReturnMessageInfo) object, context);
			} else if (object instanceof ForwardMessage) {
				visit((ForwardMessage) object, context);
			}
		}
				
	}

	private static void visit(ForwardMessage fm, IServerContext context) {
		context.getMessageHandler().handle(fm);
	}

	private static void visit(ReturnMessageInfo rm, IServerContext context) throws MessageHandlerException {
		context.getMessageHandler().handle(rm);
	}

	private static void visit(StatusReport sr, IServerContext context) {
		List<ForwardMessageStatusInfo> messages = sr.getForwardMessage();
		for (ForwardMessageStatusInfo message : messages) {
			LOG.info("%s => %d: %s", message.getKey(), message.getFid(), message.getMessageStatus().getContent());			
		}
		
	}

	private static void visit(Fault obj, IServerContext context) {
		LOG.error(obj.getFaultCode() + ": " + obj.getFaultString() + " - " + obj.getDetail());
	}

}
