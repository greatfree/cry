package edu.greatfree.cry.framework.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.util.Time;

import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.multicast.message.ClientAnycastNotification;
import edu.greatfree.cry.framework.multicast.message.ClientAnycastRequest;
import edu.greatfree.cry.framework.multicast.message.ClientAnycastResponse;
import edu.greatfree.cry.framework.multicast.message.ClientBroadcastNotification;
import edu.greatfree.cry.framework.multicast.message.ClientBroadcastRequest;
import edu.greatfree.cry.framework.multicast.message.ClientBroadcastResponse;
import edu.greatfree.cry.framework.multicast.message.ClientUnicastNotification;
import edu.greatfree.cry.framework.multicast.message.ClientUnicastRequest;
import edu.greatfree.cry.framework.multicast.message.ClientUnicastResponse;
import edu.greatfree.cry.framework.multicast.message.HelloWorldAnycastResponse;
import edu.greatfree.cry.framework.multicast.message.HelloWorldBroadcastResponse;
import edu.greatfree.cry.framework.multicast.message.HelloWorldUnicastResponse;
import edu.greatfree.cry.framework.multicast.message.MultiAppID;
import edu.greatfree.cry.framework.multicast.message.StopChildrenNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.multicast.MulticastTask;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// final class MulticastRootTask implements ServerTask
//final class MulticastRootTask implements RootTask
final class MulticastRootTask extends MulticastTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multicast.root");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case MultiAppID.CLIENT_BROADCAST_NOTIFICATION:
				log.info("CLIENT_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ClientBroadcastNotification hbn = (ClientBroadcastNotification)notification;
				log.info(hbn.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), hbn.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					RootPeer.ROOT().broadcastNotify(hbn.getHello(), hbn.getCryptoOption());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.CLIENT_ANYCAST_NOTIFICATION:
				log.info("CLIENT_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ClientAnycastNotification han = (ClientAnycastNotification)notification;
				log.info(han.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), han.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					RootPeer.ROOT().anycastNotify(han.getHello(), han.getCryptoOption());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.CLIENT_UNICAST_NOTIFICATION:
				log.info("CLIENT_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ClientUnicastNotification cun = (ClientUnicastNotification)notification;
				log.info(cun.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), cun.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					RootPeer.ROOT().unicastNotify(cun.getHello(), cun.getCryptoOption());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;

				/*
				 * 
				 * The below code is useful, but the lines are commented temporarily before the revision of multicasting is done. 04/28/2022, Bing Li
				 * 
				 */
				/*
			case MultiAppID.HELLO_WORLD_BROADCAST_RESPONSE:
				log.info("HELLO_WORLD_BROADCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().saveResponse((HelloWorldBroadcastResponse)notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_ANYCAST_RESPONSE:
				log.info("HELLO_WORLD_ANYCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().saveResponse((HelloWorldAnycastResponse)notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_UNICAST_RESPONSE:
				log.info("HELLO_WORLD_UNICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().saveResponse((HelloWorldUnicastResponse)notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				*/
				
			case MultiAppID.ADMIN_STOP_CHILDREN_NOTIFICATION:
				log.info("ADMIN_STOP_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().broadcastNotify(new StopChildrenNotification());
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.ADMIN_STOP_ROOT_NOTIFICATION:
				log.info("ADMIN_STOP_ROOT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException
						| InterruptedException | RemoteReadException | CryptographyMismatchException
						| DistributedNodeFailedException | PublicKeyUnavailableException
						| SymmetricKeyUnavailableException | IOException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
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
			case MultiAppID.CLIENT_BROADCAST_REQUEST:
				log.info("CLIENT_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				ClientBroadcastRequest cbr = (ClientBroadcastRequest)request;
				log.info(cbr.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), cbr.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					List<HelloWorldBroadcastResponse> df = RootPeer.ROOT().broadcastRead(cbr.getHello(), cbr.getCryptoOption());
					log.info("responses size = " + df.size());
//					return new ClientBroadcastResponse(RootPeer.ROOT().broadcastRead(cbr.getHello()));
					/*
					int index = 0;
					for (HelloWorldBroadcastResponse response : df)
					{
						System.out.println(++index + ") response = " + response.getHello().getMessage());
					}
					*/
					return new ClientBroadcastResponse(df);
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | DistributedNodeFailedException | IOException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
				
			case MultiAppID.CLIENT_ANYCAST_REQUEST:
				log.info("CLIENT_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				ClientAnycastRequest car = (ClientAnycastRequest)request;
				log.info(car.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), car.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					return new ClientAnycastResponse(RootPeer.ROOT().anycastRead(car.getHello(), car.getCryptoOption()));
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
				
			case MultiAppID.CLIENT_UNICAST_REQUEST:
				log.info("CLIENT_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				ClientUnicastRequest cur = (ClientUnicastRequest)request;
				log.info(cur.getHello().getMessage());
				log.info("It takes " + Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), cur.getHello().getCreatedTime()) + " ms to receive the notification");
				try
				{
					return new ClientUnicastResponse(RootPeer.ROOT().unicastRead(cur.getHello(), cur.getCryptoOption()));
				}
				catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
						| SignatureException | IOException | DistributedNodeFailedException | RemoteReadException
						| CryptographyMismatchException | InterruptedException | SymmetricKeyUnavailableException
						| PublicKeyUnavailableException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				catch (IPNotExistedException e)
				{
					e.printStackTrace();
				}
		}
		return null;
	}

	@Override
	public void processNotification(PrimitiveMulticastResponse notification)
	{
//		switch (notification.getType())
		switch (notification.getApplicationID())
		{
			case MultiAppID.HELLO_WORLD_BROADCAST_RESPONSE:
				log.info("HELLO_WORLD_BROADCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().saveResponse((HelloWorldBroadcastResponse)notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_ANYCAST_RESPONSE:
				log.info("HELLO_WORLD_ANYCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().saveResponse((HelloWorldAnycastResponse)notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case MultiAppID.HELLO_WORLD_UNICAST_RESPONSE:
				log.info("HELLO_WORLD_UNICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				try
				{
					RootPeer.ROOT().saveResponse((HelloWorldUnicastResponse)notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
//	public void processNotification(MulticastNotification notification)
	public void processNotification(PrimitiveMulticastNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
//	public ServerMessage processRequest(MulticastRequest request)
	public ServerMessage processRequest(PrimitiveMulticastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	@Override
	public void processNotification(ServerMessage notification)
	{
		
		
	}

	@Override
	public ServerMessage processRequest(ServerMessage request)
	{
		// TODO Auto-generated method stub
		return null;
	}
	*/
}
