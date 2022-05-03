package org.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.multicast.child.ChildClient;
import org.greatfree.cry.multicast.child.ChildClient.ChildClientBuilder;
import org.greatfree.cry.multicast.root.RootClient;
import org.greatfree.cry.multicast.root.RootClient.RootClientBuilder;
import org.greatfree.cry.server.Peer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.framework.container.p2p.message.LeaveClusterNotification;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageConfig;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ChildResponse;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class Child
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.child");
	
	// The IP address of the cluster root. 06/15/2017, Bing Li
	private String rootKey;
	private IPAddress rootAddress;
	private Peer child;
	private ChildClient<ClusterChildDispatcher> client;
	private RootClient<ClusterChildDispatcher> subRootClient;
	
	private Child()
	{
	}
	
	private static Child instance = new Child();
	
	public static Child CRY()
	{
		if (instance == null)
		{
			instance = new Child();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException, InterruptedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.leaveCluster();
		if (!ServerProfile.CS().isDefault())
		{
			this.child.syncNotifyByIP(PeerProfile.P2P().getRegistryServerIP(), PeerProfile.P2P().getRegistryServerPort(), new LeaveClusterNotification(this.rootKey, this.child.getPeerID()));
		}
		else
		{
			this.child.syncNotifyByIP(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new LeaveClusterNotification(this.rootKey, this.child.getPeerID()));
		}
		this.child.stop(timeout);
		this.client.close();
		this.subRootClient.close();
	}

	public void init(Peer peer, ChildClientBuilder<ClusterChildDispatcher> cBuilder, RootClientBuilder<ClusterChildDispatcher> rBuilder) throws NoSuchAlgorithmException, IOException
	{
//		this.child = new Peer(pBuilder);
		this.child = peer;
		this.client = new ChildClient<ClusterChildDispatcher>(cBuilder);
		this.subRootClient = new RootClient<ClusterChildDispatcher>(rBuilder);
	}

	public void start(String rootKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InterruptedException
	{
		this.rootKey = rootKey;
		this.child.start();
		IsRootOnlineResponse response;
		// Register the peer to the chatting registry. 06/15/2017, Bing Li
		if (!ServerProfile.CS().isDefault())
		{
			this.child.read(PeerProfile.P2P().getRegistryServerIP(), PeerProfile.P2P().getRegistryServerPort(), new ChatRegistryRequest(this.child.getPeerID()));
			response = (IsRootOnlineResponse)this.child.read(PeerProfile.P2P().getRegistryServerIP(), PeerProfile.P2P().getRegistryServerPort(), new IsRootOnlineRequest(rootKey, this.child.getPeerID()));
		}
		else
		{
			this.child.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatRegistryRequest(this.child.getPeerID()));
			response = (IsRootOnlineResponse)this.child.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new IsRootOnlineRequest(rootKey, this.child.getPeerID()));
		}
		if (response.isOnline())
		{
			this.setRootIP(response.getRootAddress());
			this.joinCluster();
			log.info("ClusterChild-start(): done!");
		}
		else
		{
			log.info("ClusterChild-start(): root is not online! ");
		}
	}

	/*
	 * If the child joins another cluster, the root key is required to be updated. 09/06/2020, Bing Li
	 */
	public void reset(String rootKey, IPAddress rootIP)
	{
		this.rootKey = rootKey;
		this.setRootIP(rootIP);
	}

	/*
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}

	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}

	/*
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return (ChildRootResponse)this.child.read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}

	/*
	 * The child is enabled to interact with the collaborator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return (ChildRootResponse)this.child.read(ip.getIP(), ip.getPort(), request);
	}

	public void joinCluster() throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()));
	}
	
	public void joinCluster(String ip, int port) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(ip, port, new JoinNotification(this.child.getPeerID()));
	}

	public void leaveCluster() throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), new LeaveNotification(this.child.getPeerID()));
	}

	/*
	 * Keep the root IP address. 05/20/2017, Bing Li
	 */
	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}
	
	public String getChildIP()
	{
		return this.child.getPeerIP();
	}

	
	public int getChildPort()
	{
		return this.child.getPort();
	}
	
	public void addPartnerIP(IPAddress ip)
	{
		this.child.addPartners(ip.getIP(), ip.getPort());
	}
	
	public void addPartnerIPs(Set<IPAddress> ips)
	{
		for (IPAddress entry : ips)
		{
			this.child.addPartners(entry.getIP(), entry.getPort());
		}
	}
	
	public void forward(ClusterNotification notification)
	{
		if (notification.getNotificationType() == MulticastMessageType.BROADCAST_NOTIFICATION)
		{
			this.asyncNotify(notification);
		}
	}

	/*
	 * The method is added to forward intercasting notifications. 04/26/2019, Bing Li
	 */
	public void forward(InterChildrenNotification notification)
	{
		if (notification.getIntercastNotification().getIntercastType() == MulticastMessageType.INTER_BROADCAST_NOTIFICATION)
		{
			this.asyncNotify(notification);
		}
	}
	
	public void forward(ClusterRequest request)
	{
		if (request.getRequestType() == MulticastMessageType.BROADCAST_REQUEST || request.getRequestType() == MulticastMessageType.INTER_BROADCAST_REQUEST)
		{
			this.asyncRead(request);
		}
	}

	/*
	 * The method is added to forward intercasting notifications. 04/26/2019, Bing Li
	 */
	public void forward(InterChildrenRequest request)
	{
		if (request.getIntercastRequest().getIntercastType() == MulticastMessageType.INTER_BROADCAST_REQUEST)
		{
			this.asyncRead(request);
		}
	}

	public void notify(MulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteReadException
	{
		this.client.notify(notification);
	}
	
	public void asyncNotify(MulticastNotification notification)
	{
		this.client.asynNotify(notification);
	}
	
	public void read(MulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteReadException
	{
		this.client.read(request);
	}
	
	public void asyncRead(MulticastRequest request)
	{
		this.client.asyncRead(request);
	}
	
	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(ip.getIP(), ip.getPort(), notification);
	}
	
	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.child.asyncNotify(ip.getIP(), ip.getPort(), notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(ip.getIP(), ip.getPort(), notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.child.asyncNotify(ip.getIP(), ip.getPort(), notification);
	}

	/*
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return this.child.read(ip.getIP(), ip.getPort(), request);
	}

	/*
	 * The method reads from the registry server to get the IP address of any node. 09/21/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		if (!ServerProfile.CS().isDefault())
		{
			return ((PeerAddressResponse)this.child.read(PeerProfile.P2P().getRegistryServerIP(),  PeerProfile.P2P().getRegistryServerPort(), new PeerAddressRequest(nodeKey))).getPeerAddress();
		}
		else
		{
			return ((PeerAddressResponse)this.child.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(nodeKey))).getPeerAddress();
		}
	}

	/*
	 * The method notifies the root for the ordinary responses upon one multicasting request. 03/02/2019, Bing Li
	 */
	public void notifyRoot(MulticastResponse response) throws IOException, InterruptedException
	{
		/*
		 * Using the ChildResponse rather than MulticastResponse aims to identify the response from children such that those messages can be collected by the rendezvous point. 03/04/2019, Bing Li
		 */
		this.child.syncNotifyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), new ChildResponse(response));
	}
	
	/*
	 * The method notifies the root for the intercasting responses. 03/02/2019, Bing Li
	 */
	public void notifyRoot(CollectedClusterResponse response) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), response);
	}
	
	
	/*
	 * ------------------------------ The below methods are designed for intercasting. 03/04/2019, Bing Li ------------------------------ 
	 */

	/*
	 * The below methods, notifySubRoot() & saveResponse(), are counterparts to attain the goal of multicasting-reading within a cluster. 03/14/2019, Bing Li
	 */
	public void notifySubRoot(String ip, int port, MulticastResponse response) throws IOException, InterruptedException
	{
		/*
		 * Using the ChildResponse rather than MulticastResponse aims to identify the response from children such that those messages can be collected by the rendezvous point. The MulticastResponse itself has no parameters to do the same thing. 03/04/2019, Bing Li
		 */
		this.child.syncNotifyByIP(ip, port, new ChildResponse(response));
	}
	
	public void saveResponse(ChildResponse response) throws InterruptedException
	{
		this.subRootClient.getRP().saveResponse(response.getResponse());
	}

	/*
	 * The method is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
//	public void interUnicastNotify(IntercastNotification in) throws IOException, InterruptedException
	public void interUnicastNotify(InterChildrenNotification icn) throws IOException, InterruptedException
	{
		this.child.syncNotifyByIP(icn.getIntercastNotification().getDestinationIP().getIP(), icn.getIntercastNotification().getDestinationIP().getPort(), icn);
	}

	/*
	 * The method is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public void interAnycastNotify(InterChildrenNotification icn) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		/*
		 * The children keys are not IP keys but user keys generated by application-level IDs. So it should be converted by the root. 02/28/2019, Bing Li
		 */
		this.subRootClient.anycastNotify(icn, icn.getIntercastNotification().getChildDestinations().keySet());
	}
	
	/*
	 * The method is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public void interBroadcastNotify(InterChildrenNotification icn) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		/*
		 * The children keys are not IP keys but user keys generated by application-level IDs. So it should be converted by the root. 02/28/2019, Bing Li
		 */
		this.subRootClient.broadcastNotify(icn, icn.getIntercastNotification().getChildDestinations().keySet());
	}
	
	public CollectedClusterResponse interUnicastRead(InterChildrenRequest icr) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		String childIPKey = UtilConfig.EMPTY_STRING;
		for (Map.Entry<String, Set<String>> entry : icr.getIntercastRequest().getChildDestinations().entrySet())
		{
			childIPKey = entry.getKey();
			break;
		}
		if (!childIPKey.equals(UtilConfig.EMPTY_STRING))
		{
			return new CollectedClusterResponse(icr.getApplicationID(), this.subRootClient.unicastRead(icr, childIPKey));
		}
		return SystemMessageConfig.NO_RESPONSE;
	}
	
	public CollectedClusterResponse interBroadcastRead(InterChildrenRequest icr) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		return new CollectedClusterResponse(icr.getApplicationID(), this.subRootClient.broadcastRead(icr, icr.getIntercastRequest().getChildDestinations().keySet()));
	}
	
	public CollectedClusterResponse interAnycastRead(InterChildrenRequest icr) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		return new CollectedClusterResponse(icr.getApplicationID(), this.subRootClient.anycastRead(icr, icr.getIntercastRequest().getChildDestinations().keySet()));
	}
	
}
