package org.greatfree.cry.framework.multisigned.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.multisigned.message.PartnerJoinNotification;
import org.greatfree.cry.framework.multisigned.message.PartnerLeaveNotification;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.messege.OwnerInfo;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
class Server
{
	private Peer peer;
	
	private Server()
	{
	}

	private static Server instance = new Server();
	
	public static Server RSC()
	{
		if (instance == null)
		{
			instance = new Server();
			return instance;
		}
		else
  		{
			return instance;
		}
	}

	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}

	public void start(String machineName, int machinePort, String registryIP, int registryPort, boolean isPrivate, int ownersSize) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(machineName)
				.port(machinePort)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new Services())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(machineName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(isPrivate)
				.ownersSize(ownersSize)
				.build();

		this.peer.start();
	}

	public void notifyOwnerJoin(String participant) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, PublicKeyUnavailableException
	{
		Collection<OwnerInfo> owners = this.peer.getOwners();
		for (OwnerInfo entry : owners)
		{
			if (!entry.getOwnerName().equals(participant))
			{
				this.peer.syncNotifyAsymmetrically(entry.getOwnerName(), new PartnerJoinNotification(participant));
			}
		}
	}
	
	public void notifyOwnerLeave(String participant) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		Collection<OwnerInfo> owners = this.peer.getOwners();
		for (OwnerInfo entry : owners)
		{
			this.peer.syncNotifyAsymmetrically(entry.getOwnerName(), new PartnerLeaveNotification(participant));
		}
	}
}
