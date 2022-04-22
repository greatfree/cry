package org.greatfree.cry.framework.bitcoin.coordinator;

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
import org.greatfree.cry.framework.bitcoin.CoinConfig;
import org.greatfree.cry.framework.blockchain.BlockConfig;
import org.greatfree.cry.framework.tncs.Config;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author Bing Li
 * 
 * 02/14/2022
 *
 */
class Coordinator
{
	private Peer peer;
	
	private Coordinator()
	{
	}
	
	private static Coordinator instance = new Coordinator();
	
	public static Coordinator COIN()
	{
		if (instance == null)
		{
			instance = new Coordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		ChainBoss.COIN().dispose();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
	
	public void start(int port, String registryIP, int registryPort) throws IOException, ClassNotFoundException, RemoteReadException
	{
		ChainBoss.COIN().init();
		this.peer = new Peer.PeerBuilder()
				.peerName(CoinConfig.COIN_COORDINATOR)
				.port(port)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new CoordinationTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(Config.RSA)
				.asymCipherKeyLength(BlockConfig.RSA_LENGTH)
				.symCipherAlgorithm(Config.AES)
				.symCipherSpec(Config.AES_SPEC)
				.symCipherKeyLength(BlockConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(BlockConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(Config.SHA_WITH_RSA)
				.signature(CoinConfig.COIN_COORDINATOR + Config.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
	}

	/*
	public void notifyHead(String peerName) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException
	{
		this.peer.syncNotify(peerName, new HeadNotification());
	}
	*/

	/*
	 * The below code does not work since the validation is controlled by the networking head. 02/28/2022, Bing Li
	 */
	/*
	public void startValidate(String sessionKey) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		this.peer.syncNotifyBySignature(ChainBoss.COIN().getChainHead(sessionKey), new StartValidateNotification(sessionKey, ChainBoss.COIN().getChainLength()));
	}
	*/
	
}
