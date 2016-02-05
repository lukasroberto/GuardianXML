package com.winksys.fortress;

import java.util.Date;
import java.util.List;

import com.winksys.fortress.ui.vo.MensagemVO;

public interface IServerContext {

	IConfig getConfig();

	long getFid();

	long getRid();

	IMessageHandler getMessageHandler();

	IServerGateway getGateway();
	
	void updateRid(Long rid);
	
	void updateFid(Long fid);

	List<MensagemVO> getMensagens();
	
	void putMensagem(MensagemVO mensagem);
	
	Date getLastRetCheck();
	
	Date getLastFwdCheck();

}
