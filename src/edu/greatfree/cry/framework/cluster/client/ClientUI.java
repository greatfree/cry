package edu.greatfree.cry.framework.cluster.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.util.Tools;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.framework.cluster.ClusterConfig;
import edu.greatfree.cry.messege.multicast.ClusterNotification;
import edu.greatfree.cry.messege.multicast.ClusterRequest;
import edu.greatfree.cry.server.Peer;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClientUI
{
	private Peer peer;
	private String clusterRoot;

	private ClientUI()
	{
	}

	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CRY()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}

	public void init(String clientName, String clusterRoot) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(clientName)
				.port(ClusterConfig.CLIENT_PORT)
				.registryServerIP(ClusterConfig.REGISTRY_IP)
				.registryServerPort(ClusterConfig.REGISTRY_PORT)
				.task(new ClusterClientTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(CryConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(CryConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(clientName + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.clusterRoot = clusterRoot;
	}
	
	public void notify(ClusterNotification notification, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		this.peer.syncCryptoNotifyByName(this.clusterRoot, notification, cryptoOption);
		this.peer.syncPrmNotifyByName(this.clusterRoot, notification, cryptoOption);
	}
	
	public void notify(IntercastNotification notification, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.syncPrmNotifyByName(this.clusterRoot, notification, cryptoOption);
	}

	public ServerMessage read(ClusterRequest request, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		return this.peer.cryptoReadByName(this.clusterRoot, request, cryptoOption);
		return this.peer.prmReadByName(this.clusterRoot, request, cryptoOption);
	}

	public ServerMessage read(IntercastRequest request, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
//		return this.peer.cryptoReadByName(this.clusterRoot, request, cryptoOption);
		return this.peer.prmReadByName(this.clusterRoot, request, cryptoOption);
	}

	public boolean claimOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.inviteAsymPartner(this.clusterRoot);
		return this.peer.claimOwner(this.peer.getPeerName(), this.clusterRoot).isSucceeded();
	}
	
	public boolean abandonOwner() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, CheatingException, PublicKeyUnavailableException, DistributedNodeFailedException, CryptographyMismatchException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		this.peer.inviteAsymPartner(this.clusterRoot);
		return this.peer.abandonOwner(this.peer.getPeerName(), this.clusterRoot).isSucceeded();
	}

	public void printMenu()
	{
		System.out.println(ClusterMenu.MENU_HEAD);

		System.out.println(ClusterMenu.BROADCAST_NOTIFICATION);
		System.out.println(ClusterMenu.ANYCAST_NOTIFICATION);
		System.out.println(ClusterMenu.UNICAST_NOTIFICATION);
		System.out.println(ClusterMenu.BROADCAST_REQUEST);
		System.out.println(ClusterMenu.ANYCAST_REQUEST);
		System.out.println(ClusterMenu.UNICAST_REQUEST);
		System.out.println(ClusterMenu.DELIMIT);

		/*
		System.out.println(MultiMenu.SYM_BROADCAST_NOTIFICATION);
		System.out.println(MultiMenu.SYM_ANYCAST_NOTIFICATION);
		System.out.println(MultiMenu.SYM_UNICAST_NOTIFICATION);
		System.out.println(MultiMenu.SYM_BROADCAST_REQUEST);
		System.out.println(MultiMenu.SYM_ANYCAST_REQUEST);
		System.out.println(MultiMenu.SYM_UNICAST_REQUEST);
		System.out.println(MultiMenu.DELIMIT);

		System.out.println(MultiMenu.ASYM_BROADCAST_NOTIFICATION);
		System.out.println(MultiMenu.ASYM_ANYCAST_NOTIFICATION);
		System.out.println(MultiMenu.ASYM_UNICAST_NOTIFICATION);
		System.out.println(MultiMenu.ASYM_BROADCAST_REQUEST);
		System.out.println(MultiMenu.ASYM_ANYCAST_REQUEST);
		System.out.println(MultiMenu.ASYM_UNICAST_REQUEST);
		System.out.println(MultiMenu.DELIMIT);

		System.out.println(MultiMenu.SIGNED_BROADCAST_NOTIFICATION);
		System.out.println(MultiMenu.SIGNED_ANYCAST_NOTIFICATION);
		System.out.println(MultiMenu.SIGNED_UNICAST_NOTIFICATION);
		System.out.println(MultiMenu.SIGNED_BROADCAST_REQUEST);
		System.out.println(MultiMenu.SIGNED_ANYCAST_REQUEST);
		System.out.println(MultiMenu.SIGNED_UNICAST_REQUEST);
		System.out.println(MultiMenu.DELIMIT);

		System.out.println(MultiMenu.CLAIM_OWNERSHIP);
		System.out.println(MultiMenu.ABANDON_OWNERSHIP);
		System.out.println(MultiMenu.DELIMIT);

		System.out.println(MultiMenu.PRIVATE_BROADCAST_NOTIFICATION);
		System.out.println(MultiMenu.PRIVATE_ANYCAST_NOTIFICATION);
		System.out.println(MultiMenu.PRIVATE_UNICAST_NOTIFICATION);
		System.out.println(MultiMenu.PRIVATE_BROADCAST_REQUEST);
		System.out.println(MultiMenu.PRIVATE_ANYCAST_REQUEST);
		System.out.println(MultiMenu.PRIVATE_UNICAST_REQUEST);
		System.out.println(MultiMenu.DELIMIT);
		*/
		
		System.out.println(ClusterMenu.INTER_BROADCAST_NOTIFICATION);
		System.out.println(ClusterMenu.INTER_ANYCAST_NOTIFICATION);
		System.out.println(ClusterMenu.INTER_UNICAST_NOTIFICATION);
		System.out.println(ClusterMenu.INTER_BROADCAST_REQUEST);
		System.out.println(ClusterMenu.INTER_ANYCAST_REQUEST);
		System.out.println(ClusterMenu.INTER_UNICAST_REQUEST);
		System.out.println(ClusterMenu.DELIMIT);

		System.out.println(ClusterMenu.CLAIM_OWNERSHIP);
		System.out.println(ClusterMenu.ABANDON_OWNERSHIP);
		System.out.println(ClusterMenu.DELIMIT);
		
		System.out.println(ClusterMenu.QUIT);
		System.out.println(ClusterMenu.MENU_TAIL);
	}
	
	public void send(int highOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, ShortBufferException, OwnerCheatingException, CheatingException, PeerNotRegisteredException, RemoteIPNotExistedException
	{
		int option = ClusterOptions.NO_OPTION;
		String optionStr;
		while (option != ClusterOptions.QUIT)
		{
			if (highOption != ClusterOptions.CLAIM_OWNERSHIP && highOption != ClusterOptions.ABANDON_OWNERSHIP)
			{
				ClusterUI.CRY().printMenu();
				optionStr = Tools.INPUT.nextLine();
				try
				{
					option = Integer.parseInt(optionStr);
					if (option == ClusterOptions.QUIT)
					{
						break;
					}
					System.out.println("Your choice: " + highOption);
					ClusterUI.CRY().send(highOption, option);
				}
				catch (NumberFormatException e)
				{
					option = ClusterOptions.NO_OPTION;
					System.out.println(ClusterMenu.WRONG_OPTION);
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				try
				{
					ClusterUI.CRY().send(highOption, option);
				}
				catch (PeerNameIsNullException e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
