package com.winksys.fortress;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.windi.guardiancar.ejb.client.xml.MessageDelivery.ForwardMessage;
import com.windi.guardiancar.ejb.client.xml.ReturnMessageInfo;
import com.winksys.fortress.ui.vo.MensagemVO;

public class MessageHandler implements IMessageHandler {

	private static final Logger LOG = LogManager.getLogger(MessageHandler.class);
	private ServerContext context;
	private String driver;
	private Connection connection;

	public MessageHandler(ServerContext context) {
		this.context = context;
		System.setProperty("java.net.preferIPv6Addresses", "true");
		driver = context.getConfig().getDriverDatabase();
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	public void handle(ForwardMessage fm) {
		LOG.info(String.format("Fid: %d - %s", fm.getFid(), fm.getMessageStatus().getContent()));
		context.updateFid(fm.getNid());
		
		MensagemVO vo = new MensagemVO();
		vo.setData(Calendar.getInstance().getTime());
		vo.setMensagem(fm.getMessageStatus().getContent());
		vo.setTerminal("-");
		context.putMensagem(vo);		
	}

	@Override
	public void handle(ReturnMessageInfo rm) throws MessageHandlerException {
		IConfig config = context.getConfig();
		String fileName = String.format("%s_%d.txt", rm.getAdC().getContent(), rm.getRid());
		
		String md = rm.getMessageData();
		
		LOG.debug(rm.getRid() + "," + rm.getMessageStatus().getTime());
		
		try {
			Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rm.getMessageStatus().getTime());
			String fDateTime = new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(dateTime);
			
			
			int p1 = Integer.parseInt(md.substring(0,1), 16);
			int p2 = Integer.parseInt(md.substring(1,3), 16);
			int p3 = Integer.parseInt(md.substring(3,5), 16);
			
			int terminal = getTerminal(rm.getAdC().getContent());
			
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%d|%s|%s|1,%d,%d,%d,%s|0|0|-1|-1|-1|-1|-1|\n\r", terminal, fDateTime, fDateTime, p1, p2, p3, md.substring(5)));
			
			File file = new File(config.getRetPath(), fileName);
			FileOutputStream fos = new FileOutputStream(file);
			try {
				
				fos.write(sb.toString().getBytes());
				fos.flush();
			} finally {
				fos.close();
			}

			context.updateRid(rm.getRid());
			
			MensagemVO vo = new MensagemVO();
			vo.setData(Calendar.getInstance().getTime());
			vo.setMensagem(sb.toString());
			vo.setTerminal(rm.getAdC().getContent());
			
			context.putMensagem(vo);
			
		} catch (Exception e) {
			throw new MessageHandlerException(e);
		}
	}

	private int getTerminal(String adc) throws SQLException {
		
		
		Connection c = getConnection();
		try {
			PreparedStatement ps = c.prepareStatement("SELECT num_terminal FROM tab_terminal WHERE (com_terminal = ?)");
			ps.setString(1, adc);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("num_terminal");
			}
			return 10091000;
		} finally {
			c.close();
		}
		
	}

	private Connection getConnection() throws SQLException {
		
		if (connection == null || connection.isClosed()) {
			String url = context.getConfig().getURLDatabase();
			String user = context.getConfig().getUserDatabase();
			String pass = context.getConfig().getPassDatabase();
			connection = DriverManager.getConnection(url, user, pass);
		}
		return connection;
	}

}
