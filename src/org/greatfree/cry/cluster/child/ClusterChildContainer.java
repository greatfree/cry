package org.greatfree.cry.cluster.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.PathRequest;
import org.greatfree.cluster.message.PathResponse;
import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.cry.cluster.ClusterConfig;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/24/2022
 *
 */
// public final class ClusterChildContainer<Dispatcher extends CryptoCSDispatcher>
public final class ClusterChildContainer
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.child");

	private ClusterChild<ClusterChildDispatcher> child;
	private ChildTask task;
	
	public ClusterChildContainer(String registryServerIP, int registryServerPort, ChildTask task, int cryptoOption) throws IOException
	{
		ClusterProfile.CLUSTER().init(registryServerIP, registryServerPort, true);

		String peerName = Tools.generateUniqueKey();
		this.child = new ClusterChild.ClusterChildBuilder<ClusterChildDispatcher>()
				.peerName(peerName)
				.peerPort(ClusterConfig.CLUSTER_CHILD_DEFAULT_PORT)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.task(new ClusterChildTask())
				.isRegistryNeeded(true)
				.isEncrypted(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(peerName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(false)
				.ownersSize(0)
				.cryptoOption(cryptoOption)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.build();
		
		this.task = task;
	}
	
	/*
	 * The method is able to get the IP address of any node. 09/22/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return this.child.getIPAddress(nodeKey);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException
	{
		this.child.syncNotify(ip, notification);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.child.asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException
	{
		this.child.syncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		this.child.asyncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return this.child.read(ip, request);
	}
	
	/*
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		this.child.syncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		this.child.asyncNotifyRoot(notification);
	}

	/*
	 * The method is not so useful. But sometimes when testing a cluster in a single machine, it makes sense. The children needs to get independent absolute paths on the same disk. So it is necessary to synchronize the paths with the root of the cluster. 09/23/2021, Bing Li
	 */
	public String getAbsolutePath(String relativePath) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		PathResponse pr = (PathResponse)this.readRoot(new PathRequest(relativePath));
		if (pr != null)
		{
			return pr.getAbsolutePath();
		}
		return null;
	}
	
	/*
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return this.child.readRoot(request);
	}

	/*
	 * The child is enabled to interact with the collaborator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException
	{
		return this.child.readCollaborator(ip, request);
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.child.stop(timeout);
	}
	
	public void start(String rootKey) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException
	{
		/*
		if (this.child == null)
		{
			log.info("child is NULL!");
		}
		else
		{
			log.info("child is NOT NULL!");
		}
		*/
		this.child.start(rootKey, this.task);
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.child.start(ClusterProfile.CLUSTER().getRootKey(), this.task);
	}
}
