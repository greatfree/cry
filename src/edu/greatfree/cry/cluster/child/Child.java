package edu.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.JoinNotification;
import org.greatfree.cluster.message.LeaveNotification;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.framework.container.p2p.message.LeaveClusterNotification;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

import edu.greatfree.cry.cluster.ClusterConfig;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.cluster.ClusterCryptoOptionNotification;
import edu.greatfree.cry.messege.multicast.ChildResponse;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.messege.multicast.CollectedClusterResponse;
import edu.greatfree.cry.messege.multicast.InterChildrenNotification;
import edu.greatfree.cry.messege.multicast.InterChildrenRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;
import edu.greatfree.cry.multicast.MulticastConfig;
import edu.greatfree.cry.multicast.child.ChildClient;
import edu.greatfree.cry.multicast.child.ChildClient.ChildClientBuilder;
import edu.greatfree.cry.multicast.root.RootClient;
import edu.greatfree.cry.multicast.root.RootClient.RootClientBuilder;
import edu.greatfree.cry.server.CryPeer;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class Child
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.cluster.child");
	
	// The IP address of the cluster root. 06/15/2017, Bing Li
	private String rootKey;
	private IPAddress rootAddress;
//	private Peer child;
	
	/**
	 * Comment temporarily. 05/06/2022, Bing Li
	 */
	private CryPeer<ClusterChildDispatcher> child;
	private ChildClient<ClusterChildDispatcher> client;
	private RootClient<ClusterChildDispatcher> subRootClient;
	private AtomicInteger cryptoOption;
//	private boolean isRootPrivate;
	
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

	public void dispose(long timeout) throws InterruptedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, SignatureException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (this.child.isRegistered())
		{
			this.leaveCluster();
			if (!ServerProfile.CS().isDefault())
			{
				this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), PeerProfile.P2P().getRegistryServerIP(), PeerProfile.P2P().getRegistryServerPort(), new LeaveClusterNotification(this.rootKey, this.child.getPeerID()), MulticastConfig.PLAIN);
			}
			else
			{
//					this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), DefaultConfig.REGISTRY_ADDRESS, DefaultConfig.REGISTRY_PORT, new LeaveClusterNotification(this.rootKey, this.child.getPeerID()), MulticastConfig.PLAIN);
				this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.child.getRegistryIP(), this.child.getRegistryPort(), new LeaveClusterNotification(this.rootKey, this.child.getPeerID()), MulticastConfig.PLAIN);
			}
		}
		this.child.stop(timeout);
		this.client.close();
		this.subRootClient.close();
	}

//	public void init(Peer peer, ChildClientBuilder<ClusterChildDispatcher> cBuilder, RootClientBuilder<ClusterChildDispatcher> rBuilder) throws NoSuchAlgorithmException, IOException
	public void init(CryPeer<ClusterChildDispatcher> peer, ChildClientBuilder<ClusterChildDispatcher> cBuilder, RootClientBuilder<ClusterChildDispatcher> rBuilder, int cryptoOption) throws NoSuchAlgorithmException, IOException
	{
//		this.child = new Peer(pBuilder);
		this.child = peer;
		this.client = new ChildClient<ClusterChildDispatcher>(cBuilder);
		this.subRootClient = new RootClient<ClusterChildDispatcher>(rBuilder);
		this.cryptoOption = new AtomicInteger(cryptoOption);
	}

	public void start(String rootKey) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException, PeerNameIsNullException
	{
		this.rootKey = rootKey;
//		this.isRootPrivate = isRootPrivate;
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
//			this.child.read(DefaultConfig.REGISTRY_ADDRESS, DefaultConfig.REGISTRY_PORT, new ChatRegistryRequest(this.child.getPeerID()));
			this.child.read(this.child.getRegistryIP(), this.child.getRegistryPort(), new ChatRegistryRequest(this.child.getPeerID()));
//			response = (IsRootOnlineResponse)this.child.read(DefaultConfig.REGISTRY_ADDRESS, DefaultConfig.REGISTRY_PORT, new IsRootOnlineRequest(rootKey, this.child.getPeerID()));
			response = (IsRootOnlineResponse)this.child.read(this.child.getRegistryIP(), this.child.getRegistryPort(), new IsRootOnlineRequest(rootKey, this.child.getPeerID()));
		}
		if (response.isOnline())
		{
			this.setRootIP(response.getRootAddress());
			/*
			if (this.isRootPrivate)
			{
				if (this.child.claimOwner(this.child.getPeerName(), response.getRootAddress().getPeerName()).isSucceeded())
				{
					log.info("The child has owned the root successfully!");
					this.joinCluster();
					log.info("ClusterChild-start(): done!");
				}
				else
				{
					log.info("The child has failed to own the root such that it cannot join the cluster!");
				}
			}
			else
			{
				this.joinCluster();
				log.info("ClusterChild-start(): done!");
			}
			*/
			this.joinCluster();
			log.info("ClusterChild-start(): done!");
		}
		else
		{
			log.info("ClusterChild-start(): root is not online! ");
		}
	}
	
	public String getLocalIPKey()
	{
		return this.child.getLocalIPKey();
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
	public void setCryptoOption(int co)
	{
		this.client.setCryptoOption(co);
	}
	*/
	
	public IPAddress getRootIP()
	{
		return this.rootAddress;
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), notification, this.cryptoOption.get());
		this.child.syncCryPrmNotify(this.rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), notification, this.cryptoOption.get());
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.asyncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), notification, this.cryptoOption.get());
		this.child.asyncCryPrmNotify(this.rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), notification, this.cryptoOption.get());
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return (ChildRootResponse)this.child.read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The child is enabled to interact with the collaborator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return (ChildRootResponse)this.child.read(ip.getIP(), ip.getPort(), request);
	}

	/*
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 */
	public void joinCluster() throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, ShortBufferException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		/*
		 * A better solution is to set those clustering message as exceptions rather than the children since the message format has to be changed to involve the keys of children. 05/18/2022, Bing Li
		 * 
		 * The solution is as follows.
		 * 
		 * The private attribute of the root has exception to its children. 05/18/2022, Bing Li
		 * 
		 * One problem exists here. If the child intends to own the root, then the interactions among the child and the root are encrypted. The performance becomes slow! 05/17/2022, Bing Li
		 * 
		 */
		/*
		if (!this.isRootPrivate)
		{
			this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()), this.cryptoOption.get());
		}
		else
		{
			if (this.child.claimOwner(this.child.getPeerName(), PeerProfile.P2P().getPeerName()).isSucceeded())
			{
				this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()), this.cryptoOption.get());
			}
		}
		*/
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()), this.cryptoOption.get());
		this.child.syncCryPrmNotify(this.rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new JoinNotification(this.child.getPeerID()), this.cryptoOption.get());
	}
	
	/*
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 */
//	public void joinCluster(String ip, int port) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	public void joinCluster(IPAddress newRootIP) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), ip, port, new JoinNotification(this.child.getPeerID()), this.cryptoOption.get());
		this.child.syncCryPrmNotify(newRootIP.getPeerName(), newRootIP.getIP(), newRootIP.getPort(), new JoinNotification(this.child.getPeerID()), this.cryptoOption.get());
	}

	/*
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 */
	public void leaveCluster() throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new LeaveNotification(this.child.getPeerID()), this.cryptoOption.get());
		this.child.syncCryPrmNotify(this.rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new LeaveNotification(this.child.getPeerID()), this.cryptoOption.get());
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
	
	public IPAddress getChildAddress()
	{
		return this.child.getPeerIPAddress();
	}
	
	public void addPartnerIP(IPAddress ip)
	{
		/*
		 * The line adds the peer's name and the IP to FreeClientPool, in which the IP is removed if being idle for long enough. To retrieve the name of Peer, the line might not be required. 02/25/2023, Bing Li
		 */
		this.child.addPartners(ip.getIP(), ip.getPort());
		/*
		 * The line adds the peer's name and the IP to ServiceProvider permanently. 02/25/2023, Bing Li
		 */
		this.child.addPeer(ip.getPeerName(), ip);
		/*
		 * The RootClient and ChildClient share the same Peer. So it is unnecessary to add ip again here. 02/12/2023, Bing Li
		 */
//		this.subRootClient.addChild(ip);
	}
	
//	public void addPartnerIPs(Set<IPAddress> ips)
	public void addPartnerIPs(Collection<IPAddress> ips)
	{
		for (IPAddress entry : ips)
		{
			if (!entry.equals(this.child.getPeerIPAddress()))
			{
				/*
				 * The line adds the peer's name and the IP to FreeClientPool, in which the IP is removed if being idle for long enough. To retrieve the name of Peer, the line might not be required. 02/25/2023, Bing Li
				 */
				this.child.addPartners(entry.getIP(), entry.getPort());
				/*
				 * The line adds the peer's name and the IP to ServiceProvider permanently. 02/25/2023, Bing Li
				 */
				this.child.addPeer(entry.getPeerName(), entry);
			}
			/*
			 * The RootClient and ChildClient share the same Peer. So it is unnecessary to add ip again here. 02/12/2023, Bing Li
			 */
//			this.subRootClient.addChild(entry);
		}
	}

	/*
	public boolean claimOwner(String rootName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		return this.child.claimOwner(this.child.getPeerName(), rootName).isSucceeded();
	}
	*/

	/*
	 * Besides physically-multicasting, the method is responsible for setting cryptography options for the physically-multicasting, the logically-multicasting as well as the one-to-one interaction. 02/26/2023, Bing Li
	 */
	public void forward(ClusterNotification notification)
	{
		if (notification.getNotificationType() == MulticastMessageType.BROADCAST_NOTIFICATION)
		{
			log.info("CLUSTER_CRYPTO_OPTION_NOTIFICATION received!");
			if (notification.getClusterAppID() == CryAppID.CLUSTER_CRYPTO_OPTION_NOTIFICATION)
			{
				int cryOption = ((ClusterCryptoOptionNotification)notification).getCryptoOption();
				this.client.setCryptoOption(cryOption);
				this.subRootClient.setCryptoOption(cryOption);
				this.cryptoOption.set(cryOption);
			}
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
			/*
			 * The IP name is important for cryptography. So The below line is added. 02/12/2023, Bing Li
			 */
			if (notification.getChildrenIPs() != null)
			{
				this.addPartnerIPs(notification.getChildrenIPs().values());
			}
			this.asyncNotify(notification);
		}
	}
	
	public void forward(ClusterRequest request)
	{
		if (request.getRequestType() == MulticastMessageType.BROADCAST_REQUEST || request.getRequestType() == MulticastMessageType.INTER_BROADCAST_REQUEST)
		{
			/*
			 * The IP name is important for cryptography. So The below line is added. 02/12/2023, Bing Li
			 */
			if (request.getChildrenIPs() != null)
			{
				this.addPartnerIPs(request.getChildrenIPs().values());
			}
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
			/*
			 * The IP name is important for cryptography. So The below line is added. 02/12/2023, Bing Li
			 */
			if (request.getChildrenIPs() != null)
			{
				for (IPAddress entry : request.getChildrenIPs().values())
				{
					log.info("destination = " + entry);
				}
				this.addPartnerIPs(request.getChildrenIPs().values());
			}
			this.asyncRead(request);
		}
	}

	/*
	 * The physically-multicasting. 02/26/2023, Bing Li
	 */
	public void notify(PrimitiveMulticastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.client.notify(notification);
	}
	
	/*
	 * The physically-multicasting. 02/26/2023, Bing Li
	 */
	public void asyncNotify(PrimitiveMulticastNotification notification)
	{
		this.client.asynNotify(notification);
	}
	
	/*
	 * The physically-multicasting. 02/26/2023, Bing Li
	 */
	public void read(PrimitiveMulticastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.client.read(request);
	}
	
	/*
	 * The physically-multicasting. 02/26/2023, Bing Li
	 */
	public void asyncRead(PrimitiveMulticastRequest request)
	{
		this.client.asyncRead(request);
	}
	
	/*
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 */
	public void syncNotify(String peerName, Notification notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.syncCryNotify(peerName, notification, this.cryptoOption.get());
	}
	
	/*
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 */
	public ServerMessage read(String peerName, Request request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return this.child.cryRead(peerName, request, this.cryptoOption.get());
	}
	
	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), ip.getIP(), ip.getPort(), notification, this.cryptoOption.get());
		this.child.syncCryPrmNotify(ip.getPeerName(), ip.getIP(), ip.getPort(), notification, this.cryptoOption.get());
	}
	
	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.asyncCryPrmNotify(PeerProfile.P2P().getPeerName(), ip.getIP(), ip.getPort(), notification, this.cryptoOption.get());
		this.child.asyncCryPrmNotify(ip.getPeerName(), ip.getIP(), ip.getPort(), notification, this.cryptoOption.get());
	}
	
	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.syncCryNotify(PeerProfile.P2P().getPeerName(), ip.getIP(), ip.getPort(), notification, this.cryptoOption.get());
		this.child.syncCryNotify(ip.getPeerName(), ip.getIP(), ip.getPort(), notification, this.cryptoOption.get());
	}
	
	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.asyncCryNotify(PeerProfile.P2P().getPeerName(), ip, notification, this.cryptoOption.get());
		this.child.asyncCryNotify(ip.getPeerName(), ip, notification, this.cryptoOption.get());
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		return this.child.cryRead(PeerProfile.P2P().getPeerName(), ip.getIP(), ip.getPort(), request, this.cryptoOption.get());
		return this.child.cryRead(ip.getPeerName(), ip.getIP(), ip.getPort(), request, this.cryptoOption.get());
	}

	/*
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 */
	public ServerMessage read(IPAddress ip, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		return this.child.cryRead(PeerProfile.P2P().getPeerName(), ip.getIP(), ip.getPort(), request, this.cryptoOption.get());
		return this.child.cryRead(ip.getPeerName(), ip.getIP(), ip.getPort(), request, this.cryptoOption.get());
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The method reads from the registry server to get the IP address of any node. 09/21/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		if (!ServerProfile.CS().isDefault())
		{
			return ((PeerAddressResponse)this.child.read(PeerProfile.P2P().getRegistryServerIP(),  PeerProfile.P2P().getRegistryServerPort(), new PeerAddressRequest(nodeKey))).getPeerAddress();
		}
		else
		{
//			return ((PeerAddressResponse)this.child.read(DefaultConfig.REGISTRY_ADDRESS, DefaultConfig.REGISTRY_PORT, new PeerAddressRequest(nodeKey))).getPeerAddress();
			return ((PeerAddressResponse)this.child.read(this.child.getRegistryIP(), this.child.getRegistryPort(), new PeerAddressRequest(nodeKey))).getPeerAddress();
		}
	}

	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The method notifies the root for the ordinary responses upon one multicasting request. 03/02/2019, Bing Li
	 */
	public void notifyRoot(PrimitiveMulticastResponse response) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		/*
		 * Using the ChildResponse rather than MulticastResponse aims to identify the response from children such that those messages can be collected by the rendezvous point. 03/04/2019, Bing Li
		 */
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new ChildResponse(response), this.cryptoOption.get());
		this.child.syncCryPrmNotify(this.rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), new ChildResponse(response), this.cryptoOption.get());
	}
	
	/*
	 * 
	 * The one-to-one interaction. 02/26/2023, Bing Li
	 * 
	 * The method notifies the root for the intercasting responses. 03/02/2019, Bing Li
	 */
	public void notifyRoot(CollectedClusterResponse response) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), response, this.cryptoOption.get());
		this.child.syncCryPrmNotify(this.rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), response, this.cryptoOption.get());
	}
	
	
	/*
	 * ------------------------------ The below methods are designed for intercasting. 03/04/2019, Bing Li ------------------------------ 
	 */

	/*
	 * The below methods, notifySubRoot() & saveResponse(), are counterparts to attain the goal of multicasting-reading within a cluster. 03/14/2019, Bing Li
	 */
//	public void notifySubRoot(String ip, int port, PrimitiveMulticastResponse response) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	public void notifySubRoot(IPAddress ip, PrimitiveMulticastResponse response) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		log.info("subRoot's name = " + ip.getPeerName());
		/*
		 * Using the ChildResponse rather than MulticastResponse aims to identify the response from children such that those messages can be collected by the rendezvous point. The MulticastResponse itself has no parameters to do the same thing. 03/04/2019, Bing Li
		 */
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), ip, port, new ChildResponse(response), this.cryptoOption.get());
		this.child.syncCryPrmNotify(ip.getPeerName(), ip.getIP(), ip.getPort(), new ChildResponse(response), this.cryptoOption.get());
	}
	
	public void saveResponse(ChildResponse response) throws InterruptedException
	{
		if (response == null)
		{
			log.info("ChildResponse is NULL!");
		}
		this.subRootClient.getRP().saveResponse(response.getResponse());
	}

	/*
	 * The method is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
//	public void interUnicastNotify(IntercastNotification in) throws IOException, InterruptedException
	public void interUnicastNotify(InterChildrenNotification icn) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		String childIPKey = UtilConfig.EMPTY_STRING;
		for (Map.Entry<String, Set<String>> entry : icn.getIntercastNotification().getChildDestinations().entrySet())
		{
			childIPKey = entry.getKey();
			break;
		}
		if (!childIPKey.equals(UtilConfig.EMPTY_STRING))
		{
			/*
			 * If the destination IP is identical to the one of the local child, the message should NOT be sent. 02/19/2023, Bing Li
			 */
			if (!childIPKey.equals(this.child.getLocalIPKey()))
			{
//				this.child.syncNotify(icn.getIntercastNotification().getDestinationIP().getIP(), icn.getIntercastNotification().getDestinationIP().getPort(), icn);
				/*
				 * The below line sends encrypted messages to specified IP. 02/26/2023, Bing Li
				 */
				this.child.syncCryPrmNotify(icn.getIntercastNotification().getDestinationIP().getPeerName(), icn.getIntercastNotification().getDestinationIP().getIP(), icn.getIntercastNotification().getDestinationIP().getPort(), icn, this.cryptoOption.get());
			}
			else
			{
				/*
				 * Process the destination key which is mapped to the local child IP at the local child. 02/19/2023, Bing Li
				 */
				ChildServiceProvider.CRY().processNotification(icn);
			}
		}
		
//		this.child.syncCryPrmNotify(PeerProfile.P2P().getPeerName(), icn.getIntercastNotification().getDestinationIP().getIP(), icn.getIntercastNotification().getDestinationIP().getPort(), icn, this.cryptoOption.get());
		
	}

	/*
	 * The method is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public void interAnycastNotify(InterChildrenNotification icn) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		/*
		 * The destination keys which are mapped to the local child IP need to be processed locally. 02/19/2023, Bing Li
		 */
		if (!icn.getIntercastNotification().getChildDestinations().containsKey(this.child.getLocalIPKey()))
		{
			/*
			 * The children keys are not IP keys but user keys generated by application-level IDs. So it should be converted by the root. 02/28/2019, Bing Li
			 */
			this.subRootClient.anycastNotify(icn, icn.getIntercastNotification().getChildDestinations().keySet());
		}
		else
		{
			/*
			 * Process the destination key which is mapped to the local child IP at the local child. 02/19/2023, Bing Li
			 */
			ChildServiceProvider.CRY().processNotification(icn);
		}
	}
	
	/*
	 * The method is written in the aircraft from Zhuhai to Xi'An. 03/02/2019, Bing Li
	 */
	public void interBroadcastNotify(InterChildrenNotification icn) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		/*
		 * The destination keys which are mapped to the local child IP need to be processed locally. 02/19/2023, Bing Li
		 */
		if (!icn.getIntercastNotification().getChildDestinations().containsKey(this.child.getLocalIPKey()))
		{
			/*
			 * The children keys are not IP keys but user keys generated by application-level IDs. So it should be converted by the root. 02/28/2019, Bing Li
			 */
			this.subRootClient.broadcastNotify(icn, icn.getIntercastNotification().getChildDestinations().keySet());
		}
		else
		{
			/*
			 * Process the destination keys which are mapped to the local child IP at the local child. 02/19/2023, Bing Li
			 */
			ChildServiceProvider.CRY().processNotification(icn);
			icn.getIntercastNotification().getChildDestinations().remove(this.child.getLocalIPKey());
			this.subRootClient.broadcastNotify(icn, icn.getIntercastNotification().getChildDestinations().keySet());
		}
	}
	
	public CollectedClusterResponse interUnicastRead(InterChildrenRequest icr) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		String childIPKey = UtilConfig.EMPTY_STRING;
		for (Map.Entry<String, Set<String>> entry : icr.getIntercastRequest().getChildDestinations().entrySet())
		{
			childIPKey = entry.getKey();
			break;
		}
		if (!childIPKey.equals(UtilConfig.EMPTY_STRING))
		{
			/*
			 * If the destination IP is identical to the one of the local child, the message should NOT be sent. 02/19/2023, Bing Li
			 */
			if (!childIPKey.equals(this.child.getLocalIPKey()))
			{
				return new CollectedClusterResponse(icr.getMultiAppID(), this.subRootClient.unicastRead(icr, childIPKey));
			}
			else
			{
				/*
				 * Process the destination key which is mapped to the local child IP at the local child. 02/19/2023, Bing Li
				 */
				PrimitiveMulticastResponse pmr = ChildServiceProvider.CRY().processRequest(icr);
				if (pmr != null)
				{
					log.info("PrimitiveMulticastResponse is NOT NULL!");
				}
				else
				{
					log.info("PrimitiveMulticastResponse is NULL!");
				}
				List<PrimitiveMulticastResponse> pmrs = new ArrayList<PrimitiveMulticastResponse>();
				pmrs.add(pmr);
//				return new CollectedClusterResponse(icr.getMultiAppID(), ChildServiceProvider.CRY().processRequest(icr));
				return new CollectedClusterResponse(icr.getMultiAppID(), pmrs);
			}
		}
		return ClusterConfig.NO_RESPONSE;
	}
	
	public CollectedClusterResponse interBroadcastRead(InterChildrenRequest icr) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		/*
		 * The destination keys which are mapped to the local child IP need to be processed locally. 02/19/2023, Bing Li
		 */
		if (!icr.getIntercastRequest().getChildDestinations().containsKey(this.child.getLocalIPKey()))
		{
			log.info("The local IP is not included in the destination IPs!");
			return new CollectedClusterResponse(icr.getMultiAppID(), this.subRootClient.broadcastRead(icr, icr.getIntercastRequest().getChildDestinations().keySet()));
		}
		else
		{
			/*
			 * Process the destination keys which are mapped to the local child IP at the local child. 02/19/2023, Bing Li
			 */
			log.info("The local IP is included in the destination IPs!");
			PrimitiveMulticastResponse res = ChildServiceProvider.CRY().processRequest(icr);
			icr.getIntercastRequest().getChildDestinations().remove(this.child.getLocalIPKey());
			List<PrimitiveMulticastResponse> reses = this.subRootClient.broadcastRead(icr, icr.getIntercastRequest().getChildDestinations().keySet());
			reses.add(res);
			return new CollectedClusterResponse(icr.getMultiAppID(), reses);
		}
	}
	
	public CollectedClusterResponse interAnycastRead(InterChildrenRequest icr) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		/*
		 * The destination keys which are mapped to the local child IP need to be processed locally. 02/19/2023, Bing Li
		 */
		if (!icr.getIntercastRequest().getChildDestinations().containsKey(this.child.getLocalIPKey()))
		{
			return new CollectedClusterResponse(icr.getMultiAppID(), this.subRootClient.anycastRead(icr, icr.getIntercastRequest().getChildDestinations().keySet()));
		}
		else
		{
			/*
			 * Process the destination keys which are mapped to the local child IP at the local child. 02/19/2023, Bing Li
			 */
			PrimitiveMulticastResponse pmr = ChildServiceProvider.CRY().processRequest(icr);
			if (pmr != null)
			{
				log.info("PrimitiveMulticastResponse is NOT NULL!");
			}
			else
			{
				log.info("PrimitiveMulticastResponse is NULL!");
			}
			List<PrimitiveMulticastResponse> pmrs = new ArrayList<PrimitiveMulticastResponse>();
			pmrs.add(pmr);
//			return new CollectedClusterResponse(icr.getMultiAppID(), ChildServiceProvider.CRY().processRequest(icr));
			return new CollectedClusterResponse(icr.getMultiAppID(), pmrs);
		}
	}
}
