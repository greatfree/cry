package org.greatfree.cry.framework.ownership.machine;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.tncs.Config;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 03/20/2022
 *
 */
class Machine
{
	private Peer peer;
	
	private Machine()
	{
	}

	private static Machine instance = new Machine();
	
	public static Machine RSC()
	{
		if (instance == null)
		{
			instance = new Machine();
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
				.task(new MachineTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(Config.RSA)
				.asymCipherKeyLength(BlockConfig.RSA_LENGTH)
				.symCipherAlgorithm(Config.AES)
				.symCipherSpec(Config.AES_SPEC)
				.symCipherKeyLength(BlockConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(BlockConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(Config.SHA_WITH_RSA)
				.signature(machineName + Config.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(isPrivate)
				.ownersSize(ownersSize)
				.build();

		this.peer.start();
	}
}
