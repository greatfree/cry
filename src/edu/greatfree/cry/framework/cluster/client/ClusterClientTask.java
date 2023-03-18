package edu.greatfree.cry.framework.cluster.client;

import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

import edu.greatfree.cry.framework.cluster.message.PeerCryptoOptionNotification;
import edu.greatfree.cry.messege.CryAppID;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClusterClientTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.framework.cluster.client");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CryAppID.PEER_CRYPTO_OPTION_NOTIFICATION:
				log.info("PEER_CRYPTO_OPTION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PeerCryptoOptionNotification pcon = (PeerCryptoOptionNotification)notification;
				ClusterUI.CRY().setCryptoOption(pcon.getCryptoOption());
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
