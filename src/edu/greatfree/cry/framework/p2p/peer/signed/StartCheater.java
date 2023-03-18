package edu.greatfree.cry.framework.p2p.peer.signed;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.chat.MenuOptions;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.framework.p2p.peer.PeerMenu;
import edu.greatfree.cry.framework.p2p.peer.PeerTask;
import edu.greatfree.cry.server.Peer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 01/15/2022, Bing Li
 *
 */
class StartCheater
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, NoSuchAlgorithmException, SignatureException, InterruptedException, InvalidKeyException, CryptographyMismatchException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, PeerNotRegisteredException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		int option = MenuOptions.NO_OPTION;

		System.out.println("Tell me your name: ");
		String yourName = Tools.INPUT.nextLine();
		
		System.out.println("Tell me the person you attempt to pretend: ");
		String pretendedPerson = Tools.INPUT.nextLine();
		
		System.out.println("Tell me your partner you trust and invite: ");
		String partnerName = Tools.INPUT.nextLine();

		Peer peer = new Peer.PeerBuilder()
				.peerName(yourName)
				.port(8944)
//				.registryServerIP("192.168.1.18")
				.registryServerIP("127.0.0.1")
				.registryServerPort(8941)
				.task(new PeerTask())
				.isRegistryNeeded(true)
				// For updates in later versions, the option is required to be set to avoid exceptions. 02/05/2023, Bing Li
				.isAsymCryptography(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(4096)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(256)
				.symIVKeyLength(128)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				// It cannot construct a conversation with the partner. 01/15/2022, Bing Li
				.signature(pretendedPerson + CryConfig.SIGNATURE_SUFFIX)
				.build();

		peer.start();
		
		System.out.println("Are you ready? (Press Enter to continue) ...");
		Tools.INPUT.nextLine();

		try
		{
			peer.inviteAsymPartner(partnerName);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			PeerUI.SIGNED().printMenu();
			optionStr = Tools.INPUT.nextLine();
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
			catch (PeerNameIsNullException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
	}

}
