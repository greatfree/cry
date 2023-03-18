package edu.greatfree.cry.framework.multicast.root;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.ClusterIPRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.IPNotExistedException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.multicast.MultiAppConfig;
import edu.greatfree.cry.framework.multicast.message.HelloWorld;
import edu.greatfree.cry.framework.multicast.message.HelloWorldAnycastNotification;
import edu.greatfree.cry.framework.multicast.message.HelloWorldAnycastRequest;
import edu.greatfree.cry.framework.multicast.message.HelloWorldAnycastResponse;
import edu.greatfree.cry.framework.multicast.message.HelloWorldBroadcastNotification;
import edu.greatfree.cry.framework.multicast.message.HelloWorldBroadcastRequest;
import edu.greatfree.cry.framework.multicast.message.HelloWorldBroadcastResponse;
import edu.greatfree.cry.framework.multicast.message.HelloWorldUnicastNotification;
import edu.greatfree.cry.framework.multicast.message.HelloWorldUnicastRequest;
import edu.greatfree.cry.framework.multicast.message.HelloWorldUnicastResponse;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.RootAddressNotification;
import edu.greatfree.cry.multicast.root.MulticastRootDispatcher;
import edu.greatfree.cry.multicast.root.RootClient;
import edu.greatfree.cry.server.CryPeer;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class RootPeer
{
//	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multicast.root");

//	private Peer peer;
	private CryPeer<MulticastRootDispatcher> peer;
	private RootClient<MulticastRootDispatcher> client;

	private RootPeer()
	{
	}
	
	private static RootPeer instance = new RootPeer();
	
	public static RootPeer ROOT()
	{
		if (instance == null)
		{
			instance = new RootPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(timeout);
		this.client.close();
	}

	public void start(String rootName, int rootPort, String registryIP, int registryPort, int cryptoOption) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException, IPNotExistedException
	{
//		this.peer = new Peer.PeerBuilder()
		this.peer = new CryPeer.CryPeerBuilder<MulticastRootDispatcher>()
				.peerName(rootName)
				.port(rootPort)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new MulticastRootTask())
				.dispatcher(new MulticastRootDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(rootName + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(false)
				.ownersSize(0)
				.build();
		this.peer.start();
		
//		log.info("Peer is started ...");

		this.client = new RootClient.RootClientBuilder<MulticastRootDispatcher>()
				.eventer(this.peer)
				.rootBranchCount(MultiAppConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MultiAppConfig.TREE_BRANCH_COUNT)
				.waitTime(MultiAppConfig.MULTICAST_WAIT_TIME)
				.pool(this.peer.getPool())
				.cryptoOption(cryptoOption)
				.build();

//		log.info("Root client is started ...");

		
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.peer.read(registryIP, registryPort, new ClusterIPRequest(this.peer.getPeerID()));
		if (ipResponse.getIPs() != null)
		{
//			log.info("RootPeer-ipResponse: ip size = " + ipResponse.getIPs().size());
			for (IPAddress ip : ipResponse.getIPs().values())
			{
//				log.info("Distributed IPs = " + ip);
//				this.peer.addPartners(ip.getPeerKey(), ip.getPeerName(), ip.getIP(), ip.getPort());
				this.peer.addPartners(ip.getIP(), ip.getPort());
				// The operation is used to keep the map between the peer name and the IP. The cryptography needs the peer name to identify each user. 04/13/2022, Bing Li
				this.peer.addPeer(ip.getPeerName(), ip);
			}
//			this.client.broadcastNotify(new RootAddressNotification(this.peer.getPeerIPAddress()));
			this.client.broadcastNotify(new RootAddressNotification(new IPAddress(this.peer.getPeerID(), this.peer.getPeerName(), this.peer.getPeerIPAddress().getIP(), this.peer.getPeerIPAddress().getPort())));
		}
	}

	public void broadcastNotify(PrimitiveMulticastNotification notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.broadcastNotify(notification);
	}

	public void broadcastNotify(HelloWorld hello, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.broadcastNotify(new HelloWorldBroadcastNotification(hello, cryptoOption));
	}

	public void anycastNotify(HelloWorld hello, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.anycastNotify(new HelloWorldAnycastNotification(hello));
	}
	
	public void unicastNotify(HelloWorld hello, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.unicastNotify(new HelloWorldUnicastNotification(hello));
	}

	public List<HelloWorldBroadcastResponse> broadcastRead(HelloWorld hello, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, DistributedNodeFailedException, IOException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.setCryptoOption(cryptoOption);
		return this.client.broadcastRead(new HelloWorldBroadcastRequest(hello, cryptoOption), HelloWorldBroadcastResponse.class);
	}
	
	public List<HelloWorldAnycastResponse> anycastRead(HelloWorld hello, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.setCryptoOption(cryptoOption);
		return this.client.anycastRead(new HelloWorldAnycastRequest(hello, cryptoOption), UtilConfig.ONE, HelloWorldAnycastResponse.class);
	}

	public List<HelloWorldUnicastResponse> unicastRead(HelloWorld hello, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, DistributedNodeFailedException, RemoteReadException, CryptographyMismatchException, InterruptedException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, RemoteIPNotExistedException, PeerNameIsNullException, IPNotExistedException
	{
		this.client.setCryptoOption(cryptoOption);
		return this.client.unicastRead(new HelloWorldUnicastRequest(hello, cryptoOption), HelloWorldUnicastResponse.class);
	}

	public void saveResponse(HelloWorldBroadcastResponse response) throws InterruptedException
	{
		this.client.getRP().saveResponse(response);
	}

	public void saveResponse(HelloWorldAnycastResponse response) throws InterruptedException
	{
		this.client.getRP().saveResponse(response);
	}
	
	public void saveResponse(HelloWorldUnicastResponse response) throws InterruptedException
	{
		this.client.getRP().saveResponse(response);
	}
}
