package edu.greatfree.cry.framework.ownership.machine;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;

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

	public void stop() throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
	
	public void start(String machineName, int machinePort, String registryIP, int registryPort, boolean isPrivate, int ownersSize) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(machineName)
				.port(machinePort)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.task(new MachineTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(machineName + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.isPrivate(isPrivate)
				.ownersSize(ownersSize)
				.build();

		this.peer.start();
	}
}
