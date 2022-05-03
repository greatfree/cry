package org.greatfree.cry.framework.multicast.client;

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
import org.greatfree.cry.framework.multicast.MultiAppConfig;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 04/11/2022
 *
 */
final class ClientUI
{
	private Scanner in = new Scanner(System.in);

	private Peer peer;
	private String multicastRoot;
	
	private ClientUI()
	{
	}

	private static ClientUI instance = new ClientUI();
	
	public static ClientUI FRONT()
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

	public void init(String ownerName, String multicastRoot) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder()
				.peerName(ownerName)
				.port(MultiAppConfig.DEFAULT_CHILD_PORT)
				.registryServerIP(MultiAppConfig.REGISTRY_SERVER_IP)
				.registryServerPort(MultiAppConfig.REGISTRY_SERVER_PORT)
				.task(new ClientTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(CryptoConfig.RSA_LENGTH)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(CryptoConfig.SYMMETRIC_KEY_LENGTH)
				.symIVKeyLength(CryptoConfig.SYMMETRIC_IV_KEY_LENGTH)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(ownerName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(true)
				.build();
		this.peer.start();
		this.multicastRoot = multicastRoot;
	}

	/*
	public void inviteRoot() throws InvalidKeyException, NoSuchAlgorithmException, ClassNotFoundException, SignatureException, RemoteReadException, IOException, DistributedNodeFailedException, CryptographyMismatchException
	{
		this.peer.inviteAsymPartner(this.multicastRoot);
	}
	*/
	
	public void notify(Notification notification, int cryptoOption) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException
	{
		this.peer.syncCryptoNotifyByName(this.multicastRoot, notification, cryptoOption);
	}

	/*
	public void notifyAsymmetrically(Notification notification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, RemoteReadException, IOException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		this.peer.syncNotifyAsymmetrically(this.multicastRoot, notification);
	}
	*/

	/*
	public ServerMessage readAsymmetrically(Request request) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, RemoteReadException, IOException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		return this.peer.readAsymmetrically(this.multicastRoot, request);
	}
	*/
	
	public ServerMessage read(Request request, int cryptoNotification) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, ShortBufferException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, OwnerCheatingException, CheatingException
	{
		return this.peer.cryptoReadByName(this.multicastRoot, request, cryptoNotification);
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
	
	public void send(int highOption) throws InvalidKeyException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, IOException, InterruptedException, RemoteReadException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SignatureException, SymmetricKeyUnavailableException, OwnerCheatingException, CheatingException
	{
		int option = MultiOptions.NO_OPTION;
		String optionStr;
		while (option != MultiOptions.QUIT)
		{
			MulticastUI.CLUSTER().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				if (option == MultiOptions.QUIT)
				{
					break;
				}
				System.out.println("Your choice: " + highOption);
				MulticastUI.CLUSTER().send(highOption, option);
			}
			catch (NumberFormatException e)
			{
				option = MultiOptions.NO_OPTION;
				System.out.println(MultiMenu.WRONG_OPTION);
			}
		}
	}
}
