package edu.greatfree.cry.framework.p2p.peer;

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
 * 01/11/2022, Bing Li
 *
 */
class StartPeer
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, DistributedNodeFailedException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, PeerNotRegisteredException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, ShortBufferException
	{
		int option = MenuOptions.NO_OPTION;

		System.out.println("Tell me your name: ");
		String yourName = Tools.INPUT.nextLine();
		System.out.println("Tell me your partner's name: ");
		String partnerName = Tools.INPUT.nextLine();

		Peer peer = new Peer.PeerBuilder()
				.peerName(yourName)
				.port(8944)
				.registryServerIP("127.0.0.1")
				.registryServerPort(8941)
				.task(new PeerTask())
				.isRegistryNeeded(true)

				// For updates in later versions, the option is required to be set to avoid exceptions. 02/05/2023, Bing Li
				.isAsymCryptography(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				// If the length is too short, the private/public key cannot be encrypted properly. 01/12/2022, Bing Li 
//				.asymCipherKeyLength(2048)
				.asymCipherKeyLength(4096)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(256)
				.symIVKeyLength(128)

				// For updates in later versions, the option is required to be set to avoid exceptions. 02/05/2023, Bing Li
//				.signature(yourName)
				.build();

		peer.start();
		
		System.out.println("Are you ready? (Press Enter to continue) ...");

		try
		{
			peer.inviteAsymPartner(partnerName);
		}
		catch (CryptographyMismatchException e)
		{
			System.out.println(e);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}

		String optionStr;
		while (option != MenuOptions.QUIT)
		{
			PeerUI.CRY().printMenu();
			try
			{
				optionStr = Tools.INPUT.nextLine();
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				PeerUI.CRY().send(peer, partnerName, option);
			}
			catch (CryptographyMismatchException | NumberFormatException e)
			{
//				option = MenuOptions.NO_OPTION;
//				System.out.println(ClientMenu.WRONG_OPTION);
				System.out.println(e);
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
		catch (CryptographyMismatchException e)
		{
			System.out.println(e);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
	}

}
