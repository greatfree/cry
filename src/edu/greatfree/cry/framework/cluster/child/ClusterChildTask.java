package edu.greatfree.cry.framework.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import edu.greatfree.cry.cluster.ChildTask;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.messege.multicast.InterChildrenNotification;
import edu.greatfree.cry.messege.multicast.InterChildrenRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.Tools;

import edu.greatfree.cry.framework.cluster.message.ClusterAppID;
import edu.greatfree.cry.framework.cluster.message.HelloAnycastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloAnycastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloAnycastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloBroadcastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloBroadcastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloBroadcastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloInterAnycastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloInterAnycastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloInterAnycastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloInterBroadcastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloInterBroadcastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloInterBroadcastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloInterUnicastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloInterUnicastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloInterUnicastResponse;
import edu.greatfree.cry.framework.cluster.message.HelloUnicastNotification;
import edu.greatfree.cry.framework.cluster.message.HelloUnicastRequest;
import edu.greatfree.cry.framework.cluster.message.HelloUnicastResponse;
import edu.greatfree.cry.framework.cluster.message.InterChildrenHelloAnyNotification;
import edu.greatfree.cry.framework.cluster.message.InterChildrenHelloAnyRequest;
import edu.greatfree.cry.framework.cluster.message.InterChildrenHelloBroadNotification;
import edu.greatfree.cry.framework.cluster.message.InterChildrenHelloBroadRequest;
import edu.greatfree.cry.framework.cluster.message.InterChildrenHelloUniNotification;
import edu.greatfree.cry.framework.cluster.message.InterChildrenHelloUniRequest;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClusterChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.framework.cluster.child");

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterAppID.HELLO_INTER_BROADCAST_NOTIFICATION:
				log.info("HELLO_INTER_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloInterBroadcastNotification hibn = (HelloInterBroadcastNotification)notification;
				log.info(hibn.getMessage());
				return new InterChildrenHelloBroadNotification(hibn, "I am crazy at all!");

			case ClusterAppID.HELLO_INTER_UNICAST_NOTIFICATION:
				log.info("HELLO_INTER_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloInterUnicastNotification hiun = (HelloInterUnicastNotification)notification;
				log.info(hiun.getMessage());
				return new InterChildrenHelloUniNotification(hiun, "Please let me in");
				
			case ClusterAppID.HELLO_INTER_ANYCAST_NOTIFICATION:
				log.info("HELLO_INTER_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloInterAnycastNotification hian = (HelloInterAnycastNotification)notification;
				log.info(hian.getMessage());
				return new InterChildrenHelloAnyNotification(hian, "How to avoid nucleic acid?");
		}
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		switch (request.getApplicationID())
		{
			case ClusterAppID.HELLO_INTER_UNICAST_REQUEST:
				log.info("HELLO_INTER_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloInterUnicastRequest hiur = (HelloInterUnicastRequest)request;
				log.info(hiur.getMessage());
				return new InterChildrenHelloUniRequest(hiur, "Do not waste time any more");
				
			case ClusterAppID.HELLO_INTER_ANYCAST_REQUEST:
				log.info("HELLO_INTER_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloInterAnycastRequest hiar = (HelloInterAnycastRequest)request;
				log.info(hiar.getMessage());
				return new InterChildrenHelloAnyRequest(hiar, "I got angry!");
				
			case ClusterAppID.HELLO_INTER_BROADCAST_REQUEST:
				log.info("HELLO_INTER_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloInterBroadcastRequest hibr = (HelloInterBroadcastRequest)request;
				log.info(hibr.getMessage());
				return new InterChildrenHelloBroadRequest(hibr, "The university is shit!");
		}
		return null;
	}

	@Override
	public void processNotification(ClusterNotification notification)
	{
		int size = 0;
		switch (notification.getClusterAppID())
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

				/*
				 * The below case might not be correct to be placed here since they are not ClusterNotification. 03/10/2023, Bing Li
				 */
			case ClusterAppID.HELLO_INTER_UNICAST_NOTIFICATION:
				log.info("HELLO_INTER_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				InterChildrenHelloUniNotification ichun = (InterChildrenHelloUniNotification)notification;
				log.info(ichun.getAdditionalMessage());
				HelloInterUnicastNotification hiun = (HelloInterUnicastNotification)ichun.getIntercastNotification();
				
				log.info(hiun.getMessage());
				break;
				
				/*
				 * The below case might not be correct to be placed here since they are not ClusterNotification. 03/10/2023, Bing Li
				 */
			case ClusterAppID.HELLO_INTER_BROADCAST_NOTIFICATION:
				log.info("HELLO_INTER_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				InterChildrenHelloBroadNotification ichbn = (InterChildrenHelloBroadNotification)notification;
				log.info(ichbn.getAdditionalMessage());
				HelloInterBroadcastNotification hibn = (HelloInterBroadcastNotification)ichbn.getIntercastNotification();
				/*
				 * One child is usually mapped to multiple application-level destinations. The size is the count of the destinations. 06/21/2022, Bing Li
				 */
				size = hibn.getChildDestinations().get(ClusterChildOfPublicRoot.CRY().getLocalIPKey()).size();
				for (int i = 0; i < size; i++)
				{
					log.info(hibn.getMessage());
				}
				break;
				
				/*
				 * The below case might not be correct to be placed here since they are not ClusterNotification. 03/10/2023, Bing Li
				 */
			case ClusterAppID.HELLO_INTER_ANYCAST_NOTIFICATION:
				log.info("HELLO_INTER_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				InterChildrenHelloAnyNotification ichan = (InterChildrenHelloAnyNotification)notification;
				log.info(ichan.getAdditionalMessage());
				HelloInterAnycastNotification hian = (HelloInterAnycastNotification)ichan.getIntercastNotification();
				/*
				 * One child is usually mapped to multiple application-level destinations. The size is the count of the destinations. 06/21/2022, Bing Li
				 */
				size = hian.getChildDestinations().get(ClusterChildOfPublicRoot.CRY().getLocalIPKey()).size();
				for (int i = 0; i < size; i++)
				{
					log.info(hian.getMessage());
				}
				break;
				
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ClusterChildOfPublicRoot.CRY().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | IOException
						| InterruptedException | RemoteReadException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException
						| SymmetricKeyUnavailableException | SignatureException e)
				{
					e.printStackTrace();
				}
				catch (RemoteIPNotExistedException e)
				{
					log.info(e.toString());
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public PrimitiveMulticastResponse processRequest(ClusterRequest request)
	{
		switch (request.getClusterAppID())
		{
			case ClusterAppID.HELLO_BROADCAST_REQUEST:
				log.info("HELLO_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloBroadcastRequest hbr = (HelloBroadcastRequest)request;
				log.info(hbr.getMessage());
				return new HelloBroadcastResponse(hbr.getMessage() + Tools.generateUniqueKey(), hbr.getCollaboratorKey());
				
			case ClusterAppID.HELLO_ANYCAST_REQUEST:
				log.info("HELLO_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloAnycastRequest har = (HelloAnycastRequest)request;
				log.info(har.getMessage());
				return new HelloAnycastResponse(har.getMessage() + Tools.generateUniqueKey(), har.getCollaboratorKey());
				
			case ClusterAppID.HELLO_UNICAST_REQUEST:
				log.info("HELLO_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloUnicastRequest hur = (HelloUnicastRequest)request;
				log.info(hur.getMessage());
				return new HelloUnicastResponse(hur.getMessage() + Tools.generateUniqueKey(), hur.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public PrimitiveMulticastResponse processRequest(InterChildrenRequest request)
	{
		int size;
		List<String> msgs;
		switch (request.getClusterAppID())
		{
			case ClusterAppID.HELLO_INTER_UNICAST_REQUEST:
				log.info("HELLO_INTER_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				InterChildrenHelloUniRequest ichur = (InterChildrenHelloUniRequest)request;
				log.info(ichur.getAdditionalMessage());
				HelloInterUnicastRequest hiur = (HelloInterUnicastRequest)ichur.getIntercastRequest();
				log.info(hiur.getMessage());
				return new HelloInterUnicastResponse(hiur.getMessage() + Tools.generateUniqueKey(), request.getCollaboratorKey());
				
			case ClusterAppID.HELLO_INTER_ANYCAST_REQUEST:
				log.info("HELLO_INTER_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				InterChildrenHelloAnyRequest ichar = (InterChildrenHelloAnyRequest)request;
				log.info(ichar.getAdditionalMessage());
				HelloInterAnycastRequest hiar = (HelloInterAnycastRequest)ichar.getIntercastRequest();
				/*
				 * One child is usually mapped to multiple application-level destinations. The size is the count of the destinations. 06/21/2022, Bing Li
				 */
				size = hiar.getChildDestinations().get(ClusterChildOfPublicRoot.CRY().getLocalIPKey()).size();
				for (int i = 0; i < size; i++)
				{
					log.info(hiar.getMessage());
				}
				msgs = new ArrayList<String>();
				for (int i = 0; i < size; i++)
				{
					msgs.add(hiar.getMessage() + Tools.generateUniqueKey());
				}
				return new HelloInterAnycastResponse(msgs, request.getCollaboratorKey());
				
			case ClusterAppID.HELLO_INTER_BROADCAST_REQUEST:
				log.info("HELLO_INTER_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				InterChildrenHelloBroadRequest ichbr = (InterChildrenHelloBroadRequest)request;
				log.info(ichbr.getAdditionalMessage());
				HelloInterBroadcastRequest hibr = (HelloInterBroadcastRequest)ichbr.getIntercastRequest();
				log.info(hibr.getMessage());
				/*
				 * One child is usually mapped to multiple application-level destinations. The size is the count of the destinations. 06/21/2022, Bing Li
				 */
				size = hibr.getChildDestinations().get(ClusterChildOfPublicRoot.CRY().getLocalIPKey()).size();
				for (int i = 0; i < size; i++)
				{
					log.info(hibr.getMessage());
				}
				msgs = new ArrayList<String>();
				for (int i = 0; i < size; i++)
				{
					msgs.add(hibr.getMessage() + Tools.generateUniqueKey());
				}
				return new HelloInterBroadcastResponse(msgs, request.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		// TODO Auto-generated method stub
		
	}

}
