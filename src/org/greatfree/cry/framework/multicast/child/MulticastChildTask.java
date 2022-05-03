package org.greatfree.cry.framework.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.multicast.message.HelloWorld;
import org.greatfree.cry.framework.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.cry.framework.multicast.message.HelloWorldAnycastRequest;
import org.greatfree.cry.framework.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.cry.framework.multicast.message.HelloWorldBroadcastNotification;
import org.greatfree.cry.framework.multicast.message.HelloWorldBroadcastRequest;
import org.greatfree.cry.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.cry.framework.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.cry.framework.multicast.message.HelloWorldUnicastRequest;
import org.greatfree.cry.framework.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.cry.framework.multicast.message.MultiAppID;
import org.greatfree.cry.framework.multicast.message.StopChildrenNotification;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.cry.multicast.MulticastTask;
import org.greatfree.data.Constants;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.RootAddressNotification;
import org.greatfree.util.Time;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// final class MulticastChildTask implements ServerTask
final class MulticastChildTask extends MulticastTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multicast.child");

	@Override
	public void processNotification(Notification notification)
	{
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		return null;
	}

	@Override
	public void processNotification(MulticastResponse notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processNotification(MulticastNotification notification)
	{
		switch (notification.getApplicationID())
		{
			/*
			 * 
			 * The below code is useful, but the lines are commented temporarily before the revision of multicasting is done. 04/28/2022, Bing Li
			 * 
			 */
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				log.info("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				RootAddressNotification ran = (RootAddressNotification)notification;
				try
				{
					ChildPeer.CHILD().setRootIP(ran.getRootAddress());
	//				ChildPeer.CHILD().broadcastNotify(ran, MulticastConfig.ASYM);
					ChildPeer.CHILD().broadcastNotify(ran, MulticastConfig.PLAIN);
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | ClassNotFoundException | SignatureException
						| RemoteReadException | IOException | DistributedNodeFailedException
						| CryptographyMismatchException | InstantiationException | IllegalAccessException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | InterruptedException | SymmetricKeyUnavailableException | PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_BROADCAST_NOTIFICATION:
				log.info("HELLO_WORLD_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloWorldBroadcastNotification hbn = (HelloWorldBroadcastNotification)notification;
				log.info(hbn.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), hbn.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					ChildPeer.CHILD().broadcastNotify(hbn, hbn.getCryptoOption());
				}
				catch (InvalidKeyException | InstantiationException | IllegalAccessException | NoSuchAlgorithmException
						| NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
						| BadPaddingException | SignatureException | IOException | InterruptedException
						| DistributedNodeFailedException | SymmetricKeyUnavailableException | CryptographyMismatchException
						| PublicKeyUnavailableException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_ANYCAST_NOTIFICATION:
				log.info("HELLO_WORLD_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloWorldAnycastNotification han = (HelloWorldAnycastNotification)notification;
				log.info(han.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), han.getHello().getCreatedTime()) + " ms to receive the notification");
				break;
				
			case MultiAppID.HELLO_WORLD_UNICAST_NOTIFICATION:
				log.info("HELLO_WORLD_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				HelloWorldUnicastNotification hun = (HelloWorldUnicastNotification)notification;
				log.info(hun.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), hun.getHello().getCreatedTime()) + " ms to receive the notification");
				break;
				
			case MultiAppID.STOP_CHILDREN_NOTIFICATION:
				log.info("STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				StopChildrenNotification scn = (StopChildrenNotification)notification;
	//				ChildPeer.CHILD().broadcastNotify(scn, MulticastConfig.ASYM);
				try
				{
					ChildPeer.CHILD().broadcastNotify(scn, MulticastConfig.PLAIN);
					ChildPeer.CHILD().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (InvalidKeyException | InstantiationException | IllegalAccessException | NoSuchAlgorithmException
						| NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
						| BadPaddingException | SignatureException | IOException | InterruptedException
						| DistributedNodeFailedException | SymmetricKeyUnavailableException | CryptographyMismatchException
						| PublicKeyUnavailableException | ClassNotFoundException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public void processRequest(MulticastRequest request)
	{
		switch (request.getApplicationID())
		{
			case MultiAppID.HELLO_WORLD_BROADCAST_REQUEST:
				log.info("HELLO_WORLD_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloWorldBroadcastRequest hbr = (HelloWorldBroadcastRequest)request;
				log.info(hbr.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), hbr.getHello().getCreatedTime()) + " ms to receive the notification");
				ChildPeer.CHILD().broadcastRead(hbr, hbr.getCryptoOption());
	//			log.info("broadcastRead done ...");
				try
				{
					ChildPeer.CHILD().notifyRoot(new HelloWorldBroadcastResponse(new HelloWorld(Constants.BROADCAST_RESPONSE + hbr.getHello().getMessage() + " id: " + ChildPeer.CHILD().getPeerName(), Calendar.getInstance().getTime()), hbr.getCollaboratorKey()), hbr.getCryptoOption());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | ClassNotFoundException | IOException | InterruptedException
						| PublicKeyUnavailableException | DistributedNodeFailedException | CryptographyMismatchException
						| SymmetricKeyUnavailableException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_ANYCAST_REQUEST:
				log.info("HELLO_WORLD_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloWorldAnycastRequest har = (HelloWorldAnycastRequest)request;
				log.info(har.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), har.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					ChildPeer.CHILD().notifyRoot(new HelloWorldAnycastResponse(new HelloWorld(Constants.ANYCAST_RESPONSE + har.getHello().getMessage() + " id: " + ChildPeer.CHILD().getPeerName(), Calendar.getInstance().getTime()), har.getCollaboratorKey()), har.getCryptoOption());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | ClassNotFoundException | IOException | InterruptedException
						| PublicKeyUnavailableException | DistributedNodeFailedException | CryptographyMismatchException
						| SymmetricKeyUnavailableException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_UNICAST_REQUEST:
				log.info("HELLO_WORLD_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				HelloWorldUnicastRequest hur = (HelloWorldUnicastRequest)request;
				log.info(hur.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), hur.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					ChildPeer.CHILD().notifyRoot(new HelloWorldUnicastResponse(new HelloWorld(Constants.UNICAST_RESPONSE + hur.getHello().getMessage() + " id: " + ChildPeer.CHILD().getPeerName(), Calendar.getInstance().getTime()), hur.getCollaboratorKey()), hur.getCryptoOption());
				}
				catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | ClassNotFoundException | IOException | InterruptedException
						| PublicKeyUnavailableException | DistributedNodeFailedException | CryptographyMismatchException
						| SymmetricKeyUnavailableException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

}
