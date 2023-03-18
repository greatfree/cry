package edu.greatfree.cry.framework.cs.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

import edu.greatfree.cry.framework.cs.message.CSAppID;
import edu.greatfree.cry.framework.cs.message.CSNotification;
import edu.greatfree.cry.framework.cs.message.CSRequest;
import edu.greatfree.cry.framework.cs.message.CSResponse;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
class CSTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.tncs.server");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CSAppID.CS_NOTIFICATION:
				log.info("CS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CSNotification csn = (CSNotification)notification;
				log.info("Received Notification: " + csn.getMessage());
				break;
				
			case CSAppID.SHUTDOWN_NOTIFICATION:
				log.info("SHUTDOWN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CryptoServer.CRY().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case CSAppID.CS_REQUEST:
				log.info("CS_REQUEST received @" + Calendar.getInstance().getTime());
				CSRequest csr = (CSRequest)request;
				log.info("Received Request: " + csr.getMessage());
				log.info("Response: " + csr.getMessage());
				return new CSResponse(csr.getMessage());
		}
		return null;
	}

}
