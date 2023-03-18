package edu.greatfree.cry.framework.ownership.owner;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
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

import edu.greatfree.cry.framework.ownership.OwnerConfig;
import edu.greatfree.cry.framework.ownership.message.ReadRequest;
import edu.greatfree.cry.framework.ownership.message.ReadResponse;
import edu.greatfree.cry.framework.ownership.message.StopPeerNotification;
import edu.greatfree.cry.framework.ownership.message.WriteNotification;

/**
 * 
 * @author libing
 * 
 * 03/19/2022
 *
 */
final class Owner
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.framework.ownership.owner");

	private Peer peer;
	private Profile profile;

	private Owner()
	{
	}
	
	private static Owner instance = new Owner();
	
	public static Owner RSC()
	{
		if (instance == null)
		{
			instance = new Owner();
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

	public void start(String peerName, String ownerName, String signature, String machineName) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(peerName)
				.port(OwnerConfig.MACHINE_PORT)
				.registryServerIP(OwnerConfig.REGISTRY_IP)
				.registryServerPort(OwnerConfig.REGISTRY_PORT)
				.task(new OwnerTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(signature + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.profile = new Profile(ownerName, machineName);
	}

	public void inviteMachine() throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.inviteAsymPartner(this.profile.getMachineName());
	}
	
	public boolean claimAsMachineOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, CryptographyMismatchException, RemoteReadException, IOException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return this.peer.claimOwner(this.profile.getOwner(), this.profile.getMachineName()).isSucceeded();
	}
	
	public boolean abandonOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return this.peer.abandonOwner(this.profile.getOwner(), this.profile.getMachineName()).isSucceeded();
	}
	
	public void write(String notification) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotify(this.profile.getMachineName(), new WriteNotification(notification));
	}
	
	public void writeSymmetrically(String notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, RemoteReadException, IOException, CryptographyMismatchException, InterruptedException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotifySymmetrically(this.profile.getMachineName(), new WriteNotification(notification));
	}
	
	public void writeAsymmetrically(String notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotifyAsymmetrically(this.profile.getMachineName(), new WriteNotification(notification));
	}
	
	public void writeBySignature(String notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotifyBySignature(this.profile.getMachineName(), new WriteNotification(notification));
	}

	public void writePrivately(String notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getMachineName(), new WriteNotification(notification));
	}
	
	public void stopMachinePrivately() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotifyPrivately(this.profile.getOwner(), this.profile.getMachineName(), new StopPeerNotification());
	}
	
	public void stopMachinePublicly() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncNotify(this.profile.getMachineName(), new StopPeerNotification());
	}

	public ReadResponse read(String request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return (ReadResponse)this.peer.read(this.profile.getMachineName(), new ReadRequest(request));
	}

	public ReadResponse readSymmetrically(String request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, SymmetricKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return (ReadResponse)this.peer.readSymmetrically(this.profile.getMachineName(), new ReadRequest(request));
	}

	public ReadResponse readAsymmetrically(String request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return (ReadResponse)this.peer.readAsymmetrically(this.profile.getMachineName(), new ReadRequest(request));
	}

	public ReadResponse readBySignature(String request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, OwnerCheatingException, CheatingException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		return (ReadResponse)this.peer.readBySignature(this.profile.getMachineName(), new ReadRequest(request));
	}

	public ReadResponse readPrivately(String request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		try
		{
			return (ReadResponse)this.peer.readPrivately(this.profile.getOwner(), this.profile.getMachineName(), new ReadRequest(request));
		}
		catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
		{
			log.info("You are cheating as the owner, " + this.profile.getOwner());
		}
		return null;
	}
	
	public String getMachineName()
	{
		return this.profile.getMachineName();
	}
}
