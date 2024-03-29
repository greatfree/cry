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

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.framework.p2p.message.Greetings;
import edu.greatfree.cry.framework.p2p.message.PeerNotification;
import edu.greatfree.cry.framework.p2p.message.PeerRequest;
import edu.greatfree.cry.framework.p2p.message.PeerResponse;
import edu.greatfree.cry.framework.p2p.peer.MenuOptions;
import edu.greatfree.cry.framework.p2p.peer.PeerMenu;
import edu.greatfree.cry.server.Peer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 01/15/2022, Bing Li
 *
 */
class PeerUI
{
	private PeerUI()
	{
	}

	private static PeerUI instance = new PeerUI();
	
	public static PeerUI SIGNED()
	{
		if (instance == null)
		{
			instance = new PeerUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
	}

	public void printMenu()
	{
		System.out.println(PeerMenu.MENU_HEAD);
		System.out.println(PeerMenu.TYPE_NOTIFICATION);
		System.out.println(PeerMenu.TYPE_REQUEST);
		System.out.println(PeerMenu.CHANGE_SIGNATURE_UNILATERALLY);
		System.out.println(PeerMenu.QUIT);
		System.out.println(PeerMenu.MENU_TAIL);
		System.out.println(PeerMenu.INPUT_PROMPT);
	}

	public void send(Peer peer, String partnerName, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		switch (option)
		{
			case MenuOptions.TYPE_NOTIFICATION:
				System.out.println("Type your notification below: ...");
				String notification = Tools.INPUT.nextLine();
				do
				{
					peer.syncNotifyBySignature(partnerName, new PeerNotification(new Greetings(peer.getPeerName(), 100, notification)));
					System.out.println("Type your notification below again: ...");
					notification = Tools.INPUT.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case MenuOptions.TYPE_REQUEST:
				System.out.println("Type your request below: ...");
				String request = Tools.INPUT.nextLine();
				PeerResponse response;
				do
				{
					try
					{
						response = (PeerResponse)peer.readBySignature(partnerName, new PeerRequest(new Greetings(peer.getPeerName(), 100, request)));
						if (response != null)
						{
							System.out.println("Response = " + response.getMessage());
						}
						else
						{
							System.out.println("No Response!");
						}
					}
					catch (OwnerCheatingException | CheatingException e)
					{
						System.out.println("You are cheating as the owner, " + peer.getPeerName());
					}
					System.out.println("Type your request below again: ...");
					request = Tools.INPUT.nextLine();
				}
				while (!request.equals("exit"));
				break;

			case MenuOptions.CHANGE_SIGNATURE_UNILATERALLY:
				System.out.println("Type your new signature below: ...");
				String signature = Tools.INPUT.nextLine();
				peer.resetSignature(signature);
				System.out.println("Your signature is changed to " + signature);
				break;
		}
	}
}
