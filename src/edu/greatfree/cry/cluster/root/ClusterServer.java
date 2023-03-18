package edu.greatfree.cry.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.Builder;

import edu.greatfree.cry.cluster.ClusterConfig;
import edu.greatfree.cry.cluster.ClusterTask;
import edu.greatfree.cry.cluster.RootTask;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.cluster.StopChatClusterNotification;
import edu.greatfree.cry.multicast.root.RootClient.RootClientBuilder;
import edu.greatfree.cry.server.CryPeer;
import edu.greatfree.cry.server.CryPeer.CryPeerBuilder;

/**
 * 
 * @author libing
 * 
 * 04/24/2022
 *
 */
// final class ClusterServer<Dispatcher extends CryptoCSDispatcher>
final class ClusterServer
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.root");

	public ClusterServer(ServerOnClusterBuilder builder) throws NoSuchAlgorithmException, IOException
	{
		CryPeerBuilder<ClusterRootDispatcher> pBuilder = new CryPeerBuilder<ClusterRootDispatcher>();
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
		pBuilder.configXML(builder.getConfigXML());
		
//		Peer p = new Peer(pBuilder);
		CryPeer<ClusterRootDispatcher> p = new CryPeer<ClusterRootDispatcher>(pBuilder);
		
		RootClientBuilder<ClusterRootDispatcher> rBuilder = new RootClientBuilder<ClusterRootDispatcher>();
		rBuilder.eventer(p);
		rBuilder.rootBranchCount(builder.getRootBranchCount());
		rBuilder.treeBranchCount(builder.getTreeBranchCount());
		rBuilder.pool(p.getPool());
		rBuilder.waitTime(builder.getRequestWaitTime());
		rBuilder.cryptoOption(builder.getCryptoOption());

		if (builder.getReplicas() == ClusterConfig.NO_REPLICAS)
		{
//			ClusterRoot.CRY().init(pBuilder, rBuilder);
			ClusterRoot.CRY().init(p, rBuilder);
		}
		else
		{
//			ClusterRoot.CRY().init(pBuilder, rBuilder, builder.getReplicas());
			ClusterRoot.CRY().init(p, rBuilder, builder.getReplicas());
		}
	}

//	public static class ServerOnClusterBuilder<ClusterRootDispatcher> implements Builder<ClusterServer<ClusterRootDispatcher>>
	public static class ServerOnClusterBuilder implements Builder<ClusterServer>
	{
		/*
		 * Most typical Peer (non-container pPeer) parameters are encapsulated. 04/24/2022, Bing Li
		 */
		private String peerName;
		private int peerPort;
		private String registryServerIP;
		private int registryServerPort;
//		private ServerTask task;
		private ClusterTask task;
		private boolean isRegistryNeeded;
		private ClusterRootDispatcher dispatcher;
		
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

		private int rootBranchCount;
		private int treeBranchCount;
		private long requestWaitTime;
		
		private int replicas;
		
		public ServerOnClusterBuilder()
		{
		}

		public ServerOnClusterBuilder peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}

		public ServerOnClusterBuilder peerPort(int peerPort)
		{
			this.peerPort = peerPort;
			return this;
		}
		
		public ServerOnClusterBuilder registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public ServerOnClusterBuilder registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
//		public ServerOnClusterBuilder<Dispatcher> task(ServerTask task)
		public ServerOnClusterBuilder task(ClusterTask task)
		{
			this.task = task;
			return this;
		}
		
		public ServerOnClusterBuilder isRegistryNeeded(boolean isRegistryNeeded)
		{
			this.isRegistryNeeded = isRegistryNeeded;
			return this;
		}
		
		public ServerOnClusterBuilder dispatcher(ClusterRootDispatcher dispatcher)
		{
			this.dispatcher = dispatcher;
			return this;
		}

		public ServerOnClusterBuilder isEncrypted(boolean isEncrypted)
		{
			this.isEncrypted = isEncrypted;
			return this;
		}

		public ServerOnClusterBuilder isAsymCryptography(boolean isAsymCryptography)
		{
			this.isAsymCryptography = isAsymCryptography;
			return this;
		}

		public ServerOnClusterBuilder symCipherAlgorithm(String symCipherAlgorithm)
		{
			this.symCipherAlgorithm = symCipherAlgorithm;
			return this;
		}

		public ServerOnClusterBuilder symCipherSpec(String symCipherSpec)
		{
			this.symCipherSpec = symCipherSpec;
			return this;
		}

		public ServerOnClusterBuilder symCipherKeyLength(int symCipherKeyLength)
		{
			this.symCipherKeyLength = symCipherKeyLength;
			return this;
		}

		public ServerOnClusterBuilder symIVKeyLength(int symIVKeyLength)
		{
			this.symIVKeyLength = symIVKeyLength;
			return this;
		}

		public ServerOnClusterBuilder asymCipherAlgorithm(String asymCipherAlgorithm)
		{
			this.asymCipherAlgorithm = asymCipherAlgorithm;
			return this;
		}
		
		public ServerOnClusterBuilder asymCipherKeyLength(int asymCipherKeyLength)
		{
			this.asymCipherKeyLength = asymCipherKeyLength;
			return this;
		}

		public ServerOnClusterBuilder signatureAlgorithm(String signatureAlgorithm)
		{
			this.signatureAlgorithm = signatureAlgorithm;
			return this;
		}
		
		public ServerOnClusterBuilder signature(String signature)
		{
			this.signature = signature;
			return this;
		}

		public ServerOnClusterBuilder isPrivate(boolean isPrivate)
		{
			this.isPrivate = isPrivate;
			return this;
		}

		public ServerOnClusterBuilder ownersSize(int ownersSize)
		{
			this.ownersSize = ownersSize;
			return this;
		}

		public ServerOnClusterBuilder configXML(String configXML)
		{
			this.configXML = configXML;
			return this;
		}

		public ServerOnClusterBuilder cryptoOption(int cryptoOption)
		{
			this.cryptoOption = cryptoOption;
			return this;
		}

		public ServerOnClusterBuilder rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}

		public ServerOnClusterBuilder treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}

		public ServerOnClusterBuilder requestWaitTime(long requestWaitTime)
		{
			this.requestWaitTime = requestWaitTime;
			return this;
		}

		public ServerOnClusterBuilder replicas(int replicas)
		{
			this.replicas = replicas;
			return this;
		}

		@Override
		public ClusterServer build() throws IOException
		{
			try
			{
				return new ClusterServer(this);
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
		
//		public ServerTask getTask()
		public ClusterTask getTask()
		{
			return this.task;
		}
		
		public boolean isRegistryNeeded()
		{
			return this.isRegistryNeeded;
		}
		
		public ClusterRootDispatcher getDispatcher()
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
		
		public int getReplicas()
		{
			return this.replicas;
		}
	}
	
	public ThreadPool getThreadPool()
	{
		return ClusterRoot.CRY().getThreadPool();
	}
	
	public boolean isChildrenEmpty()
	{
		return ClusterRoot.CRY().getChildrenCount() <= 0;
	}
	
	public void stopCluster() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		if (!this.isChildrenEmpty())
		{
			ClusterRoot.CRY().broadcastNotify(new StopChatClusterNotification());
		}
		else
		{
			log.info("No children join!");
		}
	}
	
	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		ClusterRoot.CRY().dispose(timeout);
	}

	public void start(RootTask task) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException, IPNotExistedException
	{
		RootServiceProvider.CRY().init(task);
		ClusterRoot.CRY().start();
	}
	
}
