package org.greatfree.cry.framework.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.cluster.message.ClusterAppID;
import org.greatfree.cry.framework.cluster.message.HelloAnycastNotification;
import org.greatfree.cry.framework.cluster.message.HelloAnycastRequest;
import org.greatfree.cry.framework.cluster.message.HelloAnycastResponse;
import org.greatfree.cry.framework.cluster.message.HelloBroadcastNotification;
import org.greatfree.cry.framework.cluster.message.HelloBroadcastRequest;
import org.greatfree.cry.framework.cluster.message.HelloBroadcastResponse;
import org.greatfree.cry.framework.cluster.message.HelloUnicastNotification;
import org.greatfree.cry.framework.cluster.message.HelloUnicastRequest;
import org.greatfree.cry.framework.cluster.message.HelloUnicastResponse;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClusterChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.cluster.child");

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterAppID.HELLO_BROADCAST_NOTIFICATION:
				log.info("HELLO_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloBroadcastNotification hbn = (HelloBroadcastNotification)notification;
				log.info(hbn.getMessage());
				break;
				
			case ClusterAppID.HELLO_ANYCAST_NOTIFICATION:
				log.info("HELLO_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloAnycastNotification han = (HelloAnycastNotification)notification;
				log.info(han.getMessage());
				break;
				
			case ClusterAppID.HELLO_UNICAST_NOTIFICATION:
				log.info("HELLO_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloUnicastNotification hun = (HelloUnicastNotification)notification;
				log.info(hun.getMessage());
				break;
				
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterChild.CRY().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | IOException
						| InterruptedException | RemoteReadException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException
						| SymmetricKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public MulticastResponse processRequest(ClusterRequest request)
	{
		switch (request.getApplicationID())
		{
			case ClusterAppID.HELLO_BROADCAST_REQUEST:
				log.info("HELLO_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloBroadcastRequest hbr = (HelloBroadcastRequest)request;
				return new HelloBroadcastResponse(hbr.getMessage() + Tools.generateUniqueKey(), hbr.getCollaboratorKey());
				
			case ClusterAppID.HELLO_ANYCAST_REQUEST:
				log.info("HELLO_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloAnycastRequest har = (HelloAnycastRequest)request;
				return new HelloAnycastResponse(har.getMessage() + Tools.generateUniqueKey(), har.getCollaboratorKey());
				
			case ClusterAppID.HELLO_UNICAST_REQUEST:
				log.info("HELLO_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloUnicastRequest hur = (HelloUnicastRequest)request;
				return new HelloUnicastResponse(hur.getMessage() + Tools.generateUniqueKey(), hur.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		// TODO Auto-generated method stub
		
	}

}
