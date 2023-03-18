package edu.greatfree.cry.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.Builder;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.cluster.RootTask;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;

/**
 * 
 * @author libing
 * 
 * 04/24/2022
 *
 */
// public final class ClusterServerContainer<Dispatcher extends CryptoCSDispatcher>
public class ClusterServerContainer
{
//	private ClusterServer<ClusterRootDispatcher> server;
	private ClusterServer server;
	private RootTask task;
//	private ClusterTask task;

	/*
	public ClusterServerContainer(int port, String rootName, String registryServerIP, int registryServerPort, RootTask task, int cryptoOption) throws IOException
	{
		ClusterProfile.CLUSTER().init(registryServerIP, registryServerPort, true);

//		this.server = new ClusterServer.ServerOnClusterBuilder<ClusterRootDispatcher>()
		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerName(rootName)
				.peerPort(port)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.task(new ClusterRootTask())
				.isRegistryNeeded(true)
				.dispatcher(new ClusterRootDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.isEncrypted(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(rootName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(false)
				.ownersSize(0)
				.cryptoOption(cryptoOption)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.replicas(0)
				.build();

		this.task = task;
	}
	*/
	
	public ClusterServerContainer(ClusterServerContainerBuilder builder) throws IOException
	{
		ClusterProfile.CLUSTER().init(builder.getRegistryServerIP(), builder.registryServerPort, true);
		
		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerName(builder.getRootName())
				.peerPort(builder.getRootPort())
				.registryServerIP(builder.getRegistryServerIP())
				.registryServerPort(builder.getRegistryServerPort())
				.task(new ClusterRootTask())
				.isRegistryNeeded(true)
				.dispatcher(new ClusterRootDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.isEncrypted(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(builder.getRootName() + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(builder.isPrivate())
				.ownersSize(builder.getOwnerSize())
				.cryptoOption(builder.getCryptoOption())
				.rootBranchCount(builder.getRootBranchCount())
				.treeBranchCount(builder.getSubBranchCount())
				.requestWaitTime(builder.getBroadcastWaitTime())
				.replicas(builder.getReplicas())
				.build();

		this.task = builder.getRootTask();
	}
	
	public static class ClusterServerContainerBuilder implements Builder<ClusterServerContainer>
	{
		private String rootName;
		private int rootPort;
		private String registryServerIP;
		private int registryServerPort;
		private RootTask rootTask;
		private boolean isPrivate;
		private int ownerSize;
		private int cryptoOption;
		private int rootBranchCount;
		private int subBranchCount;
		private long broadcastWaitTime;
		private int replicas;
		
		public ClusterServerContainerBuilder()
		{
		}
		
		public ClusterServerContainerBuilder rootName(String rootName)
		{
			this.rootName = rootName;
			return this;
		}
		
		public ClusterServerContainerBuilder rootPort(int rootPort)
		{
			this.rootPort = rootPort;
			return this;
		}
		
		public ClusterServerContainerBuilder registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public ClusterServerContainerBuilder registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
		public ClusterServerContainerBuilder rootTask(RootTask rootTask)
		{
			this.rootTask = rootTask;
			return this;
		}
		
		public ClusterServerContainerBuilder isPrivate(boolean isPrivate)
		{
			this.isPrivate = isPrivate;
			return this;
		}
		
		public ClusterServerContainerBuilder ownerSize(int ownerSize)
		{
			this.ownerSize = ownerSize;
			return this;
		}
		
		public ClusterServerContainerBuilder cryptoOption(int cryptoOption)
		{
			this.cryptoOption = cryptoOption;
			return this;
		}
		
		public ClusterServerContainerBuilder rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}
		
		public ClusterServerContainerBuilder subBranchCount(int subBranchCount)
		{
			this.subBranchCount = subBranchCount;
			return this;
		}
		
		public ClusterServerContainerBuilder broadcastWaitTime(long broadcastWaitTime)
		{
			this.broadcastWaitTime = broadcastWaitTime;
			return this;
		}
		
		public ClusterServerContainerBuilder replicas(int replicas)
		{
			this.replicas = replicas;
			return this;
		}

		@Override
		public ClusterServerContainer build() throws IOException
		{
			return new ClusterServerContainer(this);
		}
		
		public String getRootName()
		{
			return this.rootName;
		}
		
		public int getRootPort()
		{
			return this.rootPort;
		}
		
		public String getRegistryServerIP()
		{
			return this.registryServerIP;
		}
		
		public int getRegistryServerPort()
		{
			return this.registryServerPort;
		}
		
		public RootTask getRootTask()
		{
			return this.rootTask;
		}
		
		public boolean isPrivate()
		{
			return this.isPrivate;
		}
		
		public int getOwnerSize()
		{
			return this.ownerSize;
		}
		
		public int getCryptoOption()
		{
			return this.cryptoOption;
		}
		
		public int getRootBranchCount()
		{
			return this.rootBranchCount;
		}
		
		public int getSubBranchCount()
		{
			return this.subBranchCount;
		}
		
		public long getBroadcastWaitTime()
		{
			return this.broadcastWaitTime;
		}
		
		public int getReplicas()
		{
			return this.replicas;
		}
	}

	public void stopCluster() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.server.stopCluster();
	}
	
	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		if (!this.server.isChildrenEmpty())
		{
			TerminateSignal.SIGNAL().waitTermination(timeout);
		}
		this.server.stop(timeout);
	}
	
	public void start() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException, IPNotExistedException
	{
		this.server.start(this.task);
	}
}
