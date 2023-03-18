package edu.greatfree.cry.framework.p2p.peer.total;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import edu.greatfree.cry.exceptions.CheatingException;
import edu.greatfree.cry.exceptions.CryptographyMismatchException;
import edu.greatfree.cry.exceptions.OwnerCheatingException;
import edu.greatfree.cry.exceptions.PublicKeyUnavailableException;
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
import edu.greatfree.cry.framework.p2p.message.Greetings;
import edu.greatfree.cry.framework.p2p.message.PeerNotification;
import edu.greatfree.cry.framework.p2p.message.PeerRequest;
import edu.greatfree.cry.framework.p2p.message.PeerResponse;
import edu.greatfree.cry.server.Peer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

/**
 * 
 * @author Bing Li
 * 
 * 02/04/2022
 *
 */
class CryPeerUI
{
	private CryPeerUI()
	{
	}

	private static CryPeerUI instance = new CryPeerUI();
	
	public static CryPeerUI CRY()
	{
		if (instance == null)
		{
			instance = new CryPeerUI();
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
		System.out.println(Menu.MENU_HEAD);

		System.out.println(Menu.PLAIN_NOTIFY);
		System.out.println(Menu.PLAIN_READ);
		System.out.println(Menu.SYM_NOTIFY);
		System.out.println(Menu.SYM_READ);
		System.out.println(Menu.ASYM_NOTIFY);
		System.out.println(Menu.ASYM_READ);
		System.out.println(Menu.SIGN_NOTIFY);
		System.out.println(Menu.SIGN_READ);
		
		System.out.println(Menu.QUIT);
		System.out.println(Menu.MENU_TAIL);
		System.out.println(Menu.INPUT_PROMPT);
	}

	public void send(Peer peer, String partnerName, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, RemoteReadException, IOException, InterruptedException, InvalidAlgorithmParameterException, ShortBufferException, CryptographyMismatchException, DistributedNodeFailedException, SignatureException, PeerNotRegisteredException, RemoteIPNotExistedException, PeerNameIsNullException
	{
		String notification;
		String request;
		PeerResponse response;
		Date startTime, endTime;
		switch (option)
		{
			case CryOptions.PLAIN_NOTIFY:
				System.out.println("Type your notification below: ...");
				notification = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					peer.syncNotify(partnerName, new PeerNotification(new Greetings(peer.getPeerName(), 100, notification)));
					endTime = Calendar.getInstance().getTime();
					System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.PLAIN_NOTIFY);
					System.out.println("Type your notification below again or exit: ...");
					notification = Tools.INPUT.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case CryOptions.PLAIN_READ:
				System.out.println("Type your request below: ...");
				request = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					response = (PeerResponse)peer.read(partnerName, new PeerRequest(new Greetings(peer.getPeerName(), 100, request)));
					endTime = Calendar.getInstance().getTime();
					System.out.println("Response = " + response.getMessage());
					System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.PLAIN_READ);
					System.out.println("Type your notification below again or exit: ...");
					request = Tools.INPUT.nextLine();
				}
				while (!request.equals("exit"));
				break;
				
			case CryOptions.SYM_NOTIFY:
				peer.resetCrypto(false);
				peer.inviteSymPartner(partnerName);
				System.out.println("Type your notification below: ...");
				notification = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					try
					{
						peer.syncNotifySymmetrically(partnerName, new PeerNotification(new Greetings(peer.getPeerName(), 100, notification)));
					}
					catch (SymmetricKeyUnavailableException e)
					{
						e.printStackTrace();
					}
					endTime = Calendar.getInstance().getTime();
					System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.SYM_NOTIFY);
					System.out.println("Type your notification below again or exit: ...");
					notification = Tools.INPUT.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case CryOptions.SYM_READ:
				peer.resetCrypto(false);
				peer.inviteSymPartner(partnerName);
				System.out.println("Type your request below: ...");
				request = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					try
					{
						response = (PeerResponse)peer.readSymmetrically(partnerName, new PeerRequest(new Greetings(peer.getPeerName(), 100, request)));
						endTime = Calendar.getInstance().getTime();
						System.out.println("Response = " + response.getMessage());
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.SYM_READ);
					}
					catch (SymmetricKeyUnavailableException e)
					{
						e.printStackTrace();
					}
					System.out.println("Type your notification below again or exit: ...");
					request = Tools.INPUT.nextLine();
				}
				while (!request.equals("exit"));
				break;
				
			case CryOptions.ASYM_NOTIFY:
				peer.resetCrypto(true);
				peer.inviteAsymPartner(partnerName);
				System.out.println("Type your notification below: ...");
				notification = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					try
					{
						peer.syncNotifyAsymmetrically(partnerName, new PeerNotification(new Greetings(peer.getPeerName(), 100, notification)));
					}
					catch (PublicKeyUnavailableException e)
					{
						e.printStackTrace();
					}
					endTime = Calendar.getInstance().getTime();
					System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.ASYM_NOTIFY);
					System.out.println("Type your notification below again or exit: ...");
					notification = Tools.INPUT.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case CryOptions.ASYM_READ:
				peer.resetCrypto(true);
				peer.inviteAsymPartner(partnerName);
				System.out.println("Type your request below: ...");
				request = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					try
					{
						response = (PeerResponse)peer.readAsymmetrically(partnerName, new PeerRequest(new Greetings(peer.getPeerName(), 100, request)));
						endTime = Calendar.getInstance().getTime();
						System.out.println("Response = " + response.getMessage());
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.ASYM_READ);
					}
					catch (PublicKeyUnavailableException e)
					{
						e.printStackTrace();
					}
					System.out.println("Type your notification below again or exit: ...");
					request = Tools.INPUT.nextLine();
				}
				while (!request.equals("exit"));
				break;
				
			case CryOptions.SIGN_NOTIFY:
				peer.resetCrypto(true);
				peer.inviteAsymPartner(partnerName);
				System.out.println("Type your notification below: ...");
				notification = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					try
					{
						peer.syncNotifyBySignature(partnerName, new PeerNotification(new Greetings(peer.getPeerName(), 100, notification)));
					}
					catch (PublicKeyUnavailableException e)
					{
						e.printStackTrace();
					}
					endTime = Calendar.getInstance().getTime();
					System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.SIGN_NOTIFY);
					System.out.println("Type your notification below again or exit: ...");
					notification = Tools.INPUT.nextLine();
				}
				while (!notification.equals("exit"));
				break;
				
			case CryOptions.SIGN_READ:
				peer.resetCrypto(true);
				peer.inviteAsymPartner(partnerName);
				System.out.println("Type your request below: ...");
				request = Tools.INPUT.nextLine();
				do
				{
					startTime = Calendar.getInstance().getTime();
					try
					{
						response = (PeerResponse)peer.readBySignature(partnerName, new PeerRequest(new Greetings(peer.getPeerName(), 100, request)));
						endTime = Calendar.getInstance().getTime();
						System.out.println("Response = " + response.getMessage());
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to do " + Menu.SIGN_READ);
					}
					catch (OwnerCheatingException | CheatingException | PublicKeyUnavailableException e)
					{
						System.out.println("You are cheating as the owner, " + peer.getPeerName());
					}
					System.out.println("Type your notification below again or exit: ...");
					request = Tools.INPUT.nextLine();
				}
				while (!request.equals("exit"));
				break;
		}
	}

}
