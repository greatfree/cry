package org.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ChildServiceProvider;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.multicast.child.ChildClient.ChildClientBuilder;
import org.greatfree.cry.multicast.root.RootClient.RootClientBuilder;
import org.greatfree.cry.server.CryptoCSDispatcher;
import org.greatfree.cry.server.CryPeer;
import org.greatfree.cry.server.CryPeer.CryPeerBuilder;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class ClusterChild<Dispatcher extends CryptoCSDispatcher>
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.child");
	
	public ClusterChild(ClusterChildBuilder<Dispatcher> builder) throws NoSuchAlgorithmException, IOException
	{
		CryPeerBuilder<Dispatcher> pBuilder = new CryPeerBuilder<Dispatcher>();
		pBuilder.peerName(builder.getPeerName());
		pBuilder.port(builder.getPeerPort());
		pBuilder.registryServerIP(builder.getRegistryServerIP());
		pBuilder.registryServerPort(builder.getRegistryServerPort());
		pBuilder.task(builder.getTask());
		pBuilder.isRegistryNeeded(builder.isRegistryNeeded());
		pBuilder.isEncrypted(builder.isEncrypted());
		pBuilder.isAsymCryptography(builder.isAsymCryptography());
		pBuilder.symCipherAlgorithm(builder.getSymCipherAlgorithm());
		pBuilder.symCipherSpec(builder.getSymCipherSpec());
		pBuilder.symCipherKeyLength(builder.getSymCipherKeyLength());
		pBuilder.symIVKeyLength(builder.getSymIVKeyLength());
		pBuilder.asymCipherAlgorithm(builder.getAsymCipherAlgorithm());
		pBuilder.asymCipherKeyLength(builder.getAsymCipherKeyLength());
		pBuilder.signatureAlgorithm(builder.getSignatureAlgorithm());
		pBuilder.signature(builder.getSignature());
		pBuilder.isPrivate(builder.isPrivate());
		pBuilder.ownersSize(builder.getOwnersSize());

		/*
		log.info("configXML = " + builder.getConfigXML());
		if (builder.getConfigXML() != null)
		{
			log.info("configXML is NOT Null");
		}
		else
		{
			log.info("configXML is Null");
		}
		*/
		
		pBuilder.configXML(builder.getConfigXML());

//		Peer p = new Peer(pBuilder);
		CryPeer<Dispatcher> p = new CryPeer<Dispatcher>(pBuilder);

		ChildClientBuilder<Dispatcher> cBuilder = new ChildClientBuilder<Dispatcher>();
		cBuilder.eventer(p);
		cBuilder.treeBranchCount(builder.getTreeBranchCount());
		cBuilder.localIPKey(p.getLocalIPKey());
		cBuilder.pool(p.getPool());
		cBuilder.cryptoOption(builder.getCryptoOption());
	
		RootClientBuilder<Dispatcher> rBuilder = new RootClientBuilder<Dispatcher>();
		rBuilder.eventer(p);
		rBuilder.rootBranchCount(builder.getRootBranchCount());
		rBuilder.treeBranchCount(builder.getTreeBranchCount());
		rBuilder.pool(p.getPool());
		rBuilder.waitTime(builder.getRequestWaitTime());
		rBuilder.cryptoOption(builder.getCryptoOption());

//		Child.CRY().init(pBuilder, cBuilder, rBuilder);
//		Child.CRY().init(p, cBuilder, rBuilder);
	}
	
	public static class ClusterChildBuilder<Dispatcher extends CryptoCSDispatcher> implements Builder<ClusterChild<Dispatcher>>
	{
		/*
		 * Most typical Peer (non-container pPeer) parameters are encapsulated. 04/24/2022, Bing Li
		 */
		private String peerName;
		private int peerPort;
		private String registryServerIP;
		private int registryServerPort;
		private ServerTask task;
		private boolean isRegistryNeeded;
		
		private boolean isEncrypted;

		/*
		 * The below parameters regulate the approaches of cryptography. 02/04/2022, Bing Li
		 */
		private boolean isAsymCryptography;
		
		private String symCipherAlgorithm;
		private String symCipherSpec;
		private int symCipherKeyLength;
		private int symIVKeyLength;

		private String asymCipherAlgorithm;
		private int asymCipherKeyLength;
		private String signatureAlgorithm;
		private String signature;
		
		private boolean isPrivate;
		private int ownersSize;

		private String configXML;
		
		private int cryptoOption;

		// The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		private int rootBranchCount;
		private int treeBranchCount;
		// The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		private long requestWaitTime;

		public ClusterChildBuilder()
		{
		}

		public ClusterChildBuilder<Dispatcher> peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> peerPort(int peerPort)
		{
			this.peerPort = peerPort;
			return this;
		}
		
		public ClusterChildBuilder<Dispatcher> registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public ClusterChildBuilder<Dispatcher> registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
		public ClusterChildBuilder<Dispatcher> task(ServerTask task)
		{
			this.task = task;
			return this;
		}
		
		public ClusterChildBuilder<Dispatcher> isRegistryNeeded(boolean isRegistryNeeded)
		{
			this.isRegistryNeeded = isRegistryNeeded;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> isEncrypted(boolean isEncrypted)
		{
			this.isEncrypted = isEncrypted;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> isAsymCryptography(boolean isAsymCryptography)
		{
			this.isAsymCryptography = isAsymCryptography;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> symCipherAlgorithm(String symCipherAlgorithm)
		{
			this.symCipherAlgorithm = symCipherAlgorithm;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> symCipherSpec(String symCipherSpec)
		{
			this.symCipherSpec = symCipherSpec;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> symCipherKeyLength(int symCipherKeyLength)
		{
			this.symCipherKeyLength = symCipherKeyLength;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> symIVKeyLength(int symIVKeyLength)
		{
			this.symIVKeyLength = symIVKeyLength;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> asymCipherAlgorithm(String asymCipherAlgorithm)
		{
			this.asymCipherAlgorithm = asymCipherAlgorithm;
			return this;
		}
		
		public ClusterChildBuilder<Dispatcher> asymCipherKeyLength(int asymCipherKeyLength)
		{
			this.asymCipherKeyLength = asymCipherKeyLength;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> signatureAlgorithm(String signatureAlgorithm)
		{
			this.signatureAlgorithm = signatureAlgorithm;
			return this;
		}
		
		public ClusterChildBuilder<Dispatcher> signature(String signature)
		{
			this.signature = signature;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> isPrivate(boolean isPrivate)
		{
			this.isPrivate = isPrivate;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> ownersSize(int ownersSize)
		{
			this.ownersSize = ownersSize;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> configXML(String configXML)
		{
			this.configXML = configXML;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> cryptoOption(int cryptoOption)
		{
			this.cryptoOption = cryptoOption;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}

		public ClusterChildBuilder<Dispatcher> requestWaitTime(long requestWaitTime)
		{
			this.requestWaitTime = requestWaitTime;
			return this;
		}

		@Override
		public ClusterChild<Dispatcher> build() throws IOException
		{
			try
			{
				return new ClusterChild<Dispatcher>(this);
			}
			catch (NoSuchAlgorithmException | IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}


		public String getPeerName()
		{
			return this.peerName;
		}
		
		public int getPeerPort()
		{
			return this.peerPort;
		}

		public String getRegistryServerIP()
		{
			return this.registryServerIP;
		}
		
		public int getRegistryServerPort()
		{
			return this.registryServerPort;
		}
		
		public ServerTask getTask()
		{
			return this.task;
		}
		
		public boolean isRegistryNeeded()
		{
			return this.isRegistryNeeded;
		}
		
		public boolean isEncrypted()
		{
			return this.isEncrypted;
		}
		
		public boolean isAsymCryptography()
		{
			return this.isAsymCryptography;
		}
		
		public String getSymCipherAlgorithm()
		{
			return this.symCipherAlgorithm;
		}
		
		public String getSymCipherSpec()
		{
			return this.symCipherSpec;
		}
		
		public int getSymCipherKeyLength()
		{
			return this.symCipherKeyLength;
		}
		
		public int getSymIVKeyLength()
		{
			return this.symIVKeyLength;
		}
		
		public String getAsymCipherAlgorithm()
		{
			return this.asymCipherAlgorithm;
		}
		
		public int getAsymCipherKeyLength()
		{
			return this.asymCipherKeyLength;
		}
		
		public String getSignatureAlgorithm()
		{
			return this.signatureAlgorithm;
		}
		
		public String getSignature()
		{
			return this.signature;
		}
		
		public boolean isPrivate()
		{
			return this.isPrivate;
		}
		
		public int getOwnersSize()
		{
			return this.ownersSize;
		}
		
		public String getConfigXML()
		{
			return this.configXML;
		}
		
		public int getCryptoOption()
		{
			return this.cryptoOption;
		}

		public int getRootBranchCount()
		{
			return this.rootBranchCount;
		}
		
		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
		
		public long getRequestWaitTime()
		{
			return this.requestWaitTime;
		}
	}

	/*
	 * The method is able to get the IP address of any node. 09/22/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return Child.CRY().getIPAddress(nodeKey);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException
	{
		Child.CRY().syncNotify(ip, notification);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		Child.CRY().asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException
	{
		Child.CRY().syncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		Child.CRY().asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return Child.CRY().read(ip, request);
	}
	
	/*
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		Child.CRY().syncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		Child.CRY().asyncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return Child.CRY().readRoot(request);
	}
	
	/*
	 * The child is enabled to interact with the collabrator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return Child.CRY().readCollaborator(ip, request);
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		Child.CRY().dispose(timeout);
	}

	public void start(String rootKey, ChildTask task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException
	{
		ChildServiceProvider.CHILD().init(task);
		Child.CRY().start(rootKey);
	}
}

