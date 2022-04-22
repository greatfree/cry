package org.greatfree.cry.framework.p2p.peer.signed;

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

import org.greatfree.chat.MenuOptions;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.p2p.peer.PeerMenu;
import org.greatfree.cry.framework.p2p.peer.PeerTask;
import org.greatfree.cry.framework.tncs.Config;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 01/15/2022, Bing Li
 *
 */
class StartPeer
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, NoSuchAlgorithmException, SignatureException, InterruptedException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your name: ");
		String yourName = in.nextLine();
		System.out.println("Tell me your partner you trust and invite: ");
		String partnerName = in.nextLine();

		Peer peer = new Peer.PeerBuilder()
				.peerName(yourName)
				.port(8944)
				.registryServerIP("192.168.1.18")
				.registryServerPort(8941)
				.task(new PeerTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(Config.RSA)
				.asymCipherKeyLength(4096)
				.symCipherAlgorithm(Config.AES)
				.symCipherSpec(Config.AES_SPEC)
				.symCipherKeyLength(256)
				.symIVKeyLength(128)
				.signatureAlgorithm(Config.SHA_WITH_RSA)
				.signature(yourName + Config.SIGNATURE_SUFFIX)
				.build();

		peer.start();
		
		System.out.println("Are you ready? (Press Enter to continue) ...");
		in.nextLine();
		
		peer.inviteAsymPartner(partnerName);

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			PeerUI.SIGNED().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);
			try
			{
				PeerUI.SIGNED().send(peer, partnerName, option);
			}
			catch (NumberFormatException | PublicKeyUnavailableException e)
			{
				option = MenuOptions.NO_OPTION;
				System.out.println(PeerMenu.WRONG_OPTION);
			}
		}

		peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}

}
