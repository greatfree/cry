package org.greatfree.cry.framework.cluster.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.cluster.ClusterConfig;
import org.greatfree.cry.framework.multicast.client.MultiMenu;
import org.greatfree.cry.framework.multicast.client.MultiOptions;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClientUI
{
	private Scanner in = new Scanner(System.in);

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

	public void dispose() throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		this.in.close();
	}

	public void init(String clientName, String clusterRoot) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(clientName)
				.port(ClusterConfig.CLIENT_PORT)
				.registryServerIP(ClusterConfig.REGISTRY_IP)
				.registryServerPort(ClusterConfig.REGISTRY_PORT)
				.task(new ClusterClientTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(clientName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.clusterRoot = clusterRoot;
	}
	
	public void notify(ClusterNotification notification, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.peer.syncCryptoNotifyByName(this.clusterRoot, notification, cryptoOption);
	}

	public ServerMessage read(ClusterRequest request, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException
	{
		return this.peer.cryptoReadByName(this.clusterRoot, request, cryptoOption);
	}

	public void printMenu()
	{
		System.out.println(MultiMenu.MENU_HEAD);

		System.out.println(MultiMenu.BROADCAST_NOTIFICATION);
		System.out.println(MultiMenu.ANYCAST_NOTIFICATION);
		System.out.println(MultiMenu.UNICAST_NOTIFICATION);
		System.out.println(MultiMenu.BROADCAST_REQUEST);
		System.out.println(MultiMenu.ANYCAST_REQUEST);
		System.out.println(MultiMenu.UNICAST_REQUEST);
		System.out.println(MultiMenu.DELIMIT);

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

		System.out.println(MultiMenu.QUIT);
		System.out.println(MultiMenu.MENU_TAIL);
	}
	
	public void send(int highOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, ShortBufferException, OwnerCheatingException, CheatingException
	{
		int option = MultiOptions.NO_OPTION;
		String optionStr;
		while (option != MultiOptions.QUIT)
		{
			ClusterUI.CRY().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				if (option == MultiOptions.QUIT)
				{
					break;
				}
				System.out.println("Your choice: " + highOption);
				ClusterUI.CRY().send(highOption, option);
			}
			catch (NumberFormatException e)
			{
				option = MultiOptions.NO_OPTION;
				System.out.println(MultiMenu.WRONG_OPTION);
			}
		}
	}
}
