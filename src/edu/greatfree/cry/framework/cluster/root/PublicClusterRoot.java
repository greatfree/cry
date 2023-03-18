package edu.greatfree.cry.framework.cluster.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cry.cluster.RootTask;
import edu.greatfree.cry.cluster.root.ClusterServerContainer;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;

/**
 * 
 * @author libing
 * 
 * 04/26/2022
 *
 */
final class PublicClusterRoot
{
	private ClusterServerContainer server;

	private PublicClusterRoot()
	{
	}
	
	private static PublicClusterRoot instance = new PublicClusterRoot();
	
	public static PublicClusterRoot CRY()
	{
		if (instance == null)
		{
			instance = new PublicClusterRoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stopCluster() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.server.stopCluster();
	}
	
	public void stopServer(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int port, String rootName, String registryIP, int registryPort, RootTask task, int cryptoOption) throws IOException, InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException, IPNotExistedException
	{
//		this.server = new ClusterServerContainer(port, rootName, registryIP, registryPort, task, cryptoOption);
		this.server = new ClusterServerContainer.ClusterServerContainerBuilder()
				.rootName(rootName)
				.rootPort(port)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.rootTask(task)
				.isPrivate(false)
				.ownerSize(0)
				.cryptoOption(cryptoOption)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.subBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.broadcastWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.replicas(0)
				.build();
		this.server.start();
	}
}
