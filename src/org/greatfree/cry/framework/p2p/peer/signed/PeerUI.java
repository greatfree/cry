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

import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.framework.p2p.message.Greetings;
import org.greatfree.cry.framework.p2p.message.PeerNotification;
import org.greatfree.cry.framework.p2p.message.PeerRequest;
import org.greatfree.cry.framework.p2p.message.PeerResponse;
import org.greatfree.cry.framework.p2p.peer.MenuOptions;
import org.greatfree.cry.framework.p2p.peer.PeerMenu;
import org.greatfree.cry.server.Peer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/**
 * 
 * @author libing
 * 
 * 01/15/2022, Bing Li
 *
 */
class PeerUI
{
	private Scanner in = new Scanner(System.in);

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
		this.in.close();
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

	public void send(Peer peer, String partnerName, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException, RemoteReadException, IOException, InterruptedException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, PublicKeyUnavailableException
	{
		switch (option)
		{
			case MenuOptions.TYPE_NOTIFICATION:
				System.out.println("Type your notification below: ...");
				String notification = in.nextLine();
				do
				{
					peer.syncNotifyBySignature(partnerName, new PeerNotification(new Greetings(peer.getPeerName(), 100, notification)));
					System.out.println("Type your notification below again: ...");
					notification = in.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case MenuOptions.TYPE_REQUEST:
				System.out.println("Type your request below: ...");
				String request = in.nextLine();
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
					request = in.nextLine();
				}
				while (!request.equals("exit"));
				break;

			case MenuOptions.CHANGE_SIGNATURE_UNILATERALLY:
				System.out.println("Type your new signature below: ...");
				String signature = in.nextLine();
				peer.resetSignature(signature);
				System.out.println("Your signature is changed to " + signature);
				break;
		}
	}
}
