package edu.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;

import edu.greatfree.cry.cluster.ChildTask;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.multicast.child.ChildClient.ChildClientBuilder;
import edu.greatfree.cry.multicast.root.RootClient.RootClientBuilder;
import edu.greatfree.cry.server.CryPeer;
import edu.greatfree.cry.server.CryPeer.CryPeerBuilder;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
// final class ClusterChild<Dispatcher extends CryptoCSDispatcher>
final class ClusterChild
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.child");
	
	public ClusterChild(ClusterChildBuilder builder) throws NoSuchAlgorithmException, IOException
	{
		CryPeerBuilder<ClusterChildDispatcher> pBuilder = new CryPeerBuilder<ClusterChildDispatcher>();
		pBuilder.peerName(builder.getPeerName());
		pBuilder.port(builder.getPeerPort());
		pBuilder.registryServerIP(builder.getRegistryServerIP());
		pBuilder.registryServerPort(builder.getRegistryServerPort());
		pBuilder.task(builder.getTask());
		pBuilder.isRegistryNeeded(builder.isRegistryNeeded());
		pBuilder.dispatcher(builder.getDispatcher());
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
		CryPeer<ClusterChildDispatcher> p = new CryPeer<ClusterChildDispatcher>(pBuilder);

		ChildClientBuilder<ClusterChildDispatcher> cBuilder = new ChildClientBuilder<ClusterChildDispatcher>();
		cBuilder.eventer(p);
		cBuilder.treeBranchCount(builder.getTreeBranchCount());
		cBuilder.localIPKey(p.getLocalIPKey());
		cBuilder.pool(p.getPool());
		cBuilder.cryptoOption(builder.getCryptoOption());
	
		RootClientBuilder<ClusterChildDispatcher> rBuilder = new RootClientBuilder<ClusterChildDispatcher>();
		rBuilder.eventer(p);
		rBuilder.rootBranchCount(builder.getRootBranchCount());
		rBuilder.treeBranchCount(builder.getTreeBranchCount());
		rBuilder.pool(p.getPool());
		rBuilder.waitTime(builder.getRequestWaitTime());
		rBuilder.cryptoOption(builder.getCryptoOption());

//		Child.CRY().init(pBuilder, cBuilder, rBuilder);
		
		/**
		 * Comment temporarily. 05/06/2022, Bing Li
		 */
		Child.CRY().init(p, cBuilder, rBuilder, builder.getCryptoOption());
	}
	
//	public static class ClusterChildBuilder<Dispatcher extends CryptoCSDispatcher> implements Builder<ClusterChild<Dispatcher>>
	public static class ClusterChildBuilder implements Builder<ClusterChild>
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
		private ClusterChildDispatcher dispatcher;
		
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

		public ClusterChildBuilder peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}

		public ClusterChildBuilder peerPort(int peerPort)
		{
			this.peerPort = peerPort;
			return this;
		}
		
		public ClusterChildBuilder registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public ClusterChildBuilder registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
		public ClusterChildBuilder task(ServerTask task)
		{
			this.task = task;
			return this;
		}
		
		public ClusterChildBuilder isRegistryNeeded(boolean isRegistryNeeded)
		{
			this.isRegistryNeeded = isRegistryNeeded;
			return this;
		}
		
		public ClusterChildBuilder dispatcher(ClusterChildDispatcher dispatcher)
		{
//			log.info("dispatcher server key = " + dispatcher.getServerKey());
			this.dispatcher = dispatcher;
			return this;
		}

		public ClusterChildBuilder isEncrypted(boolean isEncrypted)
		{
			this.isEncrypted = isEncrypted;
			return this;
		}

		public ClusterChildBuilder isAsymCryptography(boolean isAsymCryptography)
		{
			this.isAsymCryptography = isAsymCryptography;
			return this;
		}

		public ClusterChildBuilder symCipherAlgorithm(String symCipherAlgorithm)
		{
			this.symCipherAlgorithm = symCipherAlgorithm;
			return this;
		}

		public ClusterChildBuilder symCipherSpec(String symCipherSpec)
		{
			this.symCipherSpec = symCipherSpec;
			return this;
		}

		public ClusterChildBuilder symCipherKeyLength(int symCipherKeyLength)
		{
			this.symCipherKeyLength = symCipherKeyLength;
			return this;
		}

		public ClusterChildBuilder symIVKeyLength(int symIVKeyLength)
		{
			this.symIVKeyLength = symIVKeyLength;
			return this;
		}

		public ClusterChildBuilder asymCipherAlgorithm(String asymCipherAlgorithm)
		{
			this.asymCipherAlgorithm = asymCipherAlgorithm;
			return this;
		}
		
		public ClusterChildBuilder asymCipherKeyLength(int asymCipherKeyLength)
		{
			this.asymCipherKeyLength = asymCipherKeyLength;
			return this;
		}

		public ClusterChildBuilder signatureAlgorithm(String signatureAlgorithm)
		{
			this.signatureAlgorithm = signatureAlgorithm;
			return this;
		}
		
		public ClusterChildBuilder signature(String signature)
		{
			this.signature = signature;
			return this;
		}

		public ClusterChildBuilder isPrivate(boolean isPrivate)
		{
			this.isPrivate = isPrivate;
			return this;
		}

		public ClusterChildBuilder ownersSize(int ownersSize)
		{
			this.ownersSize = ownersSize;
			return this;
		}

		public ClusterChildBuilder configXML(String configXML)
		{
			this.configXML = configXML;
			return this;
		}

		public ClusterChildBuilder cryptoOption(int cryptoOption)
		{
			this.cryptoOption = cryptoOption;
			return this;
		}

		public ClusterChildBuilder rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}

		public ClusterChildBuilder treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}

		public ClusterChildBuilder requestWaitTime(long requestWaitTime)
		{
			this.requestWaitTime = requestWaitTime;
			return this;
		}

		@Override
		public ClusterChild build() throws IOException
		{
			try
			{
				return new ClusterChild(this);
			}
			catch (NoSuchAlgorithmException e)
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
		
		public ClusterChildDispatcher getDispatcher()
		{
			return this.dispatcher;
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
	public boolean claimOwner(String rootName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		return Child.CRY().claimOwner(rootName);
	}
	*/
	
	public String getLocalIPKey()
	{
		return Child.CRY().getLocalIPKey();
	}
	
	public IPAddress getRootIP()
	{
		return Child.CRY().getRootIP();
	}

	/*
	 * The method is able to get the IP address of any node from the registry server. 09/22/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return Child.CRY().getIPAddress(nodeKey);
	}
	
	public void syncNotify(String peerName, Notification notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().syncNotify(peerName, notification);
	}
	
//	public ServerMessage read(String peerName, Request req)

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().syncNotify(ip, notification);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().syncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return Child.CRY().read(ip, request);
	}

	public ServerMessage read(IPAddress ip, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return Child.CRY().read(ip, request);
	}
	
	/*
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().syncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		Child.CRY().asyncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, DistributedNodeFailedException, IOException
	{
		return Child.CRY().readRoot(request);
	}
	
	/*
	 * The child is enabled to interact with the collabrator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return Child.CRY().readCollaborator(ip, request);
	}

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, SignatureException, RemoteIPNotExistedException, IOException, PeerNameIsNullException
	{
		Child.CRY().dispose(timeout);
	}

	public void start(String rootKey, ChildTask task) throws ClassNotFoundException, RemoteReadException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException, PeerNameIsNullException
	{
		ChildServiceProvider.CRY().init(task);
		Child.CRY().start(rootKey);
	}
}

