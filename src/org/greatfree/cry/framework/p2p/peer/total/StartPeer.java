package org.greatfree.cry.framework.p2p.peer.total;

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

import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.p2p.peer.PeerTask;
import org.greatfree.cry.framework.tncs.CryptoConfig;
import org.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author Bing Li
 * 
 * 02/04/2022, Bing Li
 *
 */
class StartPeer
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException
	{
		int option = CryOptions.NO_OPTION;
		Scanner in = new Scanner(System.in);

		System.out.println("Tell me your name: ");
		String yourName = in.nextLine();
		System.out.println("Tell me your partner's name: ");
		String partnerName = in.nextLine();

		Peer peer = new Peer.PeerBuilder()
				.peerName(yourName)
				.port(8944)
//				.registryServerIP("192.168.3.8")
				.registryServerIP("192.168.1.18")
				.registryServerPort(8941)
				.task(new PeerTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryptoConfig.RSA)
				.asymCipherKeyLength(4096)
				.symCipherAlgorithm(CryptoConfig.AES)
				.symCipherSpec(CryptoConfig.AES_SPEC)
				.symCipherKeyLength(256)
				.symIVKeyLength(128)
				.signatureAlgorithm(CryptoConfig.SHA_WITH_RSA)
				.signature(yourName + CryptoConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(false)
				.build();

		peer.start();

		System.out.println("Are you ready? (Press Enter to continue) ...");
		in.nextLine();

		String optionStr;
		while (option != CryOptions.QUIT)
		{
			CryPeerUI.CRY().printMenu();
			optionStr = in.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				CryPeerUI.CRY().send(peer, partnerName, option);
			}
			catch (NumberFormatException e)
			{
				option = CryOptions.NO_OPTION;
				System.out.println(MenuPresentations.WRONG_OPTION);
			}
		}
		
		peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		in.close();
	}
}
