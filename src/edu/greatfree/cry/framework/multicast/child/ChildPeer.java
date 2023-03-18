package edu.greatfree.cry.framework.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

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
import org.greatfree.framework.container.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.multicast.MultiAppConfig;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.multicast.child.ChildClient;
import edu.greatfree.cry.multicast.child.MulticastChildDispatcher;
import edu.greatfree.cry.server.CryPeer;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class ChildPeer
{
	private final static Logger log = Logger.getLogger("edu.greatfree.cry.framework.multicast.child");
	
//	private Peer peer;
	private CryPeer<MulticastChildDispatcher> peer;
	private ChildClient<MulticastChildDispatcher> client;
	// The IP address of the cluster root. 06/15/2017, Bing Li
	private IPAddress rootAddress;

	private ChildPeer()
	{
	}
	
	private static ChildPeer instance = new ChildPeer();
	
	public static ChildPeer CHILD()
	{
		if (instance == null)
		{
			instance = new ChildPeer();
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

	public void start(String rootKey, String registryIP, int registryPort, int cryptoOption) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		String peerName = Tools.generateUniqueKey();
//		this.peer = new Peer.PeerBuilder()
		this.peer = new CryPeer.CryPeerBuilder<MulticastChildDispatcher>()
				.peerName(peerName)
				.port(MultiAppConfig.DEFAULT_CHILD_PORT)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new MulticastChildTask())
				.isRegistryNeeded(true)
				.dispatcher(new MulticastChildDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.build();
		this.peer.start();

		this.peer.read(registryIP, registryPort, new ChatRegistryRequest(this.peer.getPeerID()));
		this.peer.read(registryIP, registryPort, new IsRootOnlineRequest(rootKey, this.peer.getPeerID()));
		
		this.client = new ChildClient.ChildClientBuilder<MulticastChildDispatcher>()
				.eventer(this.peer)
				.treeBranchCount(MultiAppConfig.TREE_BRANCH_COUNT)
				.localIPKey(this.peer.getLocalIPKey())
				.pool(this.peer.getPool())
				.cryptoOption(cryptoOption)
				.build();
	}
	
	public String getPeerName()
	{
		return this.peer.getPeerName();
	}

	public void setRootIP(IPAddress rootAddress) throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException, PeerNameIsNullException
	{
		log.info("rootAddress.getPeerName() = " + rootAddress.getPeerName());
		this.rootAddress = rootAddress;
		this.peer.addPeer(this.rootAddress.getPeerName(), rootAddress);
		/*
		if (!this.peer.isAsymPartnerInvited(this.rootAddress.getPeerName()))
		{
			this.peer.inviteAsymPartner(this.rootAddress.getPeerName());
		}
		*/
	}

//	public void notifyRoot(Notification response, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, ClassNotFoundException, SignatureException, RemoteReadException, InstantiationException, IllegalAccessException, SymmetricKeyUnavailableException
	public void notifyRoot(ServerMessage response, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, SignatureException, ClassNotFoundException, SymmetricKeyUnavailableException, RemoteReadException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		log.info("rootAddress.getPeerName() = " + rootAddress.getPeerName());
		/*
		if (!this.peer.isAsymPartnerInvited(this.rootAddress.getPeerName()))
		{
			this.peer.inviteAsymPartner(this.rootAddress.getPeerName());
		}
		this.peer.syncNotifyAsymmetricallyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), response);
		*/
//		this.peer.syncCryNotify(rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), response, cryptoOption);
		this.peer.syncCryPrmNotify(rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), response, cryptoOption);
	}
	
	public void broadcastNotify(PrimitiveMulticastNotification notification, int cryptoOption) throws InvalidKeyException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.asynNotify(notification);
	}

	public void broadcastRead(PrimitiveMulticastRequest request, int cryptoOption)
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.asyncRead(request);
	}
}
