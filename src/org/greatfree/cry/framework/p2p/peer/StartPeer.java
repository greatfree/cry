package org.greatfree.cry.framework.p2p.peer;

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
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 01/11/2022, Bing Li
 *
 */
class StartPeer
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		int option = MenuOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your name: ");
		String yourName = in.nextLine();
		System.out.println("Tell me your partner's name: ");
		String partnerName = in.nextLine();

		Peer peer = new Peer.PeerBuilder()
				.peerName(yourName)
				.port(8944)
				.registryServerIP("192.168.1.18")
				.registryServerPort(8941)
				.task(new PeerTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				// If the length is too short, the private/public key cannot be encrypted properly. 01/12/2022, Bing Li 
//				.asymCipherKeyLength(2048)
				.asymCipherKeyLength(4096)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(256)
				.symIVKeyLength(128)
				.build();

		peer.start();
		
		System.out.println("Are you ready? (Press Enter to continue) ...");
		in.nextLine();
		
		peer.inviteAsymPartner(partnerName);

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			PeerUI.CRY().printMenu();
			optionStr = in.nextLine();
			option = Integer.parseInt(optionStr);
			System.out.println("Your choice: " + option);
			try
			{
				PeerUI.CRY().send(peer, partnerName, option);
			}
			catch (InvalidKeyException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
					| IllegalBlockSizeException | BadPaddingException | RemoteReadException | IOException
					| InterruptedException | NumberFormatException | InvalidAlgorithmParameterException | ShortBufferException | PublicKeyUnavailableException e)
			{
//				option = MenuOptions.NO_OPTION;
//				System.out.println(ClientMenu.WRONG_OPTION);
				e.printStackTrace();
			}
		}

		peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}

}
