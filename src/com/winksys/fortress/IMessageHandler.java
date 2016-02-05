package com.winksys.fortress;

import com.windi.guardiancar.ejb.client.xml.MessageDelivery.ForwardMessage;
import com.windi.guardiancar.ejb.client.xml.ReturnMessageInfo;

public interface IMessageHandler {

	void handle(ForwardMessage fm);

	void handle(ReturnMessageInfo rm) throws MessageHandlerException;

}
