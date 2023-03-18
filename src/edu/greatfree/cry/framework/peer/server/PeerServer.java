package edu.greatfree.cry.framework.peer.server;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.p2p.peer.PeerTask;
import edu.greatfree.cry.server.Peer;

/**
 * 
 * A peer-based server. The server is implemented by Peer, but the client-side is disabled. 03/02/2023, Bing Li
 * 
 * @author libing
 * 
 * 03/02/2023
 *
 */
final class PeerServer
{
	private Peer peer;

	private PeerServer()
	{
	}

	private static PeerServer instance = new PeerServer();
	
	public static PeerServer PEER()
	{
		if (instance == null)
		{
			instance = new PeerServer();
			return instance;
		}
		else
  		{
			return instance;
		}
	}

	public void stop() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, IOException, PeerNameIsNullException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
	
	public void start(String name, int port, String registryIP, int registryPort, boolean isPrivate, int ownersSize) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(name)
				.port(port)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new PeerTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(name + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(isPrivate)
				.ownersSize(ownersSize)
//				.isServerDisabled(false)
//				.isClientDisabled(true)
				.build();
		this.peer.start();
	}
}
