package org.greatfree.cry.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ClusterConfig;
import org.greatfree.cluster.root.container.RootServiceProvider;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.multicast.root.RootClient;
import org.greatfree.cry.multicast.root.RootClient.RootClientBuilder;
import org.greatfree.cry.server.CryPeer;
import org.greatfree.cry.server.CryPeer.CryPeerBuilder;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.ClusterIPRequest;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.ClusterIPResponse;
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
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.RootAddressNotification;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

/**
 * 
 * @author libing
 * 
 * 04/24/2022
 *
 */
final class ClusterRoot
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.cluster.root");
	
	private CryPeer<ClusterRootDispatcher> root;
	private RootClient<ClusterRootDispatcher> client;
	private Map<String, String> children;
	private int replicas;
	private List<Set<String>> partitionedChildren;

	private ClusterRoot()
	{
	}
	
	private static ClusterRoot instance = new ClusterRoot();
	
	public static ClusterRoot CRY()
	{
		if (instance == null)
		{
			instance = new ClusterRoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException
	{
		this.root.stop(timeout);
		this.client.close();
		this.children.clear();
		this.children = null;
		if (this.partitionedChildren != null)
		{
			this.partitionedChildren.clear();
			this.partitionedChildren = null;
		}
	}
	
	public void init(RootClientBuilder<ClusterRootDispatcher> clientBuilder) throws NoSuchAlgorithmException, IOException
	{
		this.root = clientBuilder.getEventer();
		this.client = new RootClient<ClusterRootDispatcher>(clientBuilder);
		this.children = new ConcurrentHashMap<String, String>();
		this.replicas = 0;
		this.partitionedChildren = null;
	}

	public void init(RootClientBuilder<ClusterRootDispatcher> clientBuilder, int replicas) throws NoSuchAlgorithmException, IOException
	{
		this.root = clientBuilder.getEventer();
		this.client = new RootClient<ClusterRootDispatcher>(clientBuilder);
		this.children = new ConcurrentHashMap<String, String>();
		this.replicas = replicas;
		this.partitionedChildren = new CopyOnWriteArrayList<Set<String>>();
	}

//	public void init(PeerBuilder rootBuilder, RootClientBuilder clientBuilder) throws NoSuchAlgorithmException, IOException
	public void init(CryPeerBuilder<ClusterRootDispatcher> rootBuilder, RootClientBuilder<ClusterRootDispatcher> clientBuilder) throws NoSuchAlgorithmException, IOException
	{
		this.root = new CryPeer<ClusterRootDispatcher>(rootBuilder);
		this.client = new RootClient<ClusterRootDispatcher>(clientBuilder);
		this.children = new ConcurrentHashMap<String, String>();
		this.replicas = 0;
		this.partitionedChildren = null;
	}
	
	public void init(CryPeerBuilder<ClusterRootDispatcher> rootBuilder, RootClientBuilder<ClusterRootDispatcher> clientBuilder, int replicas) throws NoSuchAlgorithmException, IOException
	{
		this.root = new CryPeer<ClusterRootDispatcher>(rootBuilder);
		this.client = new RootClient<ClusterRootDispatcher>(clientBuilder);
		this.children = new ConcurrentHashMap<String, String>();
		this.replicas = replicas;
		this.partitionedChildren = new CopyOnWriteArrayList<Set<String>>();
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.root.start();
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.readRegistry(new ClusterIPRequest(this.root.getPeerID()));
		if (ipResponse.getIPs() != null)
		{
			for (IPAddress ip : ipResponse.getIPs().values())
			{
				this.root.addPartners(ip.getIP(), ip.getPort());
			}
			this.broadcastNotify(new RootAddressNotification(new IPAddress(this.root.getPeerID(), this.root.getPeerName(), this.root.getPeerIP(), this.root.getPort())));
		}
	}
	
	/*
	 * Partition the IP keys to implement the replication among the partitions. 09/07/2020, Bing Li
	 */
	private void partitionChild(String ipKey)
	{
		if (this.partitionedChildren.size() > 0)
		{
			int index = 0;
			boolean isFound = false;
			for (int i = 0; i < this.partitionedChildren.size(); i++)
			{
				if (this.partitionedChildren.get(i).size() < this.replicas)
				{
					index = i;
					isFound = true;
					break;
				}
			}
			if (!isFound)
			{
				Set<String> childrenKeys = Sets.newHashSet();
				childrenKeys.add(ipKey);
				this.partitionedChildren.add(childrenKeys);
			}
			else
			{
				this.partitionedChildren.get(index).add(ipKey);
			}
		}
		else
		{
			Set<String> childrenKeys = Sets.newHashSet();
			childrenKeys.add(ipKey);
			this.partitionedChildren.add(childrenKeys);
		}
	}
	
	public int getPartitionSize()
	{
		return this.partitionedChildren.size();
	}

	public void addChild(String childID, String ipKey, String ip, int port)
	{
		this.children.put(childID, ipKey);
		this.root.addPartners(ip, port);
		if (this.replicas != ClusterConfig.NO_REPLICAS)
		{
			this.partitionChild(ipKey);
		}
	}
	
	public void removeChild(String childID) throws IOException
	{
		String ipKey = this.children.get(childID);
		this.root.removePartner(ipKey);
		this.children.remove(childID);
		
		if (this.replicas != ClusterConfig.NO_REPLICAS)
		{
			// Usually, the size of the partition is NOT large. So the below algorithm is acceptable. 09/07/2020, Bing Li
			int index = 0;
			for (int i = 0; i < this.partitionedChildren.size(); i++)
			{
				if (this.partitionedChildren.get(i).contains(ipKey))
				{
					index = i;
					this.partitionedChildren.get(index).remove(ipKey);
					break;
				}
			}
			if (this.partitionedChildren.get(index).size() <= 0)
			{
				this.partitionedChildren.remove(index);
			}
		}
	}
	
	public ThreadPool getThreadPool()
	{
		return this.root.getPool();
	}
	
	public int getChildrenCount()
	{
		return this.children.size();
	}

	/*
	 * The method is invoked when the cluster plays the role of a pool. The children are selected to leave for the task cluster. 09/13/2020, Bing Li
	 */
	public Set<String> getChildrenKeys(int size)
	{
		return this.client.getChildrenKeys(size);
	}
	
	public ServerMessage readRegistry(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return this.root.read(this.root.getRegistryIP(), this.root.getRegistryPort(), request);
	}

	/*
	 * The method is more concise than the above one.03/04/2019, Bing Li
	 */
	public void saveResponse(ChildResponse response) throws InterruptedException
	{
		this.client.getRP().saveResponse(response.getResponse());
	}

	public void broadcastNotify(MulticastNotification notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.client.broadcastNotify(notification);
	}

	private void printPartition()
	{
		log.info("=====================");
		for (int i = 0; i < this.partitionedChildren.size(); i++)
		{
			for (String childKey : this.partitionedChildren.get(i))
			{
				log.info(i + ") " + childKey);
			}
		}
		log.info("=====================");
	}

	public void broadcastNotifyByPartition(MulticastNotification notification, int partitionIndex) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		log.info("ClusterRoot-broadcastNotify(): partitionIndex = " + partitionIndex);
		this.printPartition();
		this.client.broadcastNotify(notification, this.partitionedChildren.get(partitionIndex));
	}

	public void broadcastNotify(MulticastNotification notification, Set<String> childrenKeys) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.client.broadcastNotify(notification, childrenKeys);
	}

	public void broadcastNotifyWithinNChildren(MulticastNotification notification, int childrenSize) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.client.broadcastNotify(notification, childrenSize);
	}

	public void asyncBroadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncBroadcastNotify(notification);
	}
	
	public void asyncBroadcastNotify(MulticastNotification notification, int partitionIndex)
	{
		this.client.asyncBroadcastNotify(notification, this.partitionedChildren.get(partitionIndex));
	}
	
	public void anycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.client.anycastNotify(notification);
	}
	
	public void asyncAnycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncAnycastNotify(notification);
	}
	
	public void unicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.client.unicastNotify(notification);
	}
	
	public void asyncUnicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		this.client.asyncUnicastNotify(notification);
	}
	
	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		return this.client.broadcastRead(request);
	}

	public List<MulticastResponse> broadcastRead(MulticastRequest request, Set<String> childrenKeys) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		return this.client.broadcastRead(request, childrenKeys);
	}

	public MulticastResponse broadcastReadByPartition(MulticastRequest request, int partitionIndex) throws DistributedNodeFailedException, IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.printPartition();
		return this.client.broadcastReadByPartition(request, this.partitionedChildren.get(partitionIndex));
	}
	
	public List<MulticastResponse> asyncBroadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return this.client.asyncBroadcastRead(request);
	}
	
	public MulticastResponse asyncBroadcastRead(MulticastRequest request, int partitionIndex)
	{
		return this.client.asyncBroadcastReadByPartition(request, this.partitionedChildren.get(partitionIndex));
	}

	public List<MulticastResponse> anycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		return this.client.anycastRead(request, n);
	}
	
	public List<MulticastResponse> asyncAnycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return this.client.asyncAnycastRead(request, n);
	}
	
	public List<MulticastResponse> unicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		return this.client.unicastRead(request);
	}
	
	public List<MulticastResponse> asyncUnicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return this.client.asyncUnicastRead(request);
	}

	public void processNotification(ClusterNotification notification) throws IOException, DistributedNodeFailedException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		switch (notification.getNotificationType())
		{
			case MulticastMessageType.BROADCAST_NOTIFICATION:
				log.info("BROADCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (this.children.size() > 0)
				{
					log.info("unicastNearestNotify ...");
					if (notification.getChildrenKeys() != ClusterConfig.NO_CHILDREN_KEYS)
					{
						this.client.broadcastNotify(notification, notification.getChildrenKeys());
					}
					else if (notification.getPartitionIndex() != ClusterConfig.NO_PARTITION_INDEX)
					{
						this.broadcastNotifyByPartition(notification, notification.getPartitionIndex());
					}
					else
					{
						this.client.broadcastNotify(notification);
					}
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.ANYCAST_NOTIFICATION:
				log.info("ANYCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (this.children.size() > 0)
				{
					this.client.anycastNotify(notification);
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.UNICAST_NOTIFICATION:
				log.info("UNICAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (this.children.size() > 0)
				{
					if (notification.getClientKey() != null)
					{
						log.info("unicastNearestNotify ...");
						this.client.unicastNearestNotify(notification.getClientKey(), notification);
					}
					else
					{
						log.info("unicastNotify ...");
						this.client.unicastNotify(notification);
					}
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.LOCAL_NOTIFICATION:
				log.info("LOCAL_NOTIFICATION received at " + Calendar.getInstance().getTime());
				RootServiceProvider.ROOT().processNotification(notification);
				break;
		}
	}
	
	public CollectedClusterResponse processRequest(ClusterRequest request) throws DistributedNodeFailedException, IOException, ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		switch (request.getRequestType())
		{
			case MulticastMessageType.BROADCAST_REQUEST:
				log.info("BROADCAST_REQUEST received at " + Calendar.getInstance().getTime());
				if (this.children.size() > 0)
				{
					if (request.getChildrenKeys() != ClusterConfig.NO_CHILDREN_KEYS)
					{
						return new CollectedClusterResponse(MulticastMessageType.BROADCAST_RESPONSE, this.client.broadcastRead(request, request.getChildrenKeys()));
					}
					else if (request.getPartitionIndex() != ClusterConfig.NO_PARTITION_INDEX)
					{
						return new CollectedClusterResponse(MulticastMessageType.BROADCAST_RESPONSE, this.broadcastReadByPartition(request, request.getPartitionIndex()));
					}
					else
					{
						return new CollectedClusterResponse(MulticastMessageType.BROADCAST_RESPONSE, this.client.broadcastRead(request));
					}
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.ANYCAST_REQUEST:
				log.info("ANYCAST_REQUEST received at " + Calendar.getInstance().getTime());
				if (this.children.size() > 0)
				{
					return new CollectedClusterResponse(MulticastMessageType.ANYCAST_RESPONSE, this.client.anycastRead(request, ClusterConfig.ANYCAST_REQUEST_LEAST_COUNT));
				}
				else
				{
					log.info("No children join!");
				}
				break;
				
			case MulticastMessageType.UNICAST_REQUEST:
				log.info("UNICAST_REQUEST received at " + Calendar.getInstance().getTime());
				if (this.children.size() > 0)
				{
					// This key is important. Developers can set the value. So they can decide how to balance the load. For example, in the case of S3, all of the encoded data slices for the same encoding block can be sent to a unique child for merging. The client key can be the ID of the encoding block. 07/11/2020, Bing Li
					if (request.getClientKey() != null)
					{
						log.info("ClusterRoot-processRequest(): clientKey = " + request.getClientKey());
						return new CollectedClusterResponse(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastNearestRead(request.getClientKey(), request));
					}
					else
					{
						return new CollectedClusterResponse(MulticastMessageType.UNICAST_RESPONSE, this.client.unicastRead(request));
					}
				}
				else
				{
					log.info("No children join!");
				}
				break;

			case MulticastMessageType.LOCAL_REQUEST:
				log.info("LOCAL_REQUEST received at " + Calendar.getInstance().getTime());
				return RootServiceProvider.ROOT().processRequest(request);

		}
		return ClusterConfig.NO_RESPONSE;
	}
	
	public void processIntercastNotification(IntercastNotification in) throws IOException, InterruptedException
	{
		if (this.children.size() > 0)
		{
			IPAddress ip;
			Set<IPAddress> ips = Sets.newHashSet();
			Map<String, Set<String>> cds = new HashMap<String, Set<String>>();
			/*
			 * When receiving an intercast notification, the root needs to set the destination IP addresses for its children since it has all of the IP addresses of the children in the cluster. 02/28/2019, Bing Li
			 */
			if (!in.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
			{
				ip = this.root.getIPAddressByKey(this.client.getNearestChildKey(in.getDestinationKey()));
				in.setDestinationIP(ip);
				Set<String> ds = Sets.newHashSet();
				cds.put(ip.getIPKey(), ds);
				cds.get(ip.getIPKey()).add(in.getDestinationKey());
				in.setChildDestination(cds);
			}
			else
			{
				Set<String> destinationKeys = in.getDestinationKeys();
				for (String entry : destinationKeys)
				{
					ip = this.root.getIPAddressByKey(this.client.getNearestChildKey(entry));
					ips.add(ip);
					if (!cds.containsKey(ip.getIPKey()))
					{
						Set<String> ds = Sets.newHashSet();
						cds.put(ip.getIPKey(), ds);
					}
					cds.get(ip.getIPKey()).add(entry);
				}
				in.setDestinationIPs(ips);
				in.setChildDestination(cds);
			}
			
			ip = this.root.getIPAddressByKey(this.client.getNearestChildKey(in.getSourceKey()));
			this.root.syncNotify(ip.getIP(), ip.getPort(), in);
		}
		else
		{
			log.info("No children join!");
		}
	}

	public CollectedClusterResponse processIntercastRequest(IntercastRequest ir) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		if (this.children.size() > 0)
		{
			IPAddress ip;
			Set<IPAddress> ips = Sets.newHashSet();
			Map<String, Set<String>> cds = new HashMap<String, Set<String>>();
			/*
			 * When receiving an intercast notification, the root needs to set the destination IP addresses for its children since it has all of the IP addresses of the children in the cluster. 02/28/2019, Bing Li
			 */
			if (!ir.getDestinationKey().equals(UtilConfig.EMPTY_STRING))
			{
				ip = this.root.getIPAddressByKey(this.client.getNearestChildKey(ir.getDestinationKey()));
				ir.setDestinationIP(ip);
				Set<String> ds = Sets.newHashSet();
				cds.put(ip.getIPKey(), ds);
				cds.get(ip.getIPKey()).add(ir.getDestinationKey());
				ir.setChildDestination(cds);
			}
			else
			{
				Set<String> destinationKeys = ir.getDestinationKeys();
				for (String entry : destinationKeys)
				{
					ip = this.root.getIPAddressByKey(this.client.getNearestChildKey(entry));
					ips.add(ip);
					if (!cds.containsKey(ip.getIPKey()))
					{
						Set<String> ds = Sets.newHashSet();
						cds.put(ip.getIPKey(), ds);
					}
					cds.get(ip.getIPKey()).add(entry);
				}
				ir.setDestinationIPs(ips);
				ir.setChildDestination(cds);
			}
			
			IPAddress sourceIP = this.root.getIPAddressByKey(this.client.getNearestChildKey(ir.getSourceKey()));
			return (CollectedClusterResponse)this.root.read(sourceIP.getIP(), sourceIP.getPort(), ir);
		}
		else
		{
			log.info("No children join!");
		}
		return ClusterConfig.NO_RESPONSE;
	}
	
	public ChildRootResponse processRequest(ChildRootRequest request)
	{
		return RootServiceProvider.ROOT().processChildRequest(request);
	}
}
