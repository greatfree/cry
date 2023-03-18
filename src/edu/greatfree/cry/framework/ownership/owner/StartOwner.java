package edu.greatfree.cry.framework.ownership.owner;

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
import edu.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import edu.greatfree.cry.exceptions.PeerNotRegisteredException;
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
 * 03/19/2022
 *
 */
class StartOwner
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, DistributedNodeFailedException, CryptographyMismatchException, InterruptedException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, PublicKeyUnavailableException, SymmetricKeyUnavailableException, IOException, PeerNotRegisteredException, ShortBufferException, OwnerCheatingException, CheatingException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		int option = OwnerOptions.NO_OPTION;

		System.out.println("Tell me your name: ");
		String nodeName = Tools.INPUT.nextLine();
		System.out.println("Tell me your machine's name: ");
		String machineName = Tools.INPUT.nextLine();
		System.out.println("Tell me the owner's name for the machine: ");
		String yourName = Tools.INPUT.nextLine();

		try
		{
			Owner.RSC().start(nodeName, yourName, nodeName, machineName);
			Owner.RSC().inviteMachine();
			
			String optionStr;
			while (option != OwnerOptions.QUIT)
			{
				OwnerUI.RSC().printMenu();
				optionStr = Tools.INPUT.nextLine();
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);

				try
				{
					OwnerUI.RSC().send(option);
				}
				catch (NumberFormatException e)
				{
					option = OwnerOptions.NO_OPTION;
					System.out.println(OwnerMenu.WRONG_OPTION);
				}
			}
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}

		try
		{
			Owner.RSC().stop();
		}
		catch (PeerNameIsNullException e)
		{
			e.printStackTrace();
		}
		System.out.println("Owner quit!");
	}
}
