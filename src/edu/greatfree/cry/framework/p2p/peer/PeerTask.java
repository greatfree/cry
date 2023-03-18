package edu.greatfree.cry.framework.p2p.peer;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

import edu.greatfree.cry.framework.p2p.message.P2PAppID;
import edu.greatfree.cry.framework.p2p.message.PeerNotification;
import edu.greatfree.cry.framework.p2p.message.PeerRequest;
import edu.greatfree.cry.framework.p2p.message.PeerResponse;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
public class PeerTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.p2p.peer");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case P2PAppID.PEER_NOTIFICATION:
				log.info("PEER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PeerNotification pn = (PeerNotification)notification;
				log.info("Notification: " + pn.getGreetings());
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case P2PAppID.PEER_REQUEST:
				log.info("PEER_REQUEST received @" + Calendar.getInstance().getTime());
				PeerRequest pr = (PeerRequest)request;
				log.info("Request: " + pr.getGreetings());
				return new PeerResponse("Thanks!");
		}
		return null;
	}

}
