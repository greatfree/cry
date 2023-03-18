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

import org.greatfree.cluster.message.PathResponse;
import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.cluster.ChildTask;
import edu.greatfree.cry.cluster.ClusterConfig;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.messege.multicast.ChildRootRequest;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.PathRequest;

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

//	private ClusterChild<ClusterChildDispatcher> child;
	private ClusterChild child;
	private ChildTask task;
	
	public ClusterChildContainer(String registryServerIP, int registryServerPort, ChildTask task, int cryptoOption) throws IOException
	{
		ClusterProfile.CLUSTER().init(registryServerIP, registryServerPort, true);

		String peerName = Tools.generateUniqueKey();
//		this.child = new ClusterChild.ClusterChildBuilder<ClusterChildDispatcher>()
		this.child = new ClusterChild.ClusterChildBuilder()
				.peerName(peerName)
				.peerPort(ClusterConfig.CLUSTER_CHILD_DEFAULT_PORT)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.task(new ClusterChildTask())
				.isRegistryNeeded(true)
				.dispatcher(new ClusterChildDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.isEncrypted(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(peerName + CryConfig.SIGNATURE_SUFFIX)
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
	public boolean claimOwner(String rootName) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	{
		return this.child.claimOwner(rootName);
	}
	*/
	
	public String getLocalIPKey()
	{
		return this.child.getLocalIPKey();
	}
	
	/*
	 * The method is able to get the IP address of any node. 09/22/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return this.child.getIPAddress(nodeKey);
	}
	
	public void syncNotify(String peerName, Notification notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.syncNotify(peerName, notification);
	}
	
	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.syncNotify(ip, notification);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.syncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, InterruptedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.asyncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return this.child.read(ip, request);
	}

	public ServerMessage read(IPAddress ip, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, InterruptedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return this.child.read(ip, request);
	}

	/*
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.syncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, DistributedNodeFailedException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.child.asyncNotifyRoot(notification);
	}

	/*
	 * The method is not so useful. But sometimes when testing a cluster in a single machine, it makes sense. The children needs to get independent absolute paths on the same disk. So it is necessary to synchronize the paths with the root of the cluster. 09/23/2021, Bing Li
	 */
	public String getAbsolutePath(String relativePath) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, RemoteIPNotExistedException, IOException
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
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, RemoteIPNotExistedException, IOException
	{
		return this.child.readRoot(request);
	}

	/*
	 * The child is enabled to interact with the collaborator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException, DistributedNodeFailedException, RemoteIPNotExistedException
	{
		return this.child.readCollaborator(ip, request);
	}

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, SignatureException, RemoteIPNotExistedException, IOException, PeerNameIsNullException
	{
		this.child.stop(timeout);
	}
	
//	public void start(String rootKey, boolean isRootPrivate) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException
	public void start(String rootKey) throws ClassNotFoundException, RemoteReadException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException, PeerNameIsNullException
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

	/*
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException, ShortBufferException, CheatingException, DuplicatePeerNameException
	{
		this.child.start(ClusterProfile.CLUSTER().getRootKey(), this.task);
	}
	*/
}
