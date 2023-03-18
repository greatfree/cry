package edu.greatfree.cry.framework.p2p.peer.total;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import edu.greatfree.cry.CryConfig;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
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
 * @author Bing Li
 * 
 * 02/04/2022, Bing Li
 *
 */
class StartPeer
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException, SignatureException, InterruptedException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, PeerNotRegisteredException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		int option = CryOptions.NO_OPTION;

		System.out.println("Tell me your name: ");
		String yourName = Tools.INPUT.nextLine();
		System.out.println("Tell me your partner's name: ");
		String partnerName = Tools.INPUT.nextLine();

		Peer peer = new Peer.PeerBuilder()
				.peerName(yourName)
				.port(8944)
//				.registryServerIP("192.168.3.8")
//				.registryServerIP("192.168.1.18")
				.registryServerIP("127.0.0.1")
				.registryServerPort(8941)
				.task(new PeerTask())
				.isRegistryNeeded(true)
				.asymCipherAlgorithm(CryConfig.RSA)
				.asymCipherKeyLength(4096)
				.symCipherAlgorithm(CryConfig.AES)
				.symCipherSpec(CryConfig.AES_SPEC)
				.symCipherKeyLength(256)
				.symIVKeyLength(128)
				.signatureAlgorithm(CryConfig.SHA_WITH_RSA)
				.signature(yourName + CryConfig.SIGNATURE_SUFFIX)
				.isAsymCryptography(false)
				.build();

		peer.start();

		System.out.println("Are you ready? (Press Enter to continue) ...");
		Tools.INPUT.nextLine();
		
		String optionStr;
		while (option != CryOptions.QUIT)
		{
			CryPeerUI.CRY().printMenu();
			optionStr = Tools.INPUT.nextLine();
			try
			{
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				CryPeerUI.CRY().send(peer, partnerName, option);
			}
			catch (NumberFormatException e)
			{
				option = CryOptions.NO_OPTION;
				System.out.println(Menu.WRONG_OPTION);
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
