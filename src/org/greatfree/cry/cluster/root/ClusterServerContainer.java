package org.greatfree.cry.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 04/24/2022
 *
 */
// public final class ClusterServerContainer<Dispatcher extends CryptoCSDispatcher>
public final class ClusterServerContainer
{
	private ClusterServer<ClusterRootDispatcher> server;
	private RootTask task;
	
	public ClusterServerContainer(int port, String rootName, String registryServerIP, int registryServerPort, RootTask task, int cryptoOption) throws IOException
	{
		ClusterProfile.CLUSTER().init(registryServerIP, registryServerPort, true);

		this.server = new ClusterServer.ServerOnClusterBuilder<ClusterRootDispatcher>()
				.peerName(rootName)
				.peerPort(port)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.task(new ClusterServerTask())
				.isRegistryNeeded(true)
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

	public void stopCluster() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.server.stopCluster();
	}
	
	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException
	{
		if (!this.server.isChildrenEmpty())
		{
			TerminateSignal.SIGNAL().waitTermination(timeout);
		}
		this.server.stop(timeout);
	}
	
	public void start() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.server.start(this.task);
	}
}
