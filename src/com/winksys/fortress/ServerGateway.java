package com.winksys.fortress;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerGateway implements IServerGateway {

	private static final Logger LOG = LogManager.getLogger(ServerGateway.class);
	
	private ServerContext serverContext;

	public ServerGateway(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public String sendMessage(String xml) {
		IConfig config = serverContext.getConfig();
		String url1 = config.getServerURL1();
		
		try {
			LOG.debug("Sent: "+ xml);
			HttpPost httpPost = new HttpPost(url1);
			httpPost.setEntity(new StringEntity(xml));
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			String ret = EntityUtils.toString(response.getEntity());
			LOG.debug("Response: "+ ret);
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
