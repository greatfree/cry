package org.greatfree.cry.framework.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.multicast.MultiAppConfig;
import org.greatfree.cry.framework.tncs.Config;
import org.greatfree.cry.multicast.child.ChildClient;
import org.greatfree.cry.server.Peer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.message.container.Notification;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class ChildPeer
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.multicast.child");
	
	private Peer peer;
	private ChildClient client;
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

	public void stop(long timeout) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(timeout);
		this.client.close();
	}

	public void start(String rootKey, String registryIP, int registryPort, int cryptoOption) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		String peerName = Tools.generateUniqueKey();
		this.peer = new Peer.PeerBuilder()
				.peerName(peerName)
				.port(MultiAppConfig.DEFAULT_CHILD_PORT)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new MulticastChildTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(Config.RSA)
				.asymCipherKeyLength(BlockConfig.RSA_LENGTH)
				.symCipherAlgorithm(Config.AES)
				.symCipherSpec(Config.AES_SPEC)
				.symCipherKeyLength(BlockConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(BlockConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(Config.SHA_WITH_RSA)
				.signature(peerName + Config.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(false)
				.ownersSize(0)
				.build();
		this.peer.start();

		this.peer.read(registryIP, registryPort, new ChatRegistryRequest(this.peer.getPeerID()));
		this.peer.read(registryIP, registryPort, new IsRootOnlineRequest(rootKey, this.peer.getPeerID()));
		
		this.client = new ChildClient.ChildClientBuilder()
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

	public void setRootIP(IPAddress rootAddress) throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
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

//	public void notifyRoot(ServerMessage response) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException
	public void notifyRoot(Notification response, int cryptoOption) throws IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, ClassNotFoundException, SignatureException, RemoteReadException, InstantiationException, IllegalAccessException, SymmetricKeyUnavailableException
	{
		log.info("rootAddress.getPeerName() = " + rootAddress.getPeerName());
		/*
		if (!this.peer.isAsymPartnerInvited(this.rootAddress.getPeerName()))
		{
			this.peer.inviteAsymPartner(this.rootAddress.getPeerName());
		}
		this.peer.syncNotifyAsymmetricallyByIP(this.rootAddress.getIP(), this.rootAddress.getPort(), response);
		*/
		this.peer.syncCryptoNotifyByIP(rootAddress.getPeerName(), this.rootAddress.getIP(), this.rootAddress.getPort(), response, cryptoOption);
	}
	
	public void broadcastNotify(MulticastNotification notification, int cryptoOption) throws InvalidKeyException, InstantiationException, IllegalAccessException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, IOException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException, CryptographyMismatchException, PublicKeyUnavailableException
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.asynNotify(notification);
	}

	public void broadcastRead(MulticastRequest request, int cryptoOption)
	{
		this.client.setCryptoOption(cryptoOption);
		this.client.asyncRead(request);
	}
}
